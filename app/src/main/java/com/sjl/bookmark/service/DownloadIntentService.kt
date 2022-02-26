package com.sjl.bookmark.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import com.sjl.bookmark.BuildConfig
import com.sjl.bookmark.R
import com.sjl.bookmark.api.MyBookmarkService
import com.sjl.bookmark.app.AppConstant
import com.sjl.bookmark.kotlin.language.LanguageManager.getLocalContext
import com.sjl.bookmark.ui.activity.MainActivity
import com.sjl.core.net.RetrofitHelper
import com.sjl.core.net.filedownload.DownloadProgressHandler
import com.sjl.core.net.filedownload.FileDownloader
import com.sjl.core.util.log.LogUtils
import java.io.File

/**
 * 下载意图服务
 * 执行完就会回调onDestroy
 */
class DownloadIntentService : IntentService("DownloadIntentService") {
    private lateinit var mNotifyManager: NotificationManager
    private var mNotification: Notification? = null
    private val downloadId = 1
    override fun onCreate() {
        super.onCreate()
        mNotifyManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        //Android O(8.0)通知栏适配,需要NotificationChannel
        if (Build.VERSION.SDK_INT >= 26) {
            val download = baseContext.getString(R.string.app_download)
            val channel = NotificationChannel(
                PUSH_CHANNEL_ID,
                download, NotificationManager.IMPORTANCE_HIGH
            )
            channel.enableLights(true) //是否在桌面icon右上角展示小红点
            channel.lightColor = Color.GREEN //小红点颜色
            channel.setShowBadge(true) //是否在久按桌面图标时显示此渠道的通知
            channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            mNotifyManager.createNotificationChannel(channel)
        }
    }

    override fun onHandleIntent(intent: Intent?) {
        val fileName = intent!!.extras.getString("fileName")
        val fileSize = intent.extras.getLong("fileSize")
        LogUtils.i("fileName:$fileName")
        val file = File(AppConstant.UPDATE_APK_PATH + File.separator + fileName)
        if (file.exists() && file.isFile) {
            if (file.length() == fileSize) {
                installApp(file)
                return
            } else {
                file.delete()
            }
        }
        buildNotification()
        /**
         * 下载文件，有点慢
         */
        val instance = RetrofitHelper.getInstance()
        val apiService = instance.getApiService(
            MyBookmarkService::class.java
        )
        FileDownloader.downloadFile(
            apiService.downloadApkFile(),
            AppConstant.UPDATE_APK_PATH,
            fileName,
            object : DownloadProgressHandler() {
                override fun onProgress(progress: Int, total: Long, speed: Long) {
                    LogUtils.i("progress:$progress")
                    mNotification!!.contentView.setProgressBar(
                        R.id.pb_progress,
                        100,
                        progress,
                        false
                    )
                    mNotification!!.contentView.setTextViewText(R.id.tv_progress, "已下载$progress%")
                    mNotifyManager.notify(downloadId, mNotification)
                }

                override fun onCompleted(file: File) {
                    LogUtils.i("下载apk文件成功")
                    if (mNotifyManager != null) { //不能再onDestroy写，否则只会显示一次通知栏
                        if (Build.VERSION.SDK_INT >= 26) {
                            mNotifyManager.deleteNotificationChannel(PUSH_CHANNEL_ID)
                        }
                        mNotifyManager.cancel(downloadId)
                    }
                    FileDownloader.clear()
                    installApp(file)
                }

                override fun onError(e: Throwable) {
                    LogUtils.e("下载apk文件异常", e)
                    FileDownloader.clear()
                }
            })
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(getLocalContext(base))
    }

    private fun buildNotification() {
        val download = baseContext.getString(R.string.app_download)
        val startDownload = baseContext.getString(R.string.app_start_download)
        val downloaded = baseContext.getString(R.string.app_downloaded)
        val updateIntent = Intent(this@DownloadIntentService, MainActivity::class.java) //点击跳转到主页
        updateIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(
            this@DownloadIntentService,
            0,
            updateIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val remoteViews = RemoteViews(packageName, R.layout.notify_download)
        remoteViews.setProgressBar(R.id.pb_progress, 100, 0, false)
        remoteViews.setTextViewText(R.id.tv_progress, downloaded + 0 + "%")
        val builder = NotificationCompat.Builder(
            this@DownloadIntentService,
            PUSH_CHANNEL_ID
        ) //特别注意添加面板id，否则不出来
            .setTicker(startDownload)
            .setChannelId(PUSH_CHANNEL_ID)
            .setContentTitle(download)
            .setCustomContentView(remoteViews)
            .setWhen(System.currentTimeMillis())
            .setContentIntent(pendingIntent)
            .setPriority(Notification.PRIORITY_DEFAULT) // 设置该通知优先级
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
            .setSmallIcon(R.mipmap.ic_launcher)
        mNotification = builder.build()
        mNotification?.defaults = Notification.DEFAULT_SOUND //设置声音
        mNotifyManager.notify(downloadId, mNotification)
    }

    /**
     * 安装判断
     *
     * @param file
     */
    private fun installApp(file: File) {
        //7.0以上通过FileProvider
        val intent = Intent(Intent.ACTION_VIEW)
        var uri: Uri? = null
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(
                applicationContext,
                BuildConfig.APPLICATION_ID + ".fileProvider",
                file
            )
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setDataAndType(uri, "application/vnd.android.package-archive")
            startActivity(intent)
        } else {
            uri = Uri.fromFile(file)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.setDataAndType(uri, "application/vnd.android.package-archive")
        }
        //查询所有符合 intent 跳转目标应用类型的应用，注意此方法必须放置setDataAndType的方法之后
        val resInfoList =
            packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        //然后全部授权
        for (resolveInfo in resInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            grantUriPermission(
                packageName,
                uri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
        startActivity(intent)
    }

    companion object {
        private const val PUSH_CHANNEL_ID = "DOWNLOAD_NOTIFY_ID"
    }

    init {
        LogUtils.i("启动下载意图服务DownloadIntentService")
    }
}