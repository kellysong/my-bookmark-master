package com.sjl.bookmark.ui.activity;

import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.sjl.bookmark.R;
import com.sjl.bookmark.ui.contract.CheckLockContract;
import com.sjl.bookmark.ui.presenter.CheckLockPresenter;
import com.sjl.bookmark.widget.LockPatternView;
import com.sjl.core.mvp.BaseActivity;
import com.sjl.core.util.log.LogUtils;

import java.util.List;

import butterknife.BindView;

/**
 * 手势密码验证Activity
 *
 * @author Kelly
 * @version 1.0.0
 * @filename CheckLockActivity.java
 * @time 2018/3/5 10:05
 * @copyright(C) 2018 song
 */
public class CheckLockActivity extends BaseActivity<CheckLockPresenter> implements CheckLockContract.View,LockPatternView.OnPatternListener {
    @BindView(R.id.lockPatternView)
    LockPatternView mLockPatternView;
    @BindView(R.id.show_text)
    TextView mShowText;

    @Override
    protected int getLayoutId() {
        setStatusBar(0xffAFAFAF);
        return R.layout.activity_check_lock;
    }

    @Override
    protected void initView() {

    }



    @Override
    protected void initListener() {
        mLockPatternView.setOnPatternListener(this);
    }

    @Override
    protected void initData() {
    }


    @Override
    public void lockDisplayError() {
        mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
        mShowText.setText(getString(R.string.check_error));
        mShowText.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_x));
    }

    @Override
    public void kill() {
        finish();
    }


    @Override
    public void onPatternStart() {
        LogUtils.i("onPatternStart");

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
}
