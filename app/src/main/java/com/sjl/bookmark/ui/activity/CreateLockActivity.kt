package com.sjl.bookmark.ui.activity;

import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.sjl.bookmark.R;
import com.sjl.bookmark.kotlin.language.I18nUtils;
import com.sjl.bookmark.ui.contract.CreateLockContract;
import com.sjl.bookmark.ui.presenter.CreateLockPresenter;
import com.sjl.bookmark.widget.LockPatternView;
import com.sjl.core.mvp.BaseActivity;
import com.sjl.core.util.log.LogUtils;

import java.util.List;

import butterknife.BindView;

/**
 * 设置手势密码Activity
 *
 * @author Kelly
 * @version 1.0.0
 * @filename CreateLockActivity.java
 * @time 2018/3/5 10:00
 * @copyright(C) 2018 song
 */
public class CreateLockActivity extends BaseActivity<CreateLockPresenter> implements CreateLockContract.View, LockPatternView.OnPatternListener {
    @BindView(R.id.common_toolbar)
    Toolbar toolbar;
    @BindView(R.id.lockPatternView)
    LockPatternView mLockPatternView;
    @BindView(R.id.iv_warn_msg)
    TextView mWarnMsg;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_create_lock;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {
        bindingToolbar(toolbar, I18nUtils.getString(R.string.title_setting_gesture_pwd));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onBack();
                finish();
            }
        });
        mLockPatternView.setOnPatternListener(this);
    }

    @Override
    protected void initData() {
        mPresenter.init(getIntent());
    }


    @Override
    public void lockDisplayError() {
        mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
    }

    @Override
    public void setTitle(String title) {
        toolbar.setTitle(title);
    }

    @Override
    public void setResults(int isSuccess) {
        setResult(isSuccess);
    }

    @Override
    public void clearPattern() {

    }

    @Override
    public void kill() {
        finish();
    }

    @Override
    public void showLockMsg(String msg) {
        mWarnMsg.setText(msg);
    }

    @Override
    public void onPatternStart() {
        LogUtils.i("onPatternStart");
        mPresenter.fingerPress();
    }

    @Override
    public void onPatternCleared() {
        LogUtils.i("onPatternCleared");
    }

    @Override
    public void onPatternCellAdded(List<LockPatternView.Cell> pattern) {
        LogUtils.i("onPatternCellAdded");

    }

    @Override
    public void onPatternDetected(List<LockPatternView.Cell> pattern) {
        LogUtils.i("onPatternDetected");
        mPresenter.check(pattern);
    }

    @Override
    public void onBackPressed() {
        mPresenter.onBack();
        super.onBackPressed();
    }
}
