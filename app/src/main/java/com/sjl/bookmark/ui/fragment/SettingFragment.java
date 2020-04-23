package com.sjl.bookmark.ui.fragment;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.Preference;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.widget.Toast;

import com.sjl.bookmark.R;
import com.sjl.bookmark.app.AppConstant;
import com.sjl.bookmark.app.MyApplication;
import com.sjl.bookmark.ui.activity.ChangeSkinActivity;
import com.sjl.bookmark.ui.contract.SettingContract;
import com.sjl.bookmark.ui.presenter.SettingPresenter;
import com.sjl.core.mvp.BasePreferenceFragment;
import com.sjl.core.util.log.LogUtils;
import com.sjl.core.util.PreferencesHelper;
import com.sjl.core.widget.materialpreference.SwitchPreference;

/**
 * 设置Fragment
 */
public class SettingFragment extends BasePreferenceFragment<SettingPresenter> implements SettingContract.View {


    private ProgressDialog mProgressDialog;
    private ProgressDialog mCircleDialog;
    private int loadingType = 0;




    @Override
    protected void setClickPreferenceKey(Preference preference, String key) {
        mPresenter.setClickPreferenceKey(preference,key);
    }

    @Override
    protected int getPreferencesResId() {
        return R.xml.setting_preference_xml;
    }

    @Override
    protected void initView() {
        //进度条对话框
        mProgressDialog = new ProgressDialog(getActivity());
        // 设置对话框参数
        mProgressDialog.setMessage("正在更新...");
        mProgressDialog.setCancelable(false);
        // 设置进度条参数
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setMax(100);
        mProgressDialog.setIndeterminate(false); // 填false表示是明确显示进度的 填true表示不是明确显示进度的
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                LogUtils.i("mProgressDialog close.");
            }
        });

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
        Preference preference = getPreferenceScreen().findPreference("关于");
        preference.setSummary("V"+ MyApplication.getAppVersion());
    }


    @Override
    public void openChangeThemeActivity() {
        openActivity(ChangeSkinActivity.class);
    }

    @Override
    public void readyGo(Class clazz, Intent intent) {
        startActivityForResult(intent, 0);
    }

    @Override
    public void showLoading(int type, String msg) {
        this.loadingType = type;
        if (type == 0){
            if (!TextUtils.isEmpty(msg)){
                mProgressDialog.setMessage(msg);
            }
            mProgressDialog.setProgress(0);//每次显示之前重置下载进度
            mProgressDialog.show();
        }else{
            if (!TextUtils.isEmpty(msg)){
                mCircleDialog.setMessage(msg);
            }
            mCircleDialog.show();
        }
    }


    @Override
    public void hideLoading(boolean ret,String errorMsg) {
        if (loadingType == 0){
            mProgressDialog.cancel();
        }else{
            mCircleDialog.cancel();
        }
        if (ret){
            if (TextUtils.isEmpty(errorMsg)){
                return;
            }
            Toast.makeText(getActivity(),errorMsg, Toast.LENGTH_SHORT).show();
        }else{
            if (TextUtils.isEmpty(errorMsg)){
                return;
            }
            Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void update(int percent) {
        LogUtils.i("下载进度："+percent);
        mProgressDialog.setProgress(percent);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.i("resultCode="+resultCode);
        if (requestCode == 0){
            if (resultCode == 1) {
                showSnackBar("手势密码创建成功");
            }else if(resultCode == 2){
                showSnackBar("手势密码修改成功");
            }else if(resultCode == 10){//放弃设置手势密码
                PreferencesHelper preferencesHelper = PreferencesHelper.getInstance(getActivity());
                preferencesHelper.put(AppConstant.SETTING.OPEN_GESTURE, false);
                SwitchPreference openGesture = (SwitchPreference) findPreference("打开手势密码");
                openGesture.setChecked(false);
                mPresenter.resetGestureFlag();
            }else if(resultCode == 20){
                showSnackBar("放弃手势密码修改");
            }

        }

    }


    public void showSnackBar(String msg) {
        Snackbar.make(getView(), msg, Snackbar.LENGTH_SHORT).show();
    }
}
