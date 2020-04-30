package com.sjl.bookmark.app;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.mob.MobSDK;
import com.sjl.bookmark.BuildConfig;
import com.sjl.bookmark.net.MyBaseUrlAdapter;
import com.sjl.bookmark.service.X5CoreService;
import com.sjl.core.app.BaseApplication;
import com.sjl.core.app.CrashHandler;
import com.sjl.core.net.RetrofitHelper;
import com.sjl.core.net.RetrofitLogAdapter;
import com.sjl.core.net.RetrofitParams;

import androidx.multidex.MultiDex;


public class MyApplication extends BaseApplication {


    @Override
    public void onCreate() {
        super.onCreate();
        boolean enableLog = BuildConfig.enableLog;
        CrashHandler.getInstance().init(this);
        initLogConfig(enableLog);//控制是否开启开启
        initSkinLoader();
        initRetrofit();
        preInitX5Core();
        MobSDK.init(this);
    }
    private void initRetrofit() {
        RetrofitParams retrofitParams = new RetrofitParams.Builder()
                .setBaseUrlAdapter(new MyBaseUrlAdapter()).setRetrofitLogAdapter(new RetrofitLogAdapter() {
                    @Override
                    public boolean printRequestUrl() {
                        return false;
                    }

                    @Override
                    public boolean printHttpLog() {
                        return false;
                    }
                })
                .build();
        RetrofitHelper.getInstance().init(retrofitParams);
    }



    /**
     * 初始化X5内核
     */
    private void preInitX5Core() {
        //预加载x5内核
        Intent intent = new Intent(this, X5CoreService.class);
        //开启服务兼容
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    /**
     * 在onCreate之前执行
     *
     * @param base
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);//https://developer.android.com/studio/build/multidex
    }
}
