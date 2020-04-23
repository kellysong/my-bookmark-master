package com.sjl.bookmark.ui.base.extend;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.sjl.bookmark.R;
import com.sjl.core.mvp.BaseActivity;

import cn.feng.skin.manager.loader.SkinManager;
import cn.feng.skin.manager.statusbar.StatusBarUtil;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.Utils;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityBase;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityHelper;

/**
 * 重写库中SwipeBackActivity，方便扩展
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BaseSwipeBackActivity.java
 * @time 2018/3/6 14:16
 * @copyright(C) 2018 song
 */
public abstract class BaseSwipeBackActivity extends BaseActivity implements SwipeBackActivityBase {
    private SwipeBackActivityHelper mHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHelper = new SwipeBackActivityHelper(this);
        mHelper.onActivityCreate();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mHelper.onPostCreate();
    }

    @Override
    public View findViewById(int id) {
        View v = super.findViewById(id);
        if (v == null && mHelper != null)
            return mHelper.findViewById(id);
        return v;
    }

    @Override
    public SwipeBackLayout getSwipeBackLayout() {
        return mHelper.getSwipeBackLayout();
    }

    @Override
    public void setSwipeBackEnable(boolean enable) {
        getSwipeBackLayout().setEnableGesture(enable);
    }

    @Override
    public void scrollToFinishActivity() {
        Utils.convertActivityToTranslucent(this);
        getSwipeBackLayout().scrollToFinishActivity();
    }

    /**
     * 为滑动返回界面设置状态栏颜色
     * 主要适配需要滑动关闭的页面，否则状态栏会出现透明颜色，很难看
     */
    protected void setColorForSwipeBack() {
        int color = SkinManager.getInstance().getColorPrimary();
        int statusBarColor;
        if (color != -1) {
            statusBarColor = color;
        } else {
            statusBarColor = getResources().getColor(R.color.colorPrimary);
        }
        StatusBarUtil.setColorForSwipeBack(this, statusBarColor, 0);
    }


}
