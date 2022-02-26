package com.sjl.bookmark.ui.presenter

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.sjl.bookmark.R
import com.sjl.bookmark.api.MyBookmarkService
import com.sjl.bookmark.app.AppConstant
import com.sjl.bookmark.app.MyApplication
import com.sjl.bookmark.dao.impl.DaoFactory
import com.sjl.bookmark.dao.util.BookmarkParse
import com.sjl.bookmark.entity.dto.ResponseDto
import com.sjl.bookmark.kotlin.darkmode.DarkModeUtils.getDarkModeType
import com.sjl.bookmark.kotlin.darkmode.DarkModeUtils.setDarkMode
import com.sjl.bookmark.kotlin.language.I18nUtils
import com.sjl.bookmark.kotlin.language.LanguageManager.changeLanguage
import com.sjl.bookmark.kotlin.language.LanguageManager.getCurrentLanguageType
import com.sjl.bookmark.service.DownloadIntentService
import com.sjl.bookmark.ui.activity.*
import com.sjl.bookmark.ui.contract.SettingContract
import com.sjl.core.entity.EventBusDto
import com.sjl.core.entity.dto.UpdateInfoDto
import com.sjl.core.mvp.BaseActivity
import com.sjl.core.net.RetrofitHelper
import com.sjl.core.net.RxBus
import com.sjl.core.net.RxLifecycleUtils
import com.sjl.core.net.RxSchedulers
import com.sjl.core.net.filedownload.DownloadProgressHandler
import com.sjl.core.net.filedownload.FileDownloader
import com.sjl.core.util.AppUtils
import com.sjl.core.util.PreferencesHelper
import com.sjl.core.util.log.LogUtils
import com.sjl.core.util.log.LogWriter
import com.sjl.core.widget.materialpreference.SwitchPreference
import com.sjl.core.widget.update.UpdateDialog
import com.sjl.core.widget.update.UpdateDialogListener
import com.uber.autodispose.ObservableSubscribeProxy
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import java.io.File

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename SettingPresenter.java
 * @time 2018/3/6 15:12
 * @copyright(C) 2018 song
 */
class SettingPresenter : SettingContract.Presenter() {
    private var isOpenGesture = false
    private var isShowPassword = false
    override fun init() {
        val preferencesHelper = PreferencesHelper.getInstance(mContext)
        isOpenGesture = preferencesHelper[AppConstant.SETTING.OPEN_GESTURE, false] as Boolean
        isShowPassword =
            preferencesHelper[AppConstant.SETTING.OPEN_PASS_WORD_SHOW, false] as Boolean
        LogUtils.i("isOpenGesture=$isOpenGesture,isShowPassword=$isShowPassword")
        if (mView is PreferenceFragment) {
            val preferenceFragment = mView as PreferenceFragment
            val openGesture = preferenceFragment.findPreference("打开手势密码") as SwitchPreference
            val openShow = preferenceFragment.findPreference("账号的密码可见") as SwitchPreference
            if (openGesture != null) {
                openGesture.isChecked = isOpenGesture
            }
            if (openShow != null) {
                openShow.isChecked = isShowPassword
            }
        }
    }

    override fun setClickPreferenceKey(preference: Preference, key: String) {
        val preferencesHelper = PreferencesHelper.getInstance(mContext)
        if (TextUtils.equals(key, "深色模式")) {
            changeDarkMode()
        } else if (TextUtils.equals(key, "打开手势密码")) {
            isOpenGesture = !isOpenGesture
            preferencesHelper.put(AppConstant.SETTING.OPEN_GESTURE, isOpenGesture)
            if (isOpenGesture) {
                val isSuccess =
                    preferencesHelper[AppConstant.SETTING.CREATE_LOCK_SUCCESS, false] as Boolean
                if (!isSuccess) { //没有创建手势
                    val intent = Intent(mContext, CreateLockActivity::class.java)
                    intent.putExtra("CREATE_MODE", AppConstant.SETTING.CREATE_GESTURE)
                    mView.readyGo(CreateLockActivity::class.java, intent)
                }
            }
        } else if (TextUtils.equals(key, "修改手势密码")) {
            val isSuccess =
                preferencesHelper[AppConstant.SETTING.CREATE_LOCK_SUCCESS, false] as Boolean
            if (isSuccess) { //创建手势
                val intent = Intent(mContext, CreateLockActivity::class.java)
                intent.putExtra("CREATE_MODE", AppConstant.SETTING.UPDATE_GESTURE)
                mView.readyGo(CreateLockActivity::class.java, intent)
            } else {
                Toast.makeText(mContext, "请先打开手势密码", Toast.LENGTH_SHORT).show()
            }
        } else if (TextUtils.equals(key, "账号的密码可见")) {
            isShowPassword = !isShowPassword
            preferencesHelper.put(AppConstant.SETTING.OPEN_PASS_WORD_SHOW, isShowPassword)
        } else if (TextUtils.equals(key, "更换语言")) {
            changeLanguage()
        } else if (TextUtils.equals(key, "更换主题")) {
            mView.openChangeThemeActivity()
        } else if (TextUtils.equals(key, "更新书签")) {
            updateBookmarks()
        } else if (TextUtils.equals(key, "备份与同步收藏")) {
            val intent = Intent(mContext, BackupAndSyncActivity::class.java)
            mContext.startActivity(intent)
        } else if (TextUtils.equals(key, "清除浏览记录")) {
            val builder = AlertDialog.Builder(mContext)
            builder.setTitle(R.string.nb_common_tip)
            builder.setMessage(R.string.clear_browsing_hint)
                .setPositiveButton(R.string.sure) { dialog, id ->
                    DaoFactory.getBrowseTrackDao().deleteAllBrowseTrack()
                    val eventBusDto: EventBusDto<*> = EventBusDto<Any?>(0)
                    RxBus.getInstance().post(AppConstant.RxBusFlag.FLAG_1, eventBusDto)
                    Toast.makeText(mContext, R.string.clear_success, Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton(R.string.cancel) { dialog, id -> dialog.dismiss() }
            val dialog = builder.create()
            dialog.show()
        } else if (TextUtils.equals(key, "版权信息")) {
            openCopyrightInfo()
        } else if (TextUtils.equals(key, "检查更新")) {
            checkAppUpdate()
        } else if (TextUtils.equals(key, "分享应用")) {
            //新规适配
            //停用share sdk
            /* MobSDK.submitPolicyGrantResult(true, null);
            ShareSDKUtils.getInstance(mContext.getApplicationContext()).useDefaultGUI(mContext.getString(R.string.menu_share), mContext.getString(R.string.share_app), "https://avatars.githubusercontent.com/u/17974939?s=400&u=1399ed8c9ccfdeaff025a1ea71b22fffbf88a80e&v=4", "https://github.com/kellysong/my-bookmark-master/blob/master/pocket_bookmark.apk", null);
            */
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "https://github.com/kellysong/my-bookmark-master/blob/master/pocket_bookmark.apk"
            )
            intent.type = "text/plain"
            mContext.startActivity(
                Intent.createChooser(
                    intent,
                    mContext.getString(R.string.share_title)
                )
            )
        } else if (TextUtils.equals(key, "关于")) {
            val intent = Intent(mContext, AboutActivity::class.java)
            mContext.startActivity(intent)
        }
    }

    /**
     * 改变深色模式
     */
    private fun changeDarkMode() {
        val items = arrayOf(
            I18nUtils.getString(R.string.mode_auto),
            I18nUtils.getString(R.string.mode_normal),
            I18nUtils.getString(R.string.mode_dark)
        )
        val builder = AlertDialog.Builder(mContext)
        val type = getDarkModeType()
        builder.setSingleChoiceItems(items, type,
            DialogInterface.OnClickListener { dialog, which ->
                LogUtils.i("当前选择模式：$which")
                if (which == type) {
                    return@OnClickListener
                }
                dialog.dismiss()
                if (mContext is SettingActivity) {
                    setDarkMode(which)
                    AppUtils.restartApp(mContext)
                }
            })
        builder.create().show()
    }

    /**
     * 更换语言
     */
    private fun changeLanguage() {
        val items = arrayOf(
            mContext.getString(R.string.language_auto),
            "简体中文",
            "繁体中文(台灣)",
            "繁体中文(香港)",
            "English"
        )
        val builder = AlertDialog.Builder(mContext)
        builder.setTitle(R.string.language_select_hint)
        val type = getCurrentLanguageType(mContext)
        builder.setSingleChoiceItems(items, type,
            DialogInterface.OnClickListener { dialog, which ->
                LogUtils.i("当前选择语言：$which")
                if (which == type) {
                    return@OnClickListener
                }
                dialog.dismiss()
                if (mContext is SettingActivity) {
                    val settingActivity = mContext as SettingActivity
                    changeLanguage(settingActivity, which)
                    val baseActivities = BaseActivity.getActivities()
                    for (activity in baseActivities) {
                        val finishing = activity.isFinishing
                        if (!finishing && activity is MainActivity) {
                            activity.executeChangeLanguage = true
                            break
                        }
                    }
                    val intent = Intent(settingActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) //清空栈顶所有activity
                    settingActivity.startActivity(intent)
                    settingActivity.overridePendingTransition(
                        R.anim.splash_fade_in,
                        R.anim.splash_fade_out
                    )
                    settingActivity.finish()
                }
            })
        builder.create().show()
    }

    /**
     * 打开版权信息
     */
    private fun openCopyrightInfo() {
        BrowserActivity.startWithParams(
            mContext, I18nUtils.getString(R.string.setting_copyright_information),
            "file:///android_asset/www/copyright_info.html"
        )
    }

    /**
     * 检查更新
     */
    private fun checkAppUpdate() {
        if (!AppUtils.isConnected(mContext)) {
            Toast.makeText(mContext, R.string.network_unavailable, Toast.LENGTH_SHORT).show()
            return
        }
        if (AppUtils.isServiceRunning(mContext, DownloadIntentService::class.java.name)) {
            Toast.makeText(mContext, R.string.downloading, Toast.LENGTH_SHORT).show()
            return
        }
        mView.showLoading(1, mContext.getString(R.string.checking_update))
        val instance = RetrofitHelper.getInstance()
        val apiService = instance.getApiService(
            MyBookmarkService::class.java
        )
        apiService.checkUpdate(mContext.packageName, MyApplication.getVersionCode())
            .compose(RxSchedulers.applySchedulers())
            .`as`<ObservableSubscribeProxy<ResponseDto<UpdateInfoDto>>>(RxLifecycleUtils.bindLifecycle<ResponseDto<UpdateInfoDto>>())
            .subscribe({ dataResponse ->
                LogWriter.e("检查更新响应：$dataResponse")
                if (dataResponse.code == 0) {
                    val updateInfoDto = dataResponse.data
                    if (updateInfoDto.isHasUpdate) { //有新版本
                        mView.hideLoading(true, "")
                        showDialog(updateInfoDto)
                    } else {
                        mView.hideLoading(true, mContext.getString(R.string.version_update_hint))
                    }
                } else if (dataResponse.code == -1) {
                    mView.hideLoading(true, mContext.getString(R.string.version_update_hint))
                } else {
                    mView.hideLoading(false, dataResponse.msg)
                }
            }) { throwable ->
                LogUtils.e("检查更新异常", throwable)
                mView.hideLoading(false, mContext.getString(R.string.version_update_failed))
            }
    }

    /**
     * 显示对话框提示用户有新版本，并且让用户选择是否更新版本
     *
     * @param updateInfoDto
     */
    private fun showDialog(updateInfoDto: UpdateInfoDto) {
//        UpdateDialog updateDialog = UpdateDialog.newInstance(mContext, updateInfoDto,R.color.update_theme_color, R.mipmap.bg_update_top);
        val updateDialog = UpdateDialog.newInstance(mContext, updateInfoDto)
        updateDialog.setUpdateDialogListener(object : UpdateDialogListener {
            override fun onUpdate(updateDialog: UpdateDialog) {
                //下载apk文件
                val fileName = "google_bookmark_" + "V" + updateInfoDto.versionName + ".apk"
                goDownloadApk(fileName, updateInfoDto.size.toLong())
                updateDialog.dismiss()
            }

            override fun onCancel(updateDialog: UpdateDialog) {
                updateDialog.dismiss()
            }
        }).show()

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

    private fun goDownloadApk(fileName: String, size: Long) {
        val intent = Intent(mContext, DownloadIntentService::class.java)
        val bundle = Bundle()
        bundle.putString("fileName", fileName)
        bundle.putLong("fileSize", size)
        intent.putExtras(bundle)
        mContext.startService(intent)
    }

    /**
     * 更新书签
     */
    private fun updateBookmarks() {
        if (!AppUtils.isConnected(mContext)) {
            Toast.makeText(mContext, R.string.network_unavailable, Toast.LENGTH_SHORT).show()
            return
        }
        mView.showLoading(0, mContext.getString(R.string.downloading))
        val instance = RetrofitHelper.getInstance()
        val apiService = instance.getApiService(
            MyBookmarkService::class.java
        )
        FileDownloader.downloadFile(
            apiService.downloadBookmarkFile(),
            AppConstant.BOOKMARK_PATH,
            "bookmark.html",
            object : DownloadProgressHandler() {
                override fun onProgress(progress: Int, total: Long, speed: Long) {
                    mView.update(progress)
                }

                override fun onCompleted(file: File) {
                    LogUtils.i("下载书签成功")
                    saveBookmarksToLocal(file)
                    FileDownloader.clear()
                }

                override fun onError(e: Throwable) {
                    LogUtils.e("下载书签文件异常", e)
                    mView.hideLoading(
                        false,
                        mContext.getString(R.string.bookmark_file_download_failed)
                    )
                    FileDownloader.clear()
                }
            })
    }

    /**
     * 保存书签到本地数据库
     *
     * @param file
     */
    private fun saveBookmarksToLocal(file: File) {
        Observable.just(file)
            .map(object : Function<File, Boolean> {
                @Throws(Exception::class)
                override fun apply(file: File): Boolean {
                    val bookmarkParse = BookmarkParse()
                    return bookmarkParse.updateBookmark(mContext, file)
                }
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .`as`(RxLifecycleUtils.bindLifecycle())
            .subscribe(object : Observer<Boolean> {
                override fun onSubscribe(d: Disposable) {
                    LogUtils.i("2.开始书签更新")
                }

                override fun onNext(ret: Boolean) {
                    var msg: String? = ""
                    msg = if (ret) {
                        //Update bookmark successfully
                        mContext.getString(R.string.bookmark_update_hint)
                    } else {
                        mContext.getString(R.string.bookmark_update_hint2)
                    }
                    mView.hideLoading(ret, msg)
                }

                override fun onError(e: Throwable) {
                    LogUtils.e("2.更新书签数据到数据库异常", e)
                    mView.hideLoading(false, mContext.getString(R.string.bookmark_update_hint3))
                }

                override fun onComplete() {
                    LogUtils.i("2.完成书签更新")
                }
            })
    }

    override fun resetGestureFlag() {
        isOpenGesture = false
    }
}