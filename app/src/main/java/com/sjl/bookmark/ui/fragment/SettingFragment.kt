package com.sjl.bookmark.ui.fragment

import android.app.ProgressDialog
import android.content.Intent
import android.preference.Preference
import android.text.TextUtils
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.sjl.bookmark.R
import com.sjl.bookmark.app.AppConstant
import com.sjl.bookmark.app.MyApplication
import com.sjl.bookmark.ui.activity.ChangeSkinActivity
import com.sjl.bookmark.ui.contract.SettingContract
import com.sjl.bookmark.ui.presenter.SettingPresenter
import com.sjl.core.mvp.BasePreferenceFragment
import com.sjl.core.util.PreferencesHelper
import com.sjl.core.util.log.LogUtils
import com.sjl.core.widget.materialpreference.SwitchPreference

/**
 * 设置Fragment
 */
class SettingFragment : BasePreferenceFragment<SettingPresenter>(), SettingContract.View {
    private lateinit var mProgressDialog: ProgressDialog
    private lateinit var mCircleDialog: ProgressDialog
    private var loadingType = 0
    override fun setClickPreferenceKey(preference: Preference, key: String) {
        mPresenter.setClickPreferenceKey(preference, key)
    }

    override fun getPreferencesResId(): Int {
        return R.xml.setting_preference_xml
    }

    override fun initView() {
        //进度条对话框
        mProgressDialog = ProgressDialog(activity)
        // 设置对话框参数
        mProgressDialog.setMessage(getString(R.string.updating))
        mProgressDialog.setCancelable(false)
        // 设置进度条参数
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        mProgressDialog.max = 100
        mProgressDialog.isIndeterminate = false // 填false表示是明确显示进度的 填true表示不是明确显示进度的
        mProgressDialog.setOnCancelListener { LogUtils.i("mProgressDialog close.") }

        //圆形对话框
        mCircleDialog = ProgressDialog(activity)
        // 设置对话框参数
        mCircleDialog.setMessage(getString(R.string.synchronizing))
        mCircleDialog.setCancelable(false)
    }

    override fun initListener() {}
    override fun initData() {
        mPresenter.init()
        val preference = preferenceScreen.findPreference("关于")
        preference.summary = "V" + MyApplication.getAppVersion()
    }

    override fun openChangeThemeActivity() {
        openActivity(ChangeSkinActivity::class.java)
    }

    override fun readyGo(clazz: Class<*>, intent: Intent) {
        startActivityForResult(intent, 0)
    }

    override fun showLoading(type: Int, msg: String) {
        loadingType = type
        if (type == 0) {
            if (!TextUtils.isEmpty(msg)) {
                mProgressDialog.setMessage(msg)
            }
            mProgressDialog.progress = 0 //每次显示之前重置下载进度
            mProgressDialog.show()
        } else {
            if (!TextUtils.isEmpty(msg)) {
                mCircleDialog.setMessage(msg)
            }
            mCircleDialog.show()
        }
    }

    override fun hideLoading(ret: Boolean, errorMsg: String) {
        if (loadingType == 0) {
            mProgressDialog.cancel()
        } else {
            mCircleDialog.cancel()
        }
        if (ret) {
            if (TextUtils.isEmpty(errorMsg)) {
                return
            }
            Toast.makeText(activity, errorMsg, Toast.LENGTH_SHORT).show()
        } else {
            if (TextUtils.isEmpty(errorMsg)) {
                return
            }
            Toast.makeText(activity, errorMsg, Toast.LENGTH_LONG).show()
        }
    }

    override fun update(percent: Int) {
        LogUtils.i("下载进度：$percent")
        mProgressDialog.progress = percent
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        LogUtils.i("resultCode=$resultCode")
        if (requestCode == 0) {
            if (resultCode == 1) {
                showSnackBar(getString(R.string.gesture_pwd_hint))
            } else if (resultCode == 2) {
                showSnackBar(getString(R.string.gesture_pwd_hint2))
            } else if (resultCode == 10) { //放弃设置手势密码
                val preferencesHelper = PreferencesHelper.getInstance(activity)
                preferencesHelper.put(AppConstant.SETTING.OPEN_GESTURE, false)
                val openGesture = findPreference("打开手势密码") as SwitchPreference
                openGesture.isChecked = false
                mPresenter.resetGestureFlag()
            } else if (resultCode == 20) {
                showSnackBar(getString(R.string.gesture_pwd_hint3))
            }
        }
    }

    private fun showSnackBar(msg: String) {
        view?.let { Snackbar.make(it, msg, Snackbar.LENGTH_SHORT).show() }
    }
}