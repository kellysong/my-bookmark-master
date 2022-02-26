package com.sjl.bookmark.ui.presenter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Handler
import android.text.TextUtils
import android.util.Xml
import android.widget.Toast
import com.sjl.bookmark.R
import com.sjl.bookmark.entity.WifiInfo
import com.sjl.bookmark.ui.contract.WifiQueryContract
import com.sjl.core.net.RxSchedulers
import com.sjl.core.util.log.LogUtils
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import org.xmlpull.v1.XmlPullParser
import java.io.*
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename WifiQueryPresenter.java
 * @time 2018/3/3 15:58
 * @copyright(C) 2018 song
 */
class WifiQueryPresenter : WifiQueryContract.Presenter() {
    private var mWifiManager: WifiManager? = null
    private var mHandler: Handler? = null
    private var mOpenWifiRunnable: OpenWifiRunnable? = null

    /**
     * 读取wifi信息
     */
    override fun initWifiInfo() {
        Observable.create(object : ObservableOnSubscribe<Any> {
            // 第一步：初始化Observable
            @Throws(Exception::class)
            override fun subscribe(e: ObservableEmitter<Any>) {
                readWifiInfo(e)
                e.onComplete()
            }
        }).compose(RxSchedulers.applySchedulers()).`as`(bindLifecycle())
            .subscribe(object : Observer<Any> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(s: Any) {
                    if (s is String) {
                        EventBus.getDefault().post(s.toString())
                    } else if (s is List<*>) {
                        EventBus.getDefault().post(s as List<WifiInfo?>?)
                    }
                }

                override fun onError(e: Throwable) {
                    LogUtils.e("读取wifi密码异常", e)
                }

                override fun onComplete() {} // 第三步：订阅
            })
    }

    private fun readWifiInfo(e: ObservableEmitter<Any>) {
        var process: Process? = null
        var dataOutputStream: DataOutputStream? = null
        var dataInputStream: DataInputStream? = null
        val wifiConf: StringBuffer = StringBuffer()
        var wifiParseFlag: Boolean = false
        var error: Boolean = false
        try {
            process = Runtime.getRuntime().exec("su") ///(这里执行是系统已经开放了root权限，而不是说通过执行这句来获得root权限)
            dataOutputStream = DataOutputStream(process.getOutputStream())
            dataInputStream = DataInputStream(process.getInputStream())
            val sdkInt: Int = Build.VERSION.SDK_INT
            if (sdkInt >= Build.VERSION_CODES.O) {
                dataOutputStream.writeBytes("cat /data/misc/wifi/WifiConfigStore.xml\n")
                wifiParseFlag = true
            } else {
                dataOutputStream.writeBytes("cat /data/misc/wifi/wpa_supplicant.conf\n")
            }
            dataOutputStream.writeBytes("exit\n")
            dataOutputStream.flush()
            val inputStreamReader: InputStreamReader = InputStreamReader(dataInputStream, "UTF-8")
            val bufferedReader: BufferedReader = BufferedReader(inputStreamReader)
            var line: String? = null
            while ((bufferedReader.readLine().also({ line = it })) != null) {
                wifiConf.append(line)
            }
            bufferedReader.close()
            inputStreamReader.close()
            process.waitFor()
        } catch (e1: Exception) {
            LogUtils.e("读取wifi配置文件异常", e1)
            error = true
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close()
                }
                if (dataInputStream != null) {
                    dataInputStream.close()
                }
                if (process != null) {
                    process.destroy()
                }
            } catch (e2: IOException) {
                LogUtils.e("关闭读取wifi流异常", e2)
            }
        }
        if (error) {
            e.onNext(mContext.getString(R.string.no_root_permission))
            return
        }
        val wifiConfigStr: String = wifiConf.toString()
        if (TextUtils.isEmpty(wifiConfigStr)) {
            e.onNext(ArrayList<WifiInfo>())
            return
        }
        LogUtils.i("wifiConf:" + wifiConfigStr)
        val wifiInfoList: ArrayList<WifiInfo?> = ArrayList()
        if (!wifiParseFlag) {
            val network: Pattern = Pattern.compile("network=\\{([^\\}]+)\\}", Pattern.DOTALL)
            val networkMatcher: Matcher = network.matcher(wifiConfigStr)
            var wifiInfo: WifiInfo
            while (networkMatcher.find()) {
                val networkBlock: String = networkMatcher.group()
                LogUtils.i("wifi信息：" + networkBlock)
                val ssidMatcher: Matcher = REGEX_SSID.matcher(networkBlock)
                if (ssidMatcher.find()) {
                    wifiInfo = WifiInfo()
                    wifiInfo.name = ssidMatcher.group(1)
                    val pskMatcher: Matcher = REGEX_PSK.matcher(networkBlock)
                    if (pskMatcher.find()) {
                        wifiInfo.password = pskMatcher.group(1)
                    } else {
                        wifiInfo.password = mContext.getString(R.string.empty_password)
                    }
                    val keyMatcher: Matcher = REGEX_KEY_MGMT.matcher(networkBlock)
                    if (keyMatcher.find()) {
                        wifiInfo.encryptType = keyMatcher.group(1)
                    } else {
                        wifiInfo.encryptType = mContext.getString(R.string.unknown)
                    }
                    wifiInfoList.add(wifiInfo)
                }
            }
        } else {
            try {
                //https://blog.csdn.net/csdn_of_coder/article/details/73380495
                val parser: XmlPullParser = Xml.newPullParser()
                parser.setInput(StringReader(wifiConfigStr))
                var eventType: Int = parser.eventType
                var wifiInfo: WifiInfo? = null
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    when (eventType) {
                        XmlPullParser.START_DOCUMENT -> {}
                        XmlPullParser.START_TAG -> {
                            val name: String = parser.name
                            if ("Network".equals(name, ignoreCase = true)) {
                                wifiInfo = WifiInfo()
                                wifiInfo.setPassword("--")
                            }
                            if ("WifiConfiguration".equals(name, ignoreCase = true)) {
                                parserWifiConfig(wifiInfo, parser)
                            }
                            if ("WifiEnterpriseConfiguration".equals(name, ignoreCase = true)) {
                                parserWifiEnterpriseConfiguration(wifiInfo, parser)
                            }
                        }
                        XmlPullParser.END_TAG -> if (("Network" == parser.name)) { //一个person处理完毕，准备下一个节点
                            if (!("null" == wifiInfo!!.name)) {
                                wifiInfoList.add(wifiInfo)
                            }
                        }
                    }
                    eventType = parser.next()
                }
            } catch (e2: Exception) {
                e2.printStackTrace()
            }
        }
        e.onNext(wifiInfoList)
    }

    /**
     * <WifiConfiguration>
     * <string name="ConfigKey">&quot;AndroidWifi&quot;NONE</string>
     * <string name="SSID">&quot;AndroidWifi&quot;</string>
     * <null name="BSSID"></null>
     * <null name="PreSharedKey"></null>
     *
     * //...
    </WifiConfiguration> *
     *
     * @param wifiInfo
     * @param parser
     * @throws Exception
     */
    @Throws(Exception::class)
    private fun parserWifiConfig(wifiInfo: WifiInfo?, parser: XmlPullParser) {
        var event: Int
        val depth: Int = parser.depth
        while (((parser.next().also { event = it }) != XmlPullParser.END_DOCUMENT
                    && (parser.depth > depth || event != XmlPullParser.END_TAG))
        ) { //只解析当前的标签内部的内容
            if (event == XmlPullParser.TEXT || event == XmlPullParser.END_TAG) {
                continue
            }
            when (event) {
                XmlPullParser.START_TAG -> {
                    val name: String = parser.name
                    val attr: String = parser.getAttributeValue(null, "name")
                    if ("string".equals(name, ignoreCase = true) && "ConfigKey".equals(
                            attr,
                            ignoreCase = true
                        )
                    ) {
                        val ssidAndEncryptType: String = parser.nextText()
                        val i: Int = ssidAndEncryptType.lastIndexOf("\"")
                        val ssid: String = ssidAndEncryptType.substring(1, i).replace("\"", "")
                        val encryptType: String = ssidAndEncryptType.substring(i).replace("\"", "")
                        wifiInfo!!.name = ssid
                        wifiInfo.encryptType = encryptType
                    }
                    if ("string".equals(name, ignoreCase = true) && "PreSharedKey".equals(
                            attr,
                            ignoreCase = true
                        )
                    ) {
                        val pwd: String = parser.nextText().replace("\"", "")
                        if (!TextUtils.isEmpty(pwd)) {
                            wifiInfo!!.password = pwd
                        }
                    }
                    if ("string-array".equals(name, ignoreCase = true) && "WEPKeys".equals(
                            attr,
                            ignoreCase = true
                        )
                    ) {
                        parserStringArray(wifiInfo, parser)
                    }
                }
                else -> {}
            }
        }
    }

    /**
     * <string-array name="WEPKeys" num="4">
     * <item value="&quot;1333333&quot;">&quot;&quot;</item>
     * <item value=""></item>
     * <item value=""></item>
     * <item value=""></item>
    </string-array> *
     *
     * @param wifiInfo
     * @param parser
     * @throws Exception
     */
    @Throws(Exception::class)
    private fun parserStringArray(wifiInfo: WifiInfo?, parser: XmlPullParser) {
        var event: Int
        val depth: Int = parser.depth
        while (((parser.next().also { event = it }) != XmlPullParser.END_DOCUMENT
                    && (parser.depth > depth || event != XmlPullParser.END_TAG))
        ) { //只解析当前的标签内部的内容
            if (event == XmlPullParser.TEXT || event == XmlPullParser.END_TAG) {
                continue
            }
            when (event) {
                XmlPullParser.START_TAG -> {
                    val name: String = parser.name
                    var pwd: String = parser.getAttributeValue(null, "value")
                    if ("item".equals(name, ignoreCase = true) && !TextUtils.isEmpty(pwd)) {
                        pwd = pwd.replace("\"", "")
                        wifiInfo!!.password = pwd
                    }
                }
                else -> {}
            }
        }
    }

    /**
     * <WifiEnterpriseConfiguration>
     * <string name="Identity">111</string>
     * <string name="AnonIdentity">11</string>
     * <string name="Password">888888888888888888888</string>
     * <string name="ClientCert"></string>
     * <string name="CaCert"></string>
     * <string name="SubjectMatch"></string>
     * <string name="Engine">0</string>
     * <string name="EngineId"></string>
     * <string name="PrivateKeyId"></string>
     * <string name="AltSubjectMatch"></string>
     * <string name="DomSuffixMatch">18888811111</string>
     * <string name="CaPath">/system/etc/security/cacerts</string>
     * <int name="EapMethod" value="0"></int>
     * <int name="Phase2Method" value="0"></int>
     * <string name="PLMN"></string>
     * <string name="Realm"></string>
    </WifiEnterpriseConfiguration> *
     *
     * @param wifiInfo
     * @param parser
     * @throws Exception
     */
    @Throws(Exception::class)
    private fun parserWifiEnterpriseConfiguration(wifiInfo: WifiInfo?, parser: XmlPullParser) {
        var event: Int
        val depth: Int = parser.depth
        while (((parser.next().also { event = it }) != XmlPullParser.END_DOCUMENT
                    && (parser.depth > depth || event != XmlPullParser.END_TAG))
        ) { //只解析当前的标签内部的内容
            if (event == XmlPullParser.TEXT || event == XmlPullParser.END_TAG) {
                continue
            }
            when (event) {
                XmlPullParser.START_TAG -> {
                    val name: String = parser.name
                    val attr: String = parser.getAttributeValue(null, "name")
                    if ("string".equals(name, ignoreCase = true) && "Password".equals(
                            attr,
                            ignoreCase = true
                        )
                    ) {
                        val pwd: String = parser.nextText()
                        if (!TextUtils.isEmpty(pwd)) {
                            wifiInfo!!.password = pwd
                        }
                    }
                }
                else -> {}
            }
        }
    }

    /**
     * 复制wifi密码到粘贴板
     *
     * @param password
     */
    override fun copyWifiPassword(password: String) {
        val cmb: ClipboardManager =
            mContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cmb.primaryClip = ClipData.newPlainText(
            mContext.getString(R.string.item_pasword_hint),
            password
        )
        Toast.makeText(mContext, R.string.copy_success, Toast.LENGTH_SHORT).show()
    }

    /**
     * 连接wifi信息
     *
     * @param wifiInfo
     */
    override fun connectWifi(wifiInfo: WifiInfo) {
        mWifiManager = mContext.getSystemService(Context.WIFI_SERVICE) as WifiManager?
        if (!mWifiManager!!.isWifiEnabled) { //未打开wifi
            mWifiManager!!.isWifiEnabled = true
            mHandler = Handler()
            mOpenWifiRunnable = OpenWifiRunnable(wifiInfo)
            mHandler!!.post(mOpenWifiRunnable)
        } else {
            startConnectWifi(wifiInfo)
        }
    }

    /**
     * 判断Wifi是否开启成功
     */
    private inner class OpenWifiRunnable constructor(private val wifiInfo: WifiInfo) : Runnable {
        override fun run() {
            if (mWifiManager!!.isWifiEnabled) {
                LogUtils.i("wifi已经打开")
                try {
                    startConnectWifi(wifiInfo)
                } catch (e: Exception) {
                    LogUtils.e("连接wifi异常", e)
                }
            } else {
                mHandler!!.postDelayed(mOpenWifiRunnable, 1000)
            }
        }
    }

    /**
     * 开始连接wifi
     *
     * @param wifiInfo
     */
    private fun startConnectWifi(wifiInfo: WifiInfo) {
        //createWifiConfig主要用于构建一个WifiConfiguration，代码中的例子主要用于连接不需要密码的Wifi
        //WifiManager的addNetwork接口，传入WifiConfiguration后，得到对应的NetworkId
        val netId: Int = mWifiManager!!.addNetwork(createWifiConfig(wifiInfo))

        //WifiManager的enableNetwork接口，就可以连接到netId对应的wifi了
        //其中boolean参数，主要用于指定是否需要断开其它Wifi网络
        val enable: Boolean = mWifiManager!!.enableNetwork(netId, true)
        LogUtils.i("连接标志enable: " + enable)

        //可选操作，让Wifi重新连接最近使用过的接入点
        //如果上文的enableNetwork成功，那么reconnect同样连接netId对应的网络
        //若失败，则连接之前成功过的网络
        val reconnect: Boolean = mWifiManager!!.reconnect()
        LogUtils.i("重连接标志reconnect: " + reconnect)
    }

    private fun createWifiConfig(wifiInfo: WifiInfo): WifiConfiguration {
        var config: WifiConfiguration? = isExist(wifiInfo.name)
        if (config != null) {
            LogUtils.i("使用默认WifiConfiguration")
            return config
        }
        //初始化WifiConfiguration
        config = WifiConfiguration()
        //指定对应的SSID
        config.SSID = "\"" + wifiInfo.name + "\""
        //不需要密码的场景
        if (TextUtils.isEmpty(wifiInfo.password)) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
            //以WEP加密的场景
        } else {
            if (wifiInfo.encryptType.contains("WEP")) { // WIFICIPHER_WEP WEP加密
                config.hiddenSSID = true
                config.wepKeys[0] = "\"" + wifiInfo.password + "\""
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED)
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
                config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.WEP104)
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
                config.wepTxKeyIndex = 0
            } else { // WIFICIPHER_WPA wpa加密
                config.preSharedKey = "\"" + wifiInfo.password + "\""
                config.hiddenSSID = true
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN) //该配置支持的身份验证协议集合
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP) //该配置所支持的组密码集合
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK) //该配置所支持的密钥管理集合
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP) //该配置所支持的WPA配对密码集合
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
                config.status = WifiConfiguration.Status.ENABLED
            }
        }
        return config
    }

    private fun isExist(ssid: String): WifiConfiguration? {
        val configs: List<WifiConfiguration> = mWifiManager!!.configuredNetworks
        for (config: WifiConfiguration in configs) {
            if ((config.SSID == "\"" + ssid + "\"")) {
                return config
            }
        }
        return null
    }

    companion object {
        /**
         * 正则预编译
         */
        /**
         * SSID
         */
        private val REGEX_SSID: Pattern = Pattern.compile("ssid=\"([^\"]+)\"")

        /**
         * 密码
         */
        private val REGEX_PSK: Pattern = Pattern.compile("psk=\"([^\"]+)\"")

        /**
         * 加密类型
         */
        private val REGEX_KEY_MGMT: Pattern = Pattern.compile("key_mgmt=(.*?)\t")
    }
}