package com.sjl.bookmark.app

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.multidex.MultiDex
import com.sjl.bookmark.BuildConfig
import com.sjl.bookmark.kotlin.darkmode.DarkModeUtils
import com.sjl.bookmark.net.MyBaseUrlAdapter
import com.sjl.bookmark.service.X5CoreService
import com.sjl.core.app.BaseApplication
import com.sjl.core.app.CrashHandler
import com.sjl.core.manager.CachedThreadManager
import com.sjl.core.net.RetrofitHelper
import com.sjl.core.net.RetrofitLogAdapter
import com.sjl.core.net.RetrofitParams
import com.squareup.leakcanary.LeakCanary
import com.tencent.bugly.crashreport.CrashReport
import io.reactivex.plugins.RxJavaPlugins
import java.util.concurrent.TimeUnit

/**
 * @author song
 */
class MyApplication : BaseApplication() {

    companion object {

        fun getContext(): Context {
            return BaseApplication.getContext()
        }

        fun getAppVersion(): String {
            return BaseApplication.getAppVersion()
        }

        fun getVersionCode(): Int {
            return BaseApplication.getVersionCode()
        }

    }

    override fun onCreate() {
        super.onCreate()
        initSyncTask()
        initAsyncTask()
    }

    private fun initSyncTask() {
        val enableLog = BuildConfig.enableLog
        initLogConfig(enableLog) //控制是否开启开启
        initSkinLoader()
        initRetrofit()
        initDarkMode()
        initErrorHandler()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)
        //        WebViewPool.init();
    }

    private fun initAsyncTask() {
        CachedThreadManager.getInstance()
            .execute { //                MobSDK.init(MyApplication.this);
                preInitX5Core()
            }
    }

    private fun initDarkMode() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                DarkModeUtils.initDarkMode()
            }

            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }

    private fun initErrorHandler() {
        CrashHandler.getInstance().init(this)
        if (!BuildConfig.enableLog) {
            CrashReport.initCrashReport(applicationContext, BuildConfig.buglyID, false)
        }
        RxJavaPlugins.setErrorHandler { throwable: Throwable? ->
            CrashReport.postCatchedException(
                Exception("RxJava全局异常", throwable)
            )
        }
    }

    private fun initRetrofit() {
        val retrofitParams = RetrofitParams.Builder()
            .setBaseUrlAdapter(MyBaseUrlAdapter())
                .setConnectTimeout(30)
                .setReadTimeout(30)
                .setWriteTimeout(30)
            .setRetrofitLogAdapter(object : RetrofitLogAdapter {
                override fun printRequestUrl(): Boolean {
                    return false
                }

                override fun printHttpLog(): Boolean {
                    return false
                }
            }) //                .setInterceptor(new WanAndroidCookieInterceptor())
            .build()
        RetrofitHelper.getInstance().init(retrofitParams)
    }

    /**
     * 初始化X5内核
     */
    private fun preInitX5Core() {
        //预加载x5内核
       /* val intent = Intent(this, X5CoreService::class.java)
        //开启服务兼容
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }*/
        CachedThreadManager.getInstance().execute{
            var service = X5CoreService()
            service.initX5(getContext())
        }
    }

    /**
     * 在onCreate之前执行
     *
     * @param base
     */
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this) //https://developer.android.com/studio/build/multidex
    }
}