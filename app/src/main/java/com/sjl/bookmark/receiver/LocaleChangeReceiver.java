package com.sjl.bookmark.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sjl.bookmark.kotlin.language.LanguageManager;
import com.sjl.core.util.log.LogUtils;

/**
 * 系统语言切换监听
 *
 * @author Kelly
 * @version 1.0.0
 * @filename LocaleChangeReceiver.java
 * @time 2019/7/25 14:04
 * @copyright(C) 2019 song
 */
@Deprecated
public class LocaleChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_LOCALE_CHANGED)) {
            LogUtils.w("=======监听到在切换系统语言=======");
            LanguageManager.INSTANCE.initAppLanguage(context);
        }
    }
}

