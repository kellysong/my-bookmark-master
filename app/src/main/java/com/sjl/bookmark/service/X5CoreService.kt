package com.sjl.bookmark.service

import android.app.IntentService
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import com.sjl.core.util.log.LogUtils
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.QbSdk.PreInitCallback
import com.tencent.smtt.sdk.TbsListener

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename X5CoreService.java
 * @time 2018/4/18 11:27
 * @copyright(C) 2018 song
 */
class X5CoreService
/**
 * Creates an IntentService.  Invoked by your subclass's constructor.
 */
    : IntentService("X5CoreService") {
    override fun onHandleIntent(intent: Intent?) {
        //在这里添加我们要执行的代码，Intent中可以保存我们所需的数据，
        //每一次通过Intent发送的命令将被顺序执行
        initX5()
    }

    private fun initX5() {
        if (QbSdk.isTbsCoreInited()) {
            // preinit只需要调用一次，如果已经完成了初始化，那么就直接构造view
            QbSdk.preInit(applicationContext, cb) // 设置X5初始化完成的回调接口
            return
        }
        //x5内核初始化
        //非 Wifi 状态下是默认不会下载的,可以通过以下方法设置
        // QbSdk.setDownloadWithoutWifi(true);
        QbSdk.initX5Environment(applicationContext, cb)
        QbSdk.setTbsListener(object : TbsListener {
            //监听内核下载
            override fun onDownloadFinish(i: Int) {
                //tbs 内核下载完成回调
            }

            override fun onInstallFinish(i: Int) {
                //内核安装完成回调，
            }

            override fun onDownloadProgress(i: Int) {
                //下载进度监听
            }
        })
    }

    //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
    var cb: PreInitCallback = object : PreInitCallback {
        override fun onViewInitFinished(arg0: Boolean) {
            //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
            LogUtils.i("x5内核初始化标志：arg0=$arg0")
        }

        override fun onCoreInitFinished() {}
    }
    private var notificationManager: NotificationManager? = null
    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val mChannel: NotificationChannel
        //开启Service报错 兼容，前台服务需要加这个
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = NotificationChannel(
                CHANNEL_ID_STRING,
                "x5core",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager!!.createNotificationChannel(mChannel)
            val notification = Notification.Builder(applicationContext, CHANNEL_ID_STRING).build()
            startForeground(
                1,
                notification
            ) //这个id不要和应用内的其他通知id一样，不行就写 int.maxValue()        //context.startForeground(SERVICE_ID, builder.getNotification());
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager != null) {
            notificationManager!!.cancelAll()
        }
    }

    companion object {
        private const val CHANNEL_ID_STRING = "x5"
    }
}