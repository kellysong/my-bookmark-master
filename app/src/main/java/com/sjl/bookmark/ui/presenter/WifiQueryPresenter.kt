package com.sjl.bookmark.ui.presenter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Xml;
import android.widget.Toast;

import com.sjl.bookmark.R;
import com.sjl.bookmark.entity.WifiInfo;
import com.sjl.bookmark.ui.contract.WifiQueryContract;
import com.sjl.core.net.RxSchedulers;
import com.sjl.core.util.log.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename WifiQueryPresenter.java
 * @time 2018/3/3 15:58
 * @copyright(C) 2018 song
 */
public class WifiQueryPresenter extends WifiQueryContract.Presenter {
    private WifiManager mWifiManager;
    private Handler mHandler;
    private OpenWifiRunnable mOpenWifiRunnable;
    /**
     * 正则预编译
     */
    /**
     * SSID
     */
    private static final Pattern REGEX_SSID = Pattern.compile("ssid=\"([^\"]+)\"");
    /**
     * 密码
     */
    private static final Pattern REGEX_PSK = Pattern.compile("psk=\"([^\"]+)\"");
    /**
     * 加密类型
     */
    private static final Pattern REGEX_KEY_MGMT = Pattern.compile("key_mgmt=(.*?)\t");


    /**
     * 读取wifi信息
     */
    @Override
    public void initWifiInfo() {
        Observable.create(new ObservableOnSubscribe<Object>() { // 第一步：初始化Observable
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                readWifiInfo(e);
                e.onComplete();
            }
        }).compose(RxSchedulers.<Object>applySchedulers()).as(this.<Object>bindLifecycle())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object s) {
                        if (s instanceof String) {
                            EventBus.getDefault().post(s.toString());
                        } else if (s instanceof List) {
                            EventBus.getDefault().post((List<WifiInfo>) s);
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e("读取wifi密码异常", e);
                    }

                    @Override
                    public void onComplete() {

                    } // 第三步：订阅

                });

    }

    private void readWifiInfo(ObservableEmitter<Object> e) {
        Process process = null;
        DataOutputStream dataOutputStream = null;
        DataInputStream dataInputStream = null;
        StringBuffer wifiConf = new StringBuffer();
        boolean wifiParseFlag = false;
        boolean error = false;
        try {
            process = Runtime.getRuntime().exec("su");///(这里执行是系统已经开放了root权限，而不是说通过执行这句来获得root权限)
            dataOutputStream = new DataOutputStream(process.getOutputStream());
            dataInputStream = new DataInputStream(process.getInputStream());
            int sdkInt = Build.VERSION.SDK_INT;

            if (sdkInt >= Build.VERSION_CODES.O) {
                dataOutputStream.writeBytes("cat /data/misc/wifi/WifiConfigStore.xml\n");
                wifiParseFlag = true;
            } else {
                dataOutputStream.writeBytes("cat /data/misc/wifi/wpa_supplicant.conf\n");

            }
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            InputStreamReader inputStreamReader = new InputStreamReader(dataInputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                wifiConf.append(line);
            }
            bufferedReader.close();
            inputStreamReader.close();
            process.waitFor();
        } catch (Exception e1) {
            LogUtils.e("读取wifi配置文件异常", e1);
            error = true;
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (dataInputStream != null) {
                    dataInputStream.close();
                }
                if (process != null) {
                    process.destroy();
                }
            } catch (IOException e2) {
                LogUtils.e("关闭读取wifi流异常", e2);
            }
        }
        if (error) {
            e.onNext(mContext.getString(R.string.no_root_permission));
            return;
        }
        String wifiConfigStr = wifiConf.toString();
        if (TextUtils.isEmpty(wifiConfigStr)) {
            e.onNext(new ArrayList<WifiInfo>());
            return;
        }
        LogUtils.i("wifiConf:" + wifiConfigStr);
        ArrayList<WifiInfo> wifiInfoList = new ArrayList<>();
        if (!wifiParseFlag) {
            Pattern network = Pattern.compile("network=\\{([^\\}]+)\\}", Pattern.DOTALL);
            Matcher networkMatcher = network.matcher(wifiConfigStr);
            WifiInfo wifiInfo;
            while (networkMatcher.find()) {
                String networkBlock = networkMatcher.group();
                LogUtils.i("wifi信息：" + networkBlock);
                Matcher ssidMatcher = REGEX_SSID.matcher(networkBlock);
                if (ssidMatcher.find()) {
                    wifiInfo = new WifiInfo();
                    wifiInfo.setName(ssidMatcher.group(1));

                    Matcher pskMatcher = REGEX_PSK.matcher(networkBlock);
                    if (pskMatcher.find()) {
                        wifiInfo.setPassword(pskMatcher.group(1));
                    } else {
                        wifiInfo.setPassword(mContext.getString(R.string.empty_password));
                    }
                    Matcher keyMatcher = REGEX_KEY_MGMT.matcher(networkBlock);
                    if (keyMatcher.find()) {
                        wifiInfo.setEncryptType(keyMatcher.group(1));
                    } else {
                        wifiInfo.setEncryptType(mContext.getString(R.string.unknown));
                    }
                    wifiInfoList.add(wifiInfo);
                }
            }

        } else {
            try {
                //https://blog.csdn.net/csdn_of_coder/article/details/73380495
                XmlPullParser parser = Xml.newPullParser();
                parser.setInput(new StringReader(wifiConfigStr));
                int eventType = parser.getEventType();
                WifiInfo wifiInfo = null;
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    switch (eventType) {
                        case XmlPullParser.START_DOCUMENT:

                            break;
                        case XmlPullParser.START_TAG:
                            String name = parser.getName();
                            if ("Network".equalsIgnoreCase(name)) {
                                wifiInfo = new WifiInfo();
                                wifiInfo.setPassword("--");
                            }
                            if ("WifiConfiguration".equalsIgnoreCase(name)) {
                                parserWifiConfig(wifiInfo, parser);
                            }
                            if ("WifiEnterpriseConfiguration".equalsIgnoreCase(name)) {
                                parserWifiEnterpriseConfiguration(wifiInfo, parser);
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            if ("Network".equals(parser.getName())) {//一个person处理完毕，准备下一个节点
                                if (!"null".equals(wifiInfo.getName())) {
                                    wifiInfoList.add(wifiInfo);
                                }
                            }
                            break;
                    }
                    eventType = parser.next();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }

        }
        e.onNext(wifiInfoList);
    }

    /**
     * <WifiConfiguration>
     * <string name="ConfigKey">&quot;AndroidWifi&quot;NONE</string>
     * <string name="SSID">&quot;AndroidWifi&quot;</string>
     * <null name="BSSID" />
     * <null name="PreSharedKey" />
     *
     * //...
     * </WifiConfiguration>
     *
     * @param wifiInfo
     * @param parser
     * @throws Exception
     */
    private void parserWifiConfig(WifiInfo wifiInfo, XmlPullParser parser) throws Exception {
        int event;
        int depth = parser.getDepth();

        while ((event = parser.next()) != XmlPullParser.END_DOCUMENT
                && (parser.getDepth() > depth || event != XmlPullParser.END_TAG)) {//只解析当前的标签内部的内容

            if (event == XmlPullParser.TEXT || event == XmlPullParser.END_TAG) {
                continue;
            }

            switch (event) {
                case XmlPullParser.START_TAG:
                    String name = parser.getName();
                    String attr = parser.getAttributeValue(null, "name");
                    if ("string".equalsIgnoreCase(name) && "ConfigKey".equalsIgnoreCase(attr)) {
                        String ssidAndEncryptType = parser.nextText();
                        int i = ssidAndEncryptType.lastIndexOf("\"");
                        String ssid = ssidAndEncryptType.substring(1, i).replace("\"", "");
                        String encryptType = ssidAndEncryptType.substring(i).replace("\"", "");
                        wifiInfo.setName(ssid);
                        wifiInfo.setEncryptType(encryptType);
                    }

                    if ("string".equalsIgnoreCase(name) && "PreSharedKey".equalsIgnoreCase(attr)) {
                        String pwd = parser.nextText().replace("\"", "");
                        if (!TextUtils.isEmpty(pwd)) {
                            wifiInfo.setPassword(pwd);
                        }
                    }

                    if ("string-array".equalsIgnoreCase(name) && "WEPKeys".equalsIgnoreCase(attr)) {
                        parserStringArray(wifiInfo, parser);

                    }
                    break;
                default:
                    break;
            }
        }


    }

    /**
     * <string-array name="WEPKeys" num="4">
     * <item value="&quot;1333333&quot;" />
     * <item value="" />
     * <item value="" />
     * <item value="" />
     * </string-array>
     *
     * @param wifiInfo
     * @param parser
     * @throws Exception
     */
    private void parserStringArray(WifiInfo wifiInfo, XmlPullParser parser) throws Exception {
        int event;
        int depth = parser.getDepth();

        while ((event = parser.next()) != XmlPullParser.END_DOCUMENT
                && (parser.getDepth() > depth || event != XmlPullParser.END_TAG)) {//只解析当前的标签内部的内容

            if (event == XmlPullParser.TEXT || event == XmlPullParser.END_TAG) {
                continue;
            }

            switch (event) {
                case XmlPullParser.START_TAG:
                    String name = parser.getName();
                    String pwd = parser.getAttributeValue(null, "value");
                    if ("item".equalsIgnoreCase(name) && !TextUtils.isEmpty(pwd)) {
                        pwd = pwd.replace("\"", "");
                        wifiInfo.setPassword(pwd);
                    }
                    break;
                default:
                    break;
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
     * <int name="EapMethod" value="0" />
     * <int name="Phase2Method" value="0" />
     * <string name="PLMN"></string>
     * <string name="Realm"></string>
     * </WifiEnterpriseConfiguration>
     *
     * @param wifiInfo
     * @param parser
     * @throws Exception
     */
    private void parserWifiEnterpriseConfiguration(WifiInfo wifiInfo, XmlPullParser parser) throws Exception {
        int event;
        int depth = parser.getDepth();
        while ((event = parser.next()) != XmlPullParser.END_DOCUMENT
                && (parser.getDepth() > depth || event != XmlPullParser.END_TAG)) {//只解析当前的标签内部的内容

            if (event == XmlPullParser.TEXT || event == XmlPullParser.END_TAG) {
                continue;
            }

            switch (event) {
                case XmlPullParser.START_TAG:
                    String name = parser.getName();
                    String attr = parser.getAttributeValue(null, "name");
                    if ("string".equalsIgnoreCase(name) && "Password".equalsIgnoreCase(attr)) {
                        String pwd = parser.nextText();
                        if (!TextUtils.isEmpty(pwd)) {
                            wifiInfo.setPassword(pwd);
                        }

                    }
                default:
                    break;
            }
        }


    }

    /**
     * 复制wifi密码到粘贴板
     *
     * @param password
     */
    @Override
    public void copyWifiPassword(String password) {
        ClipboardManager cmb = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setPrimaryClip(ClipData.newPlainText(mContext.getString(R.string.item_pasword_hint), password));
        Toast.makeText(mContext, R.string.copy_success, Toast.LENGTH_SHORT).show();
    }

    /**
     * 连接wifi信息
     *
     * @param wifiInfo
     */
    @Override
    public void connectWifi(WifiInfo wifiInfo) {
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        if (!mWifiManager.isWifiEnabled()) {//未打开wifi
            mWifiManager.setWifiEnabled(true);
            mHandler = new Handler();
            mOpenWifiRunnable = new OpenWifiRunnable(wifiInfo);
            mHandler.post(mOpenWifiRunnable);
        } else {
            startConnectWifi(wifiInfo);
        }
    }

    /**
     * 判断Wifi是否开启成功
     */
    private class OpenWifiRunnable implements Runnable {
        private WifiInfo wifiInfo;

        public OpenWifiRunnable(WifiInfo wifiInfo) {
            this.wifiInfo = wifiInfo;
        }

        @Override
        public void run() {
            if (mWifiManager.isWifiEnabled()) {
                LogUtils.i("wifi已经打开");
                try {
                    startConnectWifi(wifiInfo);
                } catch (Exception e) {
                    LogUtils.e("连接wifi异常", e);
                }
            } else {
                mHandler.postDelayed(mOpenWifiRunnable, 1000);
            }
        }


    }

    /**
     * 开始连接wifi
     *
     * @param wifiInfo
     */
    private void startConnectWifi(WifiInfo wifiInfo) {
        //createWifiConfig主要用于构建一个WifiConfiguration，代码中的例子主要用于连接不需要密码的Wifi
        //WifiManager的addNetwork接口，传入WifiConfiguration后，得到对应的NetworkId
        int netId = mWifiManager.addNetwork(createWifiConfig(wifiInfo));

        //WifiManager的enableNetwork接口，就可以连接到netId对应的wifi了
        //其中boolean参数，主要用于指定是否需要断开其它Wifi网络
        boolean enable = mWifiManager.enableNetwork(netId, true);
        LogUtils.i("连接标志enable: " + enable);

        //可选操作，让Wifi重新连接最近使用过的接入点
        //如果上文的enableNetwork成功，那么reconnect同样连接netId对应的网络
        //若失败，则连接之前成功过的网络
        boolean reconnect = mWifiManager.reconnect();
        LogUtils.i("重连接标志reconnect: " + reconnect);
    }


    private WifiConfiguration createWifiConfig(WifiInfo wifiInfo) {
        WifiConfiguration config = isExist(wifiInfo.getName());
        if (config != null) {
            LogUtils.i("使用默认WifiConfiguration");
            return config;
        }
        //初始化WifiConfiguration
        config = new WifiConfiguration();
        //指定对应的SSID
        config.SSID = "\"" + wifiInfo.getName() + "\"";
        //不需要密码的场景
        if (TextUtils.isEmpty(wifiInfo.getPassword())) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            //以WEP加密的场景
        } else {
            if (wifiInfo.getEncryptType().contains("WEP")) {// WIFICIPHER_WEP WEP加密

                config.hiddenSSID = true;
                config.wepKeys[0] = "\"" + wifiInfo.getPassword() + "\"";
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                config.allowedGroupCiphers
                        .set(WifiConfiguration.GroupCipher.WEP104);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                config.wepTxKeyIndex = 0;
            } else {// WIFICIPHER_WPA wpa加密
                config.preSharedKey = "\"" + wifiInfo.getPassword() + "\"";
                config.hiddenSSID = true;
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);//该配置支持的身份验证协议集合
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);//该配置所支持的组密码集合
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);//该配置所支持的密钥管理集合
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);//该配置所支持的WPA配对密码集合
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                config.status = WifiConfiguration.Status.ENABLED;
            }
        }

        return config;
    }

    private WifiConfiguration isExist(String ssid) {
        List<WifiConfiguration> configs = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration config : configs) {
            if (config.SSID.equals("\"" + ssid + "\"")) {
                return config;
            }
        }
        return null;
    }

}
