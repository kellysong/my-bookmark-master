package com.sjl.bookmark.kotlin.darkmode

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import cn.feng.skin.manager.loader.SkinManager
import com.sjl.bookmark.app.AppConstant
import com.sjl.bookmark.app.MyApplication
import com.sjl.core.util.PreferencesHelper

/**
 * TODO
 * @author Kelly
 * @version 1.0.0
 * @filename NightModeUtils
 * @time 2020/6/2 16:30
 * @copyright(C) 2020 song
 */

object DarkModeUtils {

    /**
     *跟随系统
     */
    const val MODE_DEFAULT= 0


    /**
     * 普通模式
     */
    const val MODE_NORMAL = 1

    /**
     * 深色模式
     */
    const val  MODE_NIGHT = 2

    private fun isNightMode(config: Configuration): Boolean {
        val uiMode = config.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return uiMode == Configuration.UI_MODE_NIGHT_YES
    }

    fun isNightMode(context: Context): Boolean {
        return isNightMode(context.resources.configuration)
    }


    /**
     * 初始化暗黑模式
     */
    fun initDarkMode() {
        setDarkMode(getDarkModeType())
    }

    /**
     * 设置暗黑模式
     * @param modeType
     */
    fun setDarkMode(modeType: Int) {
        when (modeType) {
            MODE_DEFAULT -> {//跟随系统
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
            MODE_NORMAL -> {//普通模式
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            MODE_NIGHT -> {//深色模式
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
        PreferencesHelper.getInstance(MyApplication.getContext()).putSync(AppConstant.SETTING.DARK_THEME,modeType)
    }

    /**
     * 当前模式
     */
    fun getDarkModeType():Int {
        var type = PreferencesHelper.getInstance(MyApplication.getContext()).getInteger(AppConstant.SETTING.DARK_THEME, MODE_DEFAULT)
        if (type <= 0) {
            type = 0
        }
        return type
    }
}
