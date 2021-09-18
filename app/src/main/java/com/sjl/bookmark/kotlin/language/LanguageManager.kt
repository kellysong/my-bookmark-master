package com.sjl.bookmark.kotlin.language

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.os.LocaleList
import com.sjl.bookmark.app.MyApplication
import com.sjl.bookmark.kotlin.language.LanguageConstant.LANGUAGE_TYPE
import com.sjl.core.util.log.LogUtils
import com.sjl.core.util.PreferencesHelper
import java.util.*

/**
 * 国际化语言管理，适配8.0以上及以下
 * @author Kelly
 * @version 1.0.0
 * @filename LanguageManager.kt
 * @time 2019/7/22 16:46
 * @copyright(C) 2019 song
 */
object LanguageManager {
    private val mSupportLanguages = object : HashMap<Int, Locale>(5) {
        init {
            put(LanguageConstant.LANGUAGE_TYPE_DEFAULT, getSystemPreferredLanguage())
            put(LanguageConstant.LANGUAGE_TYPE_CN, Locale.SIMPLIFIED_CHINESE)
            put(LanguageConstant.LANGUAGE_TYPE_TW, Locale.TRADITIONAL_CHINESE)
            put(LanguageConstant.LANGUAGE_TYPE_HK, Locale("zh", "HK"))
            put(LanguageConstant.LANGUAGE_TYPE_EN, Locale.ENGLISH)
        }
    }
    /**
     * 语言上下文
     */
    var context: Context? = null


    /**
     * 初始化app语言
     *
     * @param context 对于8.0以下的系统， 上文代码中的 mContext 采用 ApplicationContext 可以正确的切换应用的语言类型
     * 但在8.0 系统中，若 mContext 采用 ApplicationContext 则无法切换应用的语言类型。
     */
    fun initAppLanguage(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            changeLanguage(context, getCurrentLanguageType(context))//必须是activity的Context
        } else {
            changeLanguage(context, getCurrentLanguageType(MyApplication.getContext()))
        }
    }

    /**
     * 改变语言
     *
     * @param context 上下文
     * @param languageType 语言种类
     */
    fun changeLanguage(context: Context, languageType: Int?) {
        LogUtils.i("1.languageType:" + languageType + ",get LocaleString:" + getLocaleString(Locale.getDefault()))
        var locale: Locale
        if (languageType == null) {//如果没有指定语言使用系统首选语言
            locale = getSystemPreferredLanguage()
        } else {//指定了语言使用指定语言
            locale = getSupportLanguage(languageType)!!
        }
        val attachBaseContext = LanguageUtils.attachBaseContext(context, locale)
        LanguageManager.context = attachBaseContext
        LogUtils.i("2.languageType:" + languageType + ",get LocaleString:" + getLocaleString(Locale.getDefault()))
        PreferencesHelper.getInstance(context).put(LANGUAGE_TYPE, languageType)
    }


    fun getLocalContext(context: Context): Context {
        var languageType: Int = -1
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            languageType = getCurrentLanguageType(context)
        } else {
            languageType = getCurrentLanguageType(MyApplication.getContext())
        }
        var locale: Locale
        if (languageType == null) {//如果没有指定语言使用系统首选语言
            locale = getSystemPreferredLanguage()
        } else {//指定了语言使用指定语言
            locale = getSupportLanguage(languageType)!!
        }
        val attachBaseContext = LanguageUtils.attachBaseContext(context, locale)
        return attachBaseContext
    }

    /**
     * 是否支持此语言
     *
     * @param language language
     * @return true:支持 false:不支持
     */
    fun isSupportLanguage(language: Int): Boolean {
        return mSupportLanguages.containsKey(language)
    }

    /**
     * 获取支持语言
     *
     * @param language language
     * @return 支持返回支持语言，不支持返回系统首选语言
     */
    @TargetApi(Build.VERSION_CODES.N)
    fun getSupportLanguage(language: Int): Locale? {
        if (isSupportLanguage(language)) {
            return mSupportLanguages.get(language)
        } else {
            return getSystemPreferredLanguage()
        }
    }

    /**
     * 获取系统首选语言
     *
     * @return Locale
     */

    fun getSystemPreferredLanguage(): Locale {
        val locale: Locale
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0)
        } else {
            locale = Locale.getDefault()
        }
        return locale
    }

    /**
     * 获取当前语言的上下文
     *@param context
     */
    fun getCurrentLanguageType(context: Context): Int {
        var type = PreferencesHelper.getInstance(context).getInteger(LANGUAGE_TYPE, 0)
        if (type <= 0) {
            type = 0
        }
        return type
    }


    private fun getLocaleString(locale: Locale): String {
        if (locale == null) {
            return "";
        } else {
            return locale.language + locale.country;
        }
    }


}