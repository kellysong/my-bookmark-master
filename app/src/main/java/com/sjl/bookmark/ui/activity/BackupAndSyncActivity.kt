package com.sjl.bookmark.ui.activity;

import com.sjl.bookmark.R;
import com.sjl.bookmark.kotlin.language.I18nUtils;
import com.sjl.bookmark.ui.base.extend.BaseSwipeBackActivity;
import com.sjl.bookmark.ui.fragment.BackupAndSyncFragment;
import com.sjl.core.net.RxLifecycleUtils;

import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;

/**
 * 收藏同步与恢复
 */
public class BackupAndSyncActivity extends BaseSwipeBackActivity {
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
                .replace(R.id.container, new BackupAndSyncFragment())
                .commit();
    }

    @Override
    protected void initListener() {
        bindingToolbar(mToolBar, I18nUtils.getString(R.string.title_backup_sync));
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
