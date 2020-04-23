package com.sjl.bookmark.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.widget.RemoteViews;

import com.sjl.bookmark.BuildConfig;
import com.sjl.bookmark.R;
import com.sjl.bookmark.api.MyBookmarkService;
import com.sjl.bookmark.app.AppConstant;
import com.sjl.bookmark.ui.activity.MainActivity;
import com.sjl.core.net.RetrofitHelper;
import com.sjl.core.net.filedownload.DownloadProgressHandler;
import com.sjl.core.net.filedownload.FileDownloader;
import com.sjl.core.util.log.LogUtils;

import java.io.File;
import java.util.List;




/**
 * 下载意图服务
 * 执行完就会回调onDestroy
 */
public class DownloadIntentService extends IntentService {

    private NotificationManager mNotifyManager;
    private Notification mNotification;
    private int downloadId = 1;
    private static final String PUSH_CHANNEL_ID = "DOWNLOAD_NOTIFY_ID";


    public DownloadIntentService() {
        super("DownloadIntentService");
        LogUtils.i("启动下载意图服务DownloadIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotifyManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        //Android O(8.0)通知栏适配,需要NotificationChannel
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(PUSH_CHANNEL_ID,
                    "下载", NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true); //是否在桌面icon右上角展示小红点
            channel.setLightColor(Color.GREEN); //小红点颜色
            channel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            mNotifyManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String fileName = intent.getExtras().getString("fileName");
        long fileSize = intent.getExtras().getLong("fileSize");
        LogUtils.i("fileName:" + fileName);
        final File file = new File(AppConstant.UPDATE_APK_PATH + File.separator + fileName);
        if (file.exists() && file.isFile()) {
            if (file.length() == fileSize) {
                installApp(file);
                return;
            } else {
                file.delete();
            }
        }

        buildNotification();




        /**
         * 下载文件，有点慢
         */
        RetrofitHelper instance = RetrofitHelper.getInstance();
        MyBookmarkService apiService = instance.getApiService(MyBookmarkService.class);

        FileDownloader.downloadFile(apiService.downloadApkFile(), AppConstant.UPDATE_APK_PATH, fileName, new DownloadProgressHandler() {
            @Override
            public void onProgress(int progress, long total, long speed) {
                LogUtils.i("progress:" + progress);
                mNotification.contentView.setProgressBar(R.id.pb_progress, 100, progress, false);
                mNotification.contentView.setTextViewText(R.id.tv_progress, "已下载" + progress + "%");
                mNotifyManager.notify(downloadId, mNotification);
            }

            @Override
            public void onCompleted(File file) {
                LogUtils.i("下载apk文件成功");
                if (mNotifyManager != null) {//不能再onDestroy写，否则只会显示一次通知栏
                    if (Build.VERSION.SDK_INT >= 26) {
                        mNotifyManager.deleteNotificationChannel(PUSH_CHANNEL_ID);
                    }
                    mNotifyManager.cancel(downloadId);
                }
                FileDownloader.clear();
                installApp(file);
            }

            @Override
            public void onError(Throwable e) {
                LogUtils.e("下载apk文件异常", e);
                FileDownloader.clear();
            }
        });
    }

    private void buildNotification() {
        Intent updateIntent = new Intent(DownloadIntentService.this, MainActivity.class);//点击跳转到主页
        updateIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(DownloadIntentService.this, 0, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        final RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notify_download);
        remoteViews.setProgressBar(R.id.pb_progress, 100, 0, false);
        remoteViews.setTextViewText(R.id.tv_progress, "已下载" + 0 + "%");

        final NotificationCompat.Builder builder =
                new NotificationCompat.Builder(DownloadIntentService.this,PUSH_CHANNEL_ID)//特别注意添加面板id，否则不出来
                        .setTicker("开始下载")
                        .setChannelId(PUSH_CHANNEL_ID)
                        .setContentTitle("下载")
                        .setCustomContentView(remoteViews)
                        .setWhen(System.currentTimeMillis())
                        .setContentIntent(pendingIntent)
                        .setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                        .setSmallIcon(R.mipmap.ic_launcher);

        mNotification = builder.build();
        mNotification.defaults = Notification.DEFAULT_SOUND;//设置声音
        mNotifyManager.notify(downloadId, mNotification);
    }

    /**
     * 安装判断
     *
     * @param file
     */
    private void installApp(File file) {
        //7.0以上通过FileProvider
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = null;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".fileProvider", file);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            startActivity(intent);
        } else {
            uri = Uri.fromFile(file);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
        }
        //查询所有符合 intent 跳转目标应用类型的应用，注意此方法必须放置setDataAndType的方法之后
        List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        //然后全部授权
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        startActivity(intent);
    }
}
