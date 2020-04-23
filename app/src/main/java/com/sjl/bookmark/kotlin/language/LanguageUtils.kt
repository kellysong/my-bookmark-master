package com.sjl.bookmark.kotlin.language

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.os.LocaleList
import java.util.*

/**
 *
 * 不需要替换Context,替换会导致某些SDK方法调用存在安全隐患
 * 参考:
 *
 * @see <a href="https://blog.csdn.net/haha_zhan/article/details/81331719">https://blog.csdn.net/haha_zhan/article/details/81331719</a>
 *
 * @author Kelly
 * @version 1.0.0
 * @filename LanguageUtils.java
 * @time 2019/7/23 16:10
 * @copyright(C) 2019 song
 */
object LanguageUtils {

    fun applyLanguage(context: Context, locale: Locale) {
        val resources = context.resources
        val configuration = resources.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // apply locale
            configuration.setLocale(locale)
        } else {
            // updateConfiguration
            configuration.locale = locale
            val dm = resources.displayMetrics
            resources.updateConfiguration(configuration, dm)
        }
        Locale.setDefault(locale)
    }

    fun attachBaseContext(context: Context, locale: Locale): Context {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return createConfigurationResources(context, locale)
        } else {
            applyLanguage(context, locale)
            return context
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun createConfigurationResources(context: Context, locale: Locale): Context {
        val resources = context.resources
        val configuration = resources.configuration
        configuration.setLocale(locale)

        val dm = resources.displayMetrics


        val localeList = LocaleList(locale)
        LocaleList.setDefault(localeList)
        configuration.locales = localeList
        Locale.setDefault(locale)
        resources.updateConfiguration(configuration, dm)        //必须加,否则无效

        return context.createConfigurationContext(configuration)
    }
}
