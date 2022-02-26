package com.sjl.bookmark.ui.presenter;

import android.content.Intent;

import com.sjl.bookmark.R;
import com.sjl.bookmark.app.AppConstant;
import com.sjl.bookmark.ui.contract.CreateLockContract;
import com.sjl.bookmark.widget.LockPatternUtils;
import com.sjl.bookmark.widget.LockPatternView;
import com.sjl.core.util.PreferencesHelper;

import java.util.List;

/**
 * 设置手势密码presenter
 *
 * @author Kelly
 * @version 1.0.0
 * @filename CreateLockPresenter.java
 * @time 2018/3/5 13:56
 * @copyright(C) 2018 song
 */
public class CreateLockPresenter extends CreateLockContract.Presenter {
    private static final String CREATE_LOCK_SUCCESS = "CREATE_LOCK_SUCCESS";
    private boolean isFinishOnce = false;
    private int createMode;

    @Override
    public void init(Intent intent) {
        createMode = intent.getIntExtra("CREATE_MODE", AppConstant.SETTING.CREATE_MODE);
        if (createMode == AppConstant.SETTING.CREATE_GESTURE) {
            mView.setTitle(mContext.getString(R.string.setting_gesture_pwd));
        } else if (createMode == AppConstant.SETTING.UPDATE_GESTURE) {
            mView.setTitle(mContext.getString(R.string.setting_update_gesture_pwd));
        }
    }

    @Override
    public void fingerPress() {
        mView.showLockMsg(mContext.getString(R.string.finger_press));
    }

    public void fingerFirstUpError() {
        mView.showLockMsg(mContext.getString(R.string.finger_up_first_error));

    }

    public void fingerFirstUpSuccess() {
        mView.showLockMsg(mContext.getString(R.string.finger_up_first_success));

    }

    public void fingerSecondUpError() {
        mView.showLockMsg(mContext.getString(R.string.finger_up_second_error));

    }

    public void fingerSecondUpSucess() {
        mView.showLockMsg(mContext.getString(R.string.finger_up_second_success));

    }

    @Override
    public void check(List<LockPatternView.Cell> pattern) {
        if (pattern == null) return;

        if (pattern.size() < LockPatternUtils.MIN_PATTERN_REGISTER_FAIL) {//小于四个点

            if (!isFinishOnce) {
                fingerFirstUpError();
            } else {
                fingerSecondUpError();
            }
            mView.lockDisplayError();

        } else {

            if (!isFinishOnce) {
                fingerFirstUpSuccess();
                LockPatternUtils instances = LockPatternUtils.getInstances(mContext);
                if (createMode == AppConstant.SETTING.CREATE_GESTURE) {//设置手势
                    instances.saveLockPattern(pattern);
                } else if (createMode == AppConstant.SETTING.UPDATE_GESTURE) {//更新手势
                    instances.saveLockPattern(pattern);
                }
                mView.clearPattern();
                isFinishOnce = true;
            } else {
                LockPatternUtils instances = LockPatternUtils.getInstances(mContext);
                if (createMode == AppConstant.SETTING.CREATE_GESTURE) {//设置手势

                    if (instances.checkPattern(pattern)) {
                        fingerSecondUpSucess();
                        PreferencesHelper preferencesHelper = PreferencesHelper.getInstance(mContext);
                        preferencesHelper.put(CREATE_LOCK_SUCCESS, true);
                        mView.setResults(1);
                        mView.kill();
                    } else {
                        fingerSecondUpError();
                        mView.lockDisplayError();
                    }
                    isFinishOnce = false;
                } else if (createMode == AppConstant.SETTING.UPDATE_GESTURE) {//更新
                    if (instances.checkPattern(pattern)) {
                        instances.saveLockPattern(pattern);
                        fingerSecondUpSucess();
                        PreferencesHelper preferencesHelper = PreferencesHelper.getInstance(mContext);
                        preferencesHelper.put(CREATE_LOCK_SUCCESS, true);
                        mView.setResults(2);
                        mView.kill();
                    } else {
                        fingerSecondUpError();
                        mView.lockDisplayError();
                    }
                    isFinishOnce = false;
                }
            }
        }
    }

    @Override
    public void onBack() {
        if (createMode == AppConstant.SETTING.CREATE_GESTURE) {
            mView.setResults(10);
        } else if (createMode == AppConstant.SETTING.UPDATE_GESTURE) {
            mView.setResults(20);
        }
    }


}
