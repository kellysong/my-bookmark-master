package com.sjl.bookmark.kotlin.language;

import android.content.Context;
import android.os.Build;

import com.sjl.core.mvp.BaseActivity;
import com.sjl.core.util.log.LogUtils;
import com.sjl.core.util.ViewUtils;

import java.util.List;

/**
 * 国际化字符工具类
 *
 * @author Kelly
 * @version 1.0.0
 * @filename I18nUtils.java
 * @time 2019/7/24 10:27
 * @copyright(C) 2019 song
 */
public class I18nUtils {
    /**
     * 获取字符串
     *
     * @param id
     * @return
     */
    public static String getString(int id) {
//        LogUtils.w("===========myPid: " + android.os.Process.myPid());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//7.0以上不能使用Application Context，否则国际化无效
            Context context = LanguageManager.INSTANCE.getContext();
            if (context == null) {
                //                LogUtils.w("===========myPid: " + android.os.Process.myPid());

                List<BaseActivity> activities = BaseActivity.getActivities();
                //https://www.v2ex.com/t/357347
                //context为空，原因估计是当切换系统语言的时候，桌面被重新刷新，进程被杀死，单例中的全局变量都是默认值，需要重新初始化。类似，google官方说过，app crash后，系统会自动kill掉进程，释放内存

                LogUtils.w("I18n context is null.");
                if (activities.isEmpty()) {
                    throw new RuntimeException("activities is null.");
                }
                BaseActivity baseActivity = activities.get(0);
                LanguageManager.INSTANCE.initAppLanguage(baseActivity);
                context = LanguageManager.INSTANCE.getContext();
            }
            return context.getString(id);
        } else {
            return ViewUtils.getString(id);
        }
    }
}
