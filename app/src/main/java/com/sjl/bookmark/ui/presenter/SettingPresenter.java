package com.sjl.bookmark.ui.presenter;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.text.TextUtils;
import android.widget.Toast;

import com.sjl.bookmark.R;
import com.sjl.bookmark.api.MyBookmarkService;
import com.sjl.bookmark.app.AppConstant;
import com.sjl.bookmark.app.MyApplication;
import com.sjl.bookmark.dao.impl.DaoFactory;
import com.sjl.bookmark.dao.util.BookmarkParse;
import com.sjl.bookmark.entity.dto.ResponseDto;
import com.sjl.bookmark.kotlin.darkmode.DarkModeUtils;
import com.sjl.bookmark.kotlin.language.I18nUtils;
import com.sjl.bookmark.kotlin.language.LanguageManager;
import com.sjl.bookmark.service.DownloadIntentService;
import com.sjl.bookmark.ui.activity.AboutActivity;
import com.sjl.bookmark.ui.activity.BackupAndSyncActivity;
import com.sjl.bookmark.ui.activity.BrowserActivity;
import com.sjl.bookmark.ui.activity.CreateLockActivity;
import com.sjl.bookmark.ui.activity.MainActivity;
import com.sjl.bookmark.ui.activity.SettingActivity;
import com.sjl.bookmark.ui.contract.SettingContract;
import com.sjl.core.entity.EventBusDto;
import com.sjl.core.entity.dto.UpdateInfoDto;
import com.sjl.core.mvp.BaseActivity;
import com.sjl.core.net.RetrofitHelper;
import com.sjl.core.net.RxBus;
import com.sjl.core.net.RxLifecycleUtils;
import com.sjl.core.net.RxSchedulers;
import com.sjl.core.net.filedownload.DownloadProgressHandler;
import com.sjl.core.net.filedownload.FileDownloader;
import com.sjl.core.util.AppUtils;
import com.sjl.core.util.PreferencesHelper;
import com.sjl.core.util.log.LogUtils;
import com.sjl.core.util.log.LogWriter;
import com.sjl.core.widget.materialpreference.SwitchPreference;
import com.sjl.core.widget.update.UpdateDialog;
import com.sjl.core.widget.update.UpdateDialogListener;

import java.io.File;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import cn.sharesdk.onekeyshare.ShareSDKUtils;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename SettingPresenter.java
 * @time 2018/3/6 15:12
 * @copyright(C) 2018 song
 */
public class SettingPresenter extends SettingContract.Presenter {
    private boolean isOpenGesture;
    private boolean isShowPassword;

    @Override
    public void init() {
        PreferencesHelper preferencesHelper = PreferencesHelper.getInstance(mContext);
        isOpenGesture = (Boolean) preferencesHelper.get(AppConstant.SETTING.OPEN_GESTURE, false);
        isShowPassword = (Boolean) preferencesHelper.get(AppConstant.SETTING.OPEN_PASS_WORD_SHOW, false);
        LogUtils.i("isOpenGesture=" + isOpenGesture + ",isShowPassword=" + isShowPassword);
        if (mView instanceof PreferenceFragment) {
            PreferenceFragment preferenceFragment = (PreferenceFragment) mView;
            SwitchPreference openGesture = (SwitchPreference) preferenceFragment.findPreference("打开手势密码");
            SwitchPreference openShow = (SwitchPreference) preferenceFragment.findPreference("账号的密码可见");

            if (openGesture != null) {
                openGesture.setChecked(isOpenGesture);
            }
            if (openShow != null) {
                openShow.setChecked(isShowPassword);
            }

        }
    }

    @Override
    public void setClickPreferenceKey(Preference preference, String key) {
        PreferencesHelper preferencesHelper = PreferencesHelper.getInstance(mContext);
        if (TextUtils.equals(key, "深色模式")){
            changeDarkMode();
        } else if (TextUtils.equals(key, "打开手势密码")) {
            isOpenGesture = !isOpenGesture;
            preferencesHelper.put(AppConstant.SETTING.OPEN_GESTURE, isOpenGesture);
            if (isOpenGesture) {
                Boolean isSuccess = (Boolean) preferencesHelper.get(AppConstant.SETTING.CREATE_LOCK_SUCCESS, false);
                if (!isSuccess) {//没有创建手势
                    Intent intent = new Intent(mContext, CreateLockActivity.class);
                    intent.putExtra("CREATE_MODE", AppConstant.SETTING.CREATE_GESTURE);
                    mView.readyGo(CreateLockActivity.class, intent);
                }
            }
        } else if (TextUtils.equals(key, "修改手势密码")) {
            Boolean isSuccess = (Boolean) preferencesHelper.get(AppConstant.SETTING.CREATE_LOCK_SUCCESS, false);
            if (isSuccess) {//创建手势
                Intent intent = new Intent(mContext, CreateLockActivity.class);
                intent.putExtra("CREATE_MODE", AppConstant.SETTING.UPDATE_GESTURE);
                mView.readyGo(CreateLockActivity.class, intent);
            } else {
                Toast.makeText(mContext, "请先打开手势密码", Toast.LENGTH_SHORT).show();
            }

        } else if (TextUtils.equals(key, "账号的密码可见")) {
            isShowPassword = !isShowPassword;
            preferencesHelper.put(AppConstant.SETTING.OPEN_PASS_WORD_SHOW, isShowPassword);
        } else if (TextUtils.equals(key, "更换语言")) {
            changeLanguage();
        } else if (TextUtils.equals(key, "更换主题")) {
            mView.openChangeThemeActivity();
        } else if (TextUtils.equals(key, "更新书签")) {
            updateBookmarks();
        } else if (TextUtils.equals(key, "备份与同步收藏")) {
            Intent intent = new Intent(mContext, BackupAndSyncActivity.class);
            mContext.startActivity(intent);
        } else if (TextUtils.equals(key, "清除浏览记录")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("提示");
            builder.setMessage("确定清除所有文章浏览记录?")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            DaoFactory.getBrowseTrackDao().deleteAllBrowseTrack();
                            EventBusDto eventBusDto = new EventBusDto(0);
                            RxBus.getInstance().post(AppConstant.RxBusFlag.FLAG_1, eventBusDto);
                            Toast.makeText(mContext, "清除成功", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (TextUtils.equals(key, "版权信息")) {
            openCopyrightInfo();
        } else if (TextUtils.equals(key, "检查更新")) {
            checkAppUpdate();
        } else if (TextUtils.equals(key, "分享应用")) {
            ShareSDKUtils.getInstance(mContext).useDefaultGUI("分享", "分享应用", "https://csdnimg.cn/pubfooter/images/csdn_cs_qr.png", "https://blog.csdn.net/augfun/article/details/72618592", null);
        } else if (TextUtils.equals(key, "关于")) {
            Intent intent = new Intent(mContext, AboutActivity.class);
            mContext.startActivity(intent);
        }
    }

    /**
     * 改变深色模式
     */
    private void changeDarkMode() {
        final String items[] = {I18nUtils.getString(R.string.mode_auto),I18nUtils.getString(R.string.mode_normal), I18nUtils.getString(R.string.mode_dark)};
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final int type =  DarkModeUtils.INSTANCE.getDarkModeType();
        builder.setSingleChoiceItems(items, type,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LogUtils.i("当前选择模式：" + which);
                        if (which == type){
                            return;
                        }
                        dialog.dismiss();
                        if (mContext instanceof SettingActivity) {
                            DarkModeUtils.INSTANCE.setDarkMode(which);
                           AppUtils.restartApp(mContext);
                        }

                    }
                });
        builder.create().show();
    }


    /**
     * 更换语言
     */
    private void changeLanguage() {
        final String items[] = {"跟随系统","简体中文", "繁体中文(台灣)", "繁体中文(香港)", "English"};
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("请选择语言");
        final int type = LanguageManager.INSTANCE.getCurrentLanguageType(mContext);
        builder.setSingleChoiceItems(items, type,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LogUtils.i("当前选择语言：" + which);
                        if (which == type){
                            return;
                        }
                        dialog.dismiss();
                        if (mContext instanceof SettingActivity) {
                            SettingActivity settingActivity = (SettingActivity) mContext;
                            LanguageManager.INSTANCE.changeLanguage(settingActivity, which);
                            List<BaseActivity> baseActivities = BaseActivity.getActivities();
                            for (BaseActivity activity : baseActivities) {
                                boolean finishing = activity.isFinishing();
                                if (!finishing && activity instanceof MainActivity) {
                                    MainActivity mainActivity = (MainActivity) activity;
                                    mainActivity.executeChangeLanguage = true;
                                    break;
                                }
                            }
                            Intent intent = new Intent(settingActivity, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//清空栈顶所有activity
                            settingActivity.startActivity(intent);
                            settingActivity.overridePendingTransition(R.anim.splash_fade_in, R.anim.splash_fade_out);
                            settingActivity.finish();
                        }
                    }
                });
        builder.create().show();
    }


    /**
     * 打开版权信息
     */
    private void openCopyrightInfo() {
        BrowserActivity.startWithParams(mContext, I18nUtils.getString(R.string.setting_copyright_information),
                "file:///android_asset/www/copyright_info.html");
    }


    /**
     * 检查更新
     */
    private void checkAppUpdate() {

        if (!AppUtils.isConnected(mContext)) {
            Toast.makeText(mContext, "当前网络不可用，无法更新", Toast.LENGTH_SHORT).show();
            return;
        }
        if (AppUtils.isServiceRunning(mContext, DownloadIntentService.class.getName())) {
            Toast.makeText(mContext, "正在下载中", Toast.LENGTH_SHORT).show();
            return;
        }
        mView.showLoading(1, "正在检测新版本");
        RetrofitHelper instance = RetrofitHelper.getInstance();
        MyBookmarkService apiService = instance.getApiService(MyBookmarkService.class);

        apiService.checkUpdate(mContext.getPackageName(), MyApplication.getVersionCode()).compose(RxSchedulers.<ResponseDto<UpdateInfoDto>>applySchedulers())
                .as(RxLifecycleUtils.<ResponseDto<UpdateInfoDto>>bindLifecycle())

                .subscribe(new Consumer<ResponseDto<UpdateInfoDto>>() {
                    @Override
                    public void accept(ResponseDto<UpdateInfoDto> dataResponse) throws Exception {
                        LogWriter.e("检查更新响应："+dataResponse);
                        if (dataResponse.getCode() == 0) {
                            UpdateInfoDto updateInfoDto = dataResponse.getData();
                            if (updateInfoDto.isHasUpdate()) {//有新版本
                                mView.hideLoading(true, "");
                                showDialog(updateInfoDto);
                            } else {
                                mView.hideLoading(true, "已经更新到最新版本");
                            }
                        } else if (dataResponse.getCode() == -1) {
                            mView.hideLoading(true, "已经更新到最新版本");
                        } else {
                            mView.hideLoading(false, dataResponse.getMsg());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("检查更新异常", throwable);
                        mView.hideLoading(false, "检查更新失败");
                    }
                });


    }

    /**
     * 显示对话框提示用户有新版本，并且让用户选择是否更新版本
     *
     * @param updateInfoDto
     */
    private void showDialog(final UpdateInfoDto updateInfoDto) {
//        UpdateDialog updateDialog = UpdateDialog.newInstance(mContext, updateInfoDto,R.color.update_theme_color, R.mipmap.bg_update_top);
        UpdateDialog updateDialog = UpdateDialog.newInstance(mContext, updateInfoDto);
        updateDialog.setUpdateDialogListener(new UpdateDialogListener() {
            @Override
            public void onUpdate(UpdateDialog updateDialog) {
                //下载apk文件
                String fileName = "google_bookmark_" + "V" + updateInfoDto.getVersionName() + ".apk";
                goDownloadApk(fileName, updateInfoDto.getSize());
                updateDialog.dismiss();
            }

            @Override
            public void onCancel(UpdateDialog updateDialog) {
                updateDialog.dismiss();
            }
        }).show();

//        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//        builder.setTitle("提示");
//        builder.setMessage(updateInfoDto.getUpdateContent())
//                .setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        //下载apk文件
//                        String fileName = "google_bookmark_" + "V" + updateInfoDto.getVersionName() + ".apk";
//                        goDownloadApk(fileName, updateInfoDto.getSize());
//                    }
//                })
//                .setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.dismiss();
//                    }
//                });
//
//        AlertDialog dialog = builder.create();
//        //点击对话框外面,对话框不消失
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.show();
    }

    private void goDownloadApk(String fileName, long size) {
        Intent intent = new Intent(mContext, DownloadIntentService.class);
        Bundle bundle = new Bundle();
        bundle.putString("fileName", fileName);
        bundle.putLong("fileSize", size);
        intent.putExtras(bundle);
        mContext.startService(intent);
    }


    /**
     * 更新书签
     */
    private void updateBookmarks() {
        if (!AppUtils.isConnected(mContext)) {
            Toast.makeText(mContext, "当前网络不可用，无法更新", Toast.LENGTH_SHORT).show();
            return;
        }
        mView.showLoading(0, "正在更新...");
        RetrofitHelper instance = RetrofitHelper.getInstance();
        MyBookmarkService apiService = instance.getApiService(MyBookmarkService.class);
        FileDownloader.downloadFile(apiService.downloadBookmarkFile(), AppConstant.BOOKMARK_PATH, "bookmark.html", new DownloadProgressHandler() {


            @Override
            public void onProgress(int progress, long total, long speed) {
                mView.update(progress);
            }

            @Override
            public void onCompleted(File file) {
                LogUtils.i("下载书签成功");
                saveBookmarksToLocal(file);
                FileDownloader.clear();
            }

            @Override
            public void onError(Throwable e) {
                LogUtils.e("下载书签文件异常", e);
                mView.hideLoading(false, "下载书签文件异常");
                FileDownloader.clear();
            }
        });

    }

    /**
     * 保存书签到本地数据库
     *
     * @param file
     */
    private void saveBookmarksToLocal(File file) {
        Observable.just(file)
                .map(new Function<File, Boolean>() {

                    @Override
                    public Boolean apply(File file) throws Exception {
                        BookmarkParse bookmarkParse = new BookmarkParse();
                        return bookmarkParse.updateBookmark(mContext, file);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(RxLifecycleUtils.<Boolean>bindLifecycle())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        LogUtils.i("2.开始书签更新");
                    }

                    @Override
                    public void onNext(Boolean ret) {
                        String msg = "";
                        if (ret) {
                            msg = "更新书签成功";
                        } else {
                            msg = "更新书签失败";
                        }
                        mView.hideLoading(ret, msg);
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e("2.更新书签数据到数据库异常", e);
                        mView.hideLoading(false, "更新书签数据到数据库异常");
                    }

                    @Override
                    public void onComplete() {
                        LogUtils.i("2.完成书签更新");
                    }
                });
    }


    @Override
    public void resetGestureFlag() {
        isOpenGesture = false;
    }


}
