package com.sjl.bookmark.ui.activity

import android.Manifest
import com.amap.api.location.*
import com.amap.api.location.AMapLocationClientOption.*
import com.sjl.bookmark.R
import com.sjl.bookmark.kotlin.language.I18nUtils
import com.sjl.core.mvp.BaseActivity
import com.sjl.core.mvp.NoPresenter
import com.sjl.core.permission.PermissionsManager
import com.sjl.core.permission.PermissionsResultAction
import com.sjl.core.util.datetime.TimeUtils
import kotlinx.android.synthetic.main.speed_detection_activity.*
import kotlinx.android.synthetic.main.toolbar_default.*
import java.text.DecimalFormat

/**
 * 测速
 *
 * @author Kelly
 * @version 1.0.0
 * @filename SpeedDetectionActivity
 * @time 2021/5/9 13:21
 * @copyright(C) 2021 song
 */
class SpeedDetectionActivity : BaseActivity<NoPresenter>() {

    /**
     * 需要进行检测的权限数组
     */
    protected var needPermissions: Array<String> = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.READ_PHONE_STATE
    )

    override fun getLayoutId(): Int {
        return R.layout.speed_detection_activity
    }

    override fun initView() {
        bindingToolbar(common_toolbar, I18nUtils.getString(R.string.tool_speed_detection))
    }

    override fun initListener() {}
    override fun initData() {
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(
            this,
            needPermissions,
            object : PermissionsResultAction() {
                override fun onGranted() {
                    //初始化定位
                    initLocation()
                    //开启定位
                    startLocation()
                }

                override fun onDenied(permission: String) {
                    showToast("请先授权定位所需要的权限")
                    finish()
                }
            })
    }

    override fun onResume() {
        super.onResume()
        startLocation()
    }

    override fun onPause() {
        super.onPause()
        stopLocation()
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyLocation()
    }

    private lateinit var locationClient: AMapLocationClient
    private lateinit var locationOption: AMapLocationClientOption

    /**
     * 初始化定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private fun initLocation() {
        //初始化client
        locationClient = AMapLocationClient(applicationContext)
        locationOption = defaultOption
        //设置定位参数
        locationClient.setLocationOption(locationOption)
        // 设置定位监听
        locationClient.setLocationListener(locationListener)
        //高精度模式
        locationOption.locationMode = AMapLocationMode.Hight_Accuracy
    }//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
    //可选，设置是否gps优先，只在高精度模式下有效。默认关闭
    //可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
    //可选，设置定位间隔。默认为2秒
    //可选，设置是否返回逆地理地址信息。默认是true
    //可选，设置是否单次定位。默认是false
    //可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
    //可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
    //可选，设置是否使用传感器。默认是false
    //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
    //可选，设置是否使用缓存定位，默认为true
    //可选，设置逆地理信息的语言，默认值为默认语言（根据所在地区选择语言）
    /**
     * 默认的定位参数
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private val defaultOption: AMapLocationClientOption
        private get() {
            val mOption: AMapLocationClientOption = AMapLocationClientOption()
            mOption.locationMode =
                AMapLocationMode.Hight_Accuracy //可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
            mOption.isGpsFirst = false //可选，设置是否gps优先，只在高精度模式下有效。默认关闭
            mOption.httpTimeOut = 20000 //可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
            mOption.interval = 2000 //可选，设置定位间隔。默认为2秒
            mOption.isNeedAddress = true //可选，设置是否返回逆地理地址信息。默认是true
            mOption.isOnceLocation = false //可选，设置是否单次定位。默认是false
            mOption.isOnceLocationLatest =
                false //可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
            AMapLocationClientOption.setLocationProtocol(AMapLocationProtocol.HTTP) //可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
            mOption.isSensorEnable = false //可选，设置是否使用传感器。默认是false
            mOption.isWifiScan =
                true //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
            mOption.isLocationCacheEnable = true //可选，设置是否使用缓存定位，默认为true
            mOption.geoLanguage = GeoLanguage.DEFAULT //可选，设置逆地理信息的语言，默认值为默认语言（根据所在地区选择语言）
            return mOption
        }

    /**
     * 定位监听
     */
    var locationListener: AMapLocationListener? = object : AMapLocationListener {
        override fun onLocationChanged(location: AMapLocation) {
            if (isDestroy(this@SpeedDetectionActivity)) {
                return
            }
            if (null != location) {
                val sb: StringBuffer = StringBuffer()
                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if (location.errorCode == 0) {
                    sb.append("定位成功" + "\n")
                    sb.append("定位类型: " + location.locationType + "\n")
                    sb.append("经    度    : " + location.longitude + "\n")
                    sb.append("纬    度    : " + location.latitude + "\n")
                    sb.append("精    度    : " + location.accuracy + "米" + "\n")
                    sb.append("提供者    : " + location.provider + "\n")
                    val speed: Float = location.speed //取得速度
                    val decimalFormat: DecimalFormat =
                        DecimalFormat("0.00") //构造方法的字符格式这里如果小数不足2位,会以0补足.
                    val p: String = decimalFormat.format(speed * 3.6) //format 返回的是字符串
                    tv_speed!!.text = "速度: " + p + "km/h"
                    sb.append("速    度    : " + p + "km/h" + "\n")
                    sb.append("角    度    : " + location.bearing + "\n")
                    // 获取当前提供定位服务的卫星个数
                    sb.append("星    数    : " + location.satellites + "\n")
                    sb.append("国    家    : " + location.country + "\n")
                    sb.append("省            : " + location.province + "\n")
                    sb.append("市            : " + location.city + "\n")
                    sb.append("城市编码 : " + location.cityCode + "\n")
                    sb.append("区            : " + location.district + "\n")
                    sb.append("区域 码   : " + location.adCode + "\n")
                    tv_location.text = "地址: " + location.address
                    sb.append("地    址    : " + location.address + "\n")
                    sb.append("兴趣点    : " + location.poiName + "\n")
                    //定位完成的时间
                    sb.append(
                        "定位时间: " + TimeUtils.formatDateToStr(
                            location.time,
                            "yyyy-MM-dd HH:mm:ss"
                        ) + "\n"
                    )
                } else {
                    //定位失败
                    sb.append("定位失败" + "\n")
                    sb.append("错误码:" + location.errorCode + "\n")
                    sb.append("错误信息:" + location.errorInfo + "\n")
                    sb.append("错误描述:" + location.locationDetail + "\n")
                }
                sb.append("***定位质量报告***").append("\n")
                sb.append("* WIFI开关：")
                    .append(if (location.locationQualityReport.isWifiAble) "开启" else "关闭")
                    .append("\n")
                sb.append("* GPS状态：")
                    .append(getGPSStatusString(location.locationQualityReport.gpsStatus))
                    .append("\n")
                sb.append("* GPS星数：").append(location.locationQualityReport.gpsSatellites)
                    .append("\n")
                sb.append("* 网络类型：" + location.locationQualityReport.networkType)
                    .append("\n")
                sb.append("* 网络耗时：" + location.locationQualityReport.netUseTime)
                    .append("\n")
                sb.append("****************").append("\n")
                //定位之后的回调时间
                sb.append(
                    "回调时间: " + TimeUtils.formatDateToStr(
                        System.currentTimeMillis(),
                        "yyyy-MM-dd HH:mm:ss"
                    ) + "\n"
                )

                //解析定位结果，
                val result: String = sb.toString()
                tv_result!!.text = result
            } else {
                tv_location!!.text = "定位失败，loc is null"
            }
        }
    }

    /**
     * 获取GPS状态的字符串
     *
     * @param statusCode GPS状态码
     * @return
     */
    private fun getGPSStatusString(statusCode: Int): String {
        var str: String = ""
        when (statusCode) {
            AMapLocationQualityReport.GPS_STATUS_OK -> str = "GPS状态正常"
            AMapLocationQualityReport.GPS_STATUS_NOGPSPROVIDER -> str =
                "手机中没有GPS Provider，无法进行GPS定位"
            AMapLocationQualityReport.GPS_STATUS_OFF -> str = "GPS关闭，建议开启GPS，提高定位质量"
            AMapLocationQualityReport.GPS_STATUS_MODE_SAVING -> str =
                "选择的定位模式中不包含GPS定位，建议选择包含GPS定位的模式，提高定位质量"
            AMapLocationQualityReport.GPS_STATUS_NOGPSPERMISSION -> str = "没有GPS定位权限，建议开启gps定位权限"
        }
        return str
    }

    /**
     * 开始定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private fun startLocation() {
        if (locationClient != null) {
            // 设置定位参数
            locationClient.setLocationOption(locationOption)
            // 启动定位
            locationClient.startLocation()
        }
    }

    /**
     * 停止定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private fun stopLocation() {
        if (locationClient != null) {
            // 停止定位
            locationClient.stopLocation()
        }
    }

    /**
     * 销毁定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private fun destroyLocation() {
        if (locationClient != null) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy()
            locationListener = null
        }
    }
}