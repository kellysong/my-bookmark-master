package com.sjl.bookmark.ui.fragment

import android.app.ProgressDialog
import android.preference.Preference
import android.text.TextUtils
import android.widget.Toast
import com.sjl.bookmark.R
import com.sjl.bookmark.ui.contract.BackupAndSyncContract
import com.sjl.bookmark.ui.presenter.BackupAndSyncPresenter
import com.sjl.core.mvp.BasePreferenceFragment

/**
 * 同步与恢复
 */
class BackupAndSyncFragment : BasePreferenceFragment<BackupAndSyncPresenter>(),
    BackupAndSyncContract.View {
    private lateinit var mCircleDialog: ProgressDialog
    override fun getPreferencesResId(): Int {
        return R.xml.backup_sync_preference_xml
    }

    override fun setClickPreferenceKey(preference: Preference, key: String) {
        mPresenter.setClickPreferenceKey(key)
    }

    override fun initView() {
        //圆形对话框
        mCircleDialog = ProgressDialog(activity)
        // 设置对话框参数
        mCircleDialog.setMessage("正在同步...")
        mCircleDialog.setCancelable(false)
    }

    override fun initListener() {}
    override fun initData() {
        mPresenter.init()
    }

    override fun showLoading(msg: String) {
        if (!TextUtils.isEmpty(msg)) {
            mCircleDialog.setMessage(msg)
        }
        mCircleDialog.show()
    }

    override fun hideLoading(errorMsg: String) {
        mCircleDialog.cancel()
        if (TextUtils.isEmpty(errorMsg)) {
            return
        }
        Toast.makeText(activity, errorMsg, Toast.LENGTH_SHORT).show()
    }
}