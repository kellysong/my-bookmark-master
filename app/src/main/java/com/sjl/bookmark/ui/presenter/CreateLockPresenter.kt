package com.sjl.bookmark.ui.presenter

import android.content.Intent
import com.sjl.bookmark.R
import com.sjl.bookmark.app.AppConstant
import com.sjl.bookmark.ui.contract.CreateLockContract
import com.sjl.bookmark.widget.LockPatternUtils
import com.sjl.bookmark.widget.LockPatternView
import com.sjl.core.util.PreferencesHelper

/**
 * 设置手势密码presenter
 *
 * @author Kelly
 * @version 1.0.0
 * @filename CreateLockPresenter.java
 * @time 2018/3/5 13:56
 * @copyright(C) 2018 song
 */
class CreateLockPresenter : CreateLockContract.Presenter() {
    private var isFinishOnce: Boolean = false
    private var createMode: Int = 0
    override fun init(intent: Intent) {
        createMode = intent.getIntExtra("CREATE_MODE", AppConstant.SETTING.CREATE_MODE)
        if (createMode == AppConstant.SETTING.CREATE_GESTURE) {
            mView.setTitle(mContext.getString(R.string.setting_gesture_pwd))
        } else if (createMode == AppConstant.SETTING.UPDATE_GESTURE) {
            mView.setTitle(mContext.getString(R.string.setting_update_gesture_pwd))
        }
    }

    override fun fingerPress() {
        mView.showLockMsg(mContext.getString(R.string.finger_press))
    }

    fun fingerFirstUpError() {
        mView.showLockMsg(mContext.getString(R.string.finger_up_first_error))
    }

    fun fingerFirstUpSuccess() {
        mView.showLockMsg(mContext.getString(R.string.finger_up_first_success))
    }

    fun fingerSecondUpError() {
        mView.showLockMsg(mContext.getString(R.string.finger_up_second_error))
    }

    fun fingerSecondUpSucess() {
        mView.showLockMsg(mContext.getString(R.string.finger_up_second_success))
    }

    override fun check(pattern: List<LockPatternView.Cell>) {
        if (pattern == null) return
        if (pattern.size < LockPatternUtils.MIN_PATTERN_REGISTER_FAIL) { //小于四个点
            if (!isFinishOnce) {
                fingerFirstUpError()
            } else {
                fingerSecondUpError()
            }
            mView.lockDisplayError()
        } else {
            if (!isFinishOnce) {
                fingerFirstUpSuccess()
                val instances: LockPatternUtils = LockPatternUtils.getInstances(mContext)
                if (createMode == AppConstant.SETTING.CREATE_GESTURE) { //设置手势
                    instances.saveLockPattern(pattern)
                } else if (createMode == AppConstant.SETTING.UPDATE_GESTURE) { //更新手势
                    instances.saveLockPattern(pattern)
                }
                mView.clearPattern()
                isFinishOnce = true
            } else {
                val instances: LockPatternUtils = LockPatternUtils.getInstances(mContext)
                if (createMode == AppConstant.SETTING.CREATE_GESTURE) { //设置手势
                    if (instances.checkPattern(pattern)) {
                        fingerSecondUpSucess()
                        val preferencesHelper: PreferencesHelper =
                            PreferencesHelper.getInstance(mContext)
                        preferencesHelper.put(CREATE_LOCK_SUCCESS, true)
                        mView.setResults(1)
                        mView.kill()
                    } else {
                        fingerSecondUpError()
                        mView.lockDisplayError()
                    }
                    isFinishOnce = false
                } else if (createMode == AppConstant.SETTING.UPDATE_GESTURE) { //更新
                    if (instances.checkPattern(pattern)) {
                        instances.saveLockPattern(pattern)
                        fingerSecondUpSucess()
                        val preferencesHelper: PreferencesHelper =
                            PreferencesHelper.getInstance(mContext)
                        preferencesHelper.put(CREATE_LOCK_SUCCESS, true)
                        mView.setResults(2)
                        mView.kill()
                    } else {
                        fingerSecondUpError()
                        mView.lockDisplayError()
                    }
                    isFinishOnce = false
                }
            }
        }
    }

    override fun onBack() {
        if (createMode == AppConstant.SETTING.CREATE_GESTURE) {
            mView.setResults(10)
        } else if (createMode == AppConstant.SETTING.UPDATE_GESTURE) {
            mView.setResults(20)
        }
    }

    companion object {
        private val CREATE_LOCK_SUCCESS: String = "CREATE_LOCK_SUCCESS"
    }
}