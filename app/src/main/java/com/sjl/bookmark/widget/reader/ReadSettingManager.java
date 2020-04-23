package com.sjl.bookmark.widget.reader;


import com.sjl.bookmark.app.MyApplication;
import com.sjl.bookmark.widget.reader.bean.PageMode;
import com.sjl.bookmark.widget.reader.bean.PageStyle;
import com.sjl.core.util.PreferencesHelper;
import com.sjl.core.util.ViewUtils;

/**
 *
 * 阅读器的配置管理
 */

public class ReadSettingManager {
    /*************实在想不出什么好记的命名方式。。******************/
    public static final int READ_BG_DEFAULT = 0;
    public static final int READ_BG_1 = 1;
    public static final int READ_BG_2 = 2;
    public static final int READ_BG_3 = 3;
    public static final int READ_BG_4 = 4;
    public static final int NIGHT_MODE = 5;

    public static final String SHARED_READ_BG = "shared_read_bg";
    public static final String SHARED_READ_BRIGHTNESS = "shared_read_brightness";
    public static final String SHARED_READ_IS_BRIGHTNESS_AUTO = "shared_read_is_brightness_auto";
    public static final String SHARED_READ_TEXT_SIZE = "shared_read_text_size";
    public static final String SHARED_READ_IS_TEXT_DEFAULT = "shared_read_text_default";
    public static final String SHARED_READ_PAGE_MODE = "shared_read_mode";
    public static final String SHARED_READ_NIGHT_MODE = "shared_night_mode";
    public static final String SHARED_READ_VOLUME_TURN_PAGE = "shared_read_volume_turn_page";
    public static final String SHARED_READ_FULL_SCREEN = "shared_read_full_screen";
    public static final String SHARED_READ_CONVERT_TYPE = "shared_read_convert_type";

    private static volatile ReadSettingManager sInstance;

    private PreferencesHelper preferencesHelper;

    public static ReadSettingManager getInstance() {
        if (sInstance == null) {
            synchronized (ReadSettingManager.class) {
                if (sInstance == null) {
                    sInstance = new ReadSettingManager();
                }
            }
        }
        return sInstance;
    }

    private ReadSettingManager() {
        preferencesHelper = PreferencesHelper.getInstance(MyApplication.getContext());
    }

    public void setPageStyle(PageStyle pageStyle) {
        preferencesHelper.put(SHARED_READ_BG, pageStyle.ordinal());
    }

    public void setBrightness(int progress) {
        preferencesHelper.put(SHARED_READ_BRIGHTNESS, progress);
    }

    public void setAutoBrightness(boolean isAuto) {
        preferencesHelper.put(SHARED_READ_IS_BRIGHTNESS_AUTO, isAuto);
    }

    public void setDefaultTextSize(boolean isDefault) {
        preferencesHelper.put(SHARED_READ_IS_TEXT_DEFAULT, isDefault);
    }

    public void setTextSize(int textSize) {
        preferencesHelper.put(SHARED_READ_TEXT_SIZE, textSize);
    }

    public void setPageMode(PageMode mode) {
        preferencesHelper.put(SHARED_READ_PAGE_MODE, mode.ordinal());
    }

    public void setNightMode(boolean isNight) {
        preferencesHelper.put(SHARED_READ_NIGHT_MODE, isNight);
    }

    public int getBrightness() {
        return preferencesHelper.getInteger(SHARED_READ_BRIGHTNESS, 40);
    }

    public boolean isBrightnessAuto() {
        return preferencesHelper.getBoolean(SHARED_READ_IS_BRIGHTNESS_AUTO, false);
    }

    public int getTextSize() {
        return preferencesHelper.getInteger(SHARED_READ_TEXT_SIZE, ViewUtils.sp2px(MyApplication.getContext(),16));
    }

    public boolean isDefaultTextSize() {
        return preferencesHelper.getBoolean(SHARED_READ_IS_TEXT_DEFAULT, false);
    }

    public PageMode getPageMode() {
        int mode = preferencesHelper.getInteger(SHARED_READ_PAGE_MODE, PageMode.SIMULATION.ordinal());
        return PageMode.values()[mode];
    }

    public PageStyle getPageStyle() {
        int style = preferencesHelper.getInteger(SHARED_READ_BG, PageStyle.BG_0.ordinal());
        return PageStyle.values()[style];
    }

    public boolean isNightMode() {
        return preferencesHelper.getBoolean(SHARED_READ_NIGHT_MODE, false);
    }

    public void setVolumeTurnPage(boolean isTurn) {
        preferencesHelper.put(SHARED_READ_VOLUME_TURN_PAGE, isTurn);
    }

    public boolean isVolumeTurnPage() {
        return preferencesHelper.getBoolean(SHARED_READ_VOLUME_TURN_PAGE, false);
    }

    public void setFullScreen(boolean isFullScreen) {
        preferencesHelper.put(SHARED_READ_FULL_SCREEN, isFullScreen);
    }

    public boolean isFullScreen() {
        return preferencesHelper.getBoolean(SHARED_READ_FULL_SCREEN, false);
    }

    /**
     * 设置阅读器语言转换种类
     * @param convertType
     */
    public void setConvertType(int convertType) {
        preferencesHelper.put(SHARED_READ_CONVERT_TYPE, convertType);
    }

    /**
     * 获取阅读器语言转换种类
     * @return
     */
    public int getConvertType() {
        return preferencesHelper.getInteger(SHARED_READ_CONVERT_TYPE, 0);
    }
}
