package com.sjl.bookmark.kotlin.language;

import android.app.Activity;
import android.content.Context;
import android.os.Build;

import com.sjl.bookmark.app.MyApplication;
import com.sjl.core.manager.MyActivityManager;
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
     * <p>
     * LogUtils.w("===========myPid: " + android.os.Process.myPid());
     * //https://www.v2ex.com/t/357347
     * //context为空，原因估计是当切换系统语言的时候，桌面被重新刷新，进程被杀死，单例中的全局变量都是默认值，需要重新初始化。类似，google官方说过，app crash后，系统会自动kill掉进程，释放内存
     * </p>
     *
     * @param id
     * @return
     */
    public static String getString(int id) {
        MyActivityManager instance = MyActivityManager.getInstance();
        Context activity = instance.getCurrentActivity();
        if (activity == null) {
            List<Activity> activityList = instance.getActivityList();
            if (activityList == null || activityList.isEmpty()) {
                return ViewUtils.getString(id);
            } else {
                activity = activityList.get(0);
            }
        }
        LanguageManager.INSTANCE.initAppLanguage(activity);
        Context context = LanguageManager.INSTANCE.getContext();
        return context.getString(id);

    }

}
