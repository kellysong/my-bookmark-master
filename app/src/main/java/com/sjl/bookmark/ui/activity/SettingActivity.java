package com.sjl.bookmark.ui.activity;

import android.support.v7.widget.Toolbar;

import com.sjl.bookmark.R;
import com.sjl.bookmark.kotlin.language.I18nUtils;
import com.sjl.bookmark.ui.base.extend.BaseSwipeBackActivity;
import com.sjl.bookmark.ui.fragment.SettingFragment;
import com.sjl.core.net.RxLifecycleUtils;

import butterknife.BindView;

/**
 * 设置Activity
 *
 * @author Kelly
 * @version 1.0.0
 * @filename SettingActivity.java
 * @time 2018/3/6 14:28
 * @copyright(C) 2018 song
 */
public class SettingActivity extends BaseSwipeBackActivity {
    @BindView(R.id.common_toolbar)
    Toolbar mToolBar;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void changeStatusBarColor() {
        setColorForSwipeBack();
    }


    @Override
    protected void initView() {
        setFragment();
    }

    private void setFragment() {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new SettingFragment())
                .commit();
    }

    @Override
    protected void initListener() {
        bindingToolbar(mToolBar,  I18nUtils.getString(R.string.title_setting));
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        RxLifecycleUtils.setLifecycleOwner(this);
    }
}
