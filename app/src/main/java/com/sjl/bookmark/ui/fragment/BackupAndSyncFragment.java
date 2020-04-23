package com.sjl.bookmark.ui.fragment;


import android.app.ProgressDialog;
import android.preference.Preference;
import android.text.TextUtils;
import android.widget.Toast;

import com.sjl.bookmark.R;
import com.sjl.bookmark.ui.contract.BackupAndSyncContract;
import com.sjl.bookmark.ui.presenter.BackupAndSyncPresenter;
import com.sjl.core.mvp.BasePreferenceFragment;

/**
 * 同步与恢复
 */
public class BackupAndSyncFragment extends BasePreferenceFragment<BackupAndSyncPresenter> implements BackupAndSyncContract.View {


    private ProgressDialog mCircleDialog;



    @Override
    protected int getPreferencesResId() {
        return R.xml.backup_sync_preference_xml;
    }

    @Override
    protected void setClickPreferenceKey(Preference preference, String key) {
        mPresenter.setClickPreferenceKey(key);
    }


    @Override
    protected void initView() {
        //圆形对话框
        mCircleDialog = new ProgressDialog(getActivity());
        // 设置对话框参数
        mCircleDialog.setMessage("正在同步...");
        mCircleDialog.setCancelable(false);
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {
        mPresenter.init();
    }


    @Override
    public void showLoading(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            mCircleDialog.setMessage(msg);
        }
        mCircleDialog.show();
    }

    @Override
    public void hideLoading(String errorMsg) {
        mCircleDialog.cancel();
        if (TextUtils.isEmpty(errorMsg)) {
            return;
        }
        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
    }
}
