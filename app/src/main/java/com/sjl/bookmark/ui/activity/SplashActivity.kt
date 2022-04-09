package com.sjl.bookmark.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.*
import android.os.Build.VERSION_CODES
import android.provider.Settings
import android.text.TextUtils
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sinpo.xnfc.NFCardActivity
import com.sjl.bookmark.BuildConfig
import com.sjl.bookmark.R
import com.sjl.bookmark.app.AppConstant
import com.sjl.bookmark.kotlin.language.LanguageManager.initAppLanguage
import com.sjl.bookmark.ui.activity.MainActivity
import com.sjl.bookmark.util.PermissionRequestUtils
import com.sjl.core.manager.CachedThreadManager
import com.sjl.core.mvp.BaseActivity
import com.sjl.core.mvp.NoPresenter
import com.sjl.core.permission.PermissionsManager
import com.sjl.core.permission.PermissionsResultAction
import com.sjl.core.permission.SpecialPermission
import com.sjl.core.util.PreferencesHelper
import com.sjl.core.util.ShortcutUtils
import com.sjl.core.util.ToastUtils
import com.sjl.core.util.log.LogUtils
import com.tencent.smtt.sdk.CacheManager
import java.util.*

/**
 * 对BaseActivity不指定泛型当做普通类使用
 *
 * @author Kelly
 * @version 1.0.0
 * @filename SplashActivity.java
 * @time 2018/3/5 9:31
 * @copyright(C) 2018 song
 */
class SplashActivity : BaseActivity<NoPresenter>() {
    /**
     * 要申请的权限,
     * Manifest.permission.REQUEST_INSTALL_PACKAGES
     * 存储权限，相机权限，8.0安装权限
     */
    private var permissions = arrayOf<String?>(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    private val copyright: TextView? = null
    private val ivLogo: ImageView? = null
    override fun changeStatusBarColor() {
        //启动页覆写不要改变状态栏颜色
    }

    override fun getLayoutId(): Int {
        //        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        /*    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
           getWindow().getDecorView().setBackground(null);
        }
        setStatusBar(0xffAFAFAF);*/
        initAppLanguage(this) //初始化语言
        //        return R.layout.activity_splash;
        return 0 //不需要布局
    }

    override fun initView() {
//        copyright = findViewById(R.id.tv_copyright);
//        ivLogo = findViewById(R.id.iv_icon);
    }

    /**
     * 打开主页activity
     */
    private fun openMainActivity() {
        if (BuildConfig.appType == 0) { //默认应用
            openGoogleBookmark()
            /* new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //生成截图
                    RelativeLayout rl = findViewById(R.id.rl_content);
                    File file = new File(AppConstant.ROOT_PATH + "splash.png");
                    ViewUtils.saveBitmap(rl, rl.getWidth(), rl.getHeight(), file.getAbsolutePath());

                }
            },5000);*/
        } else if (BuildConfig.appType == 1) {
            openAPPReader()
        } else {
            ToastUtils.showShort(this, getString(R.string.app_match_hint) + BuildConfig.appType)
            finish()
        }
    }

    /**
     * 打开Google书签应用
     */
    private fun openGoogleBookmark() {
        Handler().postDelayed({ //                ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//                int memorySize = activityManager.getMemoryClass();
//                LogUtils.i("分配给应用的内存上限：" + memorySize);
            val preferencesHelper = PreferencesHelper.getInstance(this@SplashActivity)
            val intent: Intent
            val isOpen = preferencesHelper[AppConstant.SETTING.OPEN_GESTURE, false] as Boolean
            intent = if (isOpen) {
                Intent(this@SplashActivity, CheckLockActivity::class.java)
            } else {
                Intent(this@SplashActivity, MainActivity::class.java)
            }
            startActivity(intent)
            overridePendingTransition(R.anim.splash_fade_in, R.anim.splash_fade_out)
            finish()
        }, 100)
    }

    /**
     * 打开小说阅读应用
     */
    private fun openAPPReader() {
        Handler().postDelayed({
            startActivity(Intent(this@SplashActivity, BookShelfActivity::class.java))
            overridePendingTransition(R.anim.splash_fade_in, R.anim.splash_fade_out)
            finish()
        }, 200)
    }

    override fun initListener() {}
    override fun initData() {
        when (BuildConfig.appType) {
            0 -> { //默认应用
                //创建快捷方式
                ShortcutUtils.addShortcut(this@SplashActivity, R.string.app_name, R.mipmap.ic_shortcut)
                ShortcutUtils.addDyShortcut(
                    this@SplashActivity,
                    MyNfcActivity::class.java,
                    "nfc_id",
                    "余额查询",
                    R.mipmap.menu_card
                )
                ShortcutUtils.addDyShortcut(
                    this@SplashActivity,
                    ExpressActivity::class.java,
                    "express_id",
                    "我的快递",
                    R.mipmap.menu_express_query
                )
                // fix NfcActivity bug
                ShortcutUtils.updateDyShortcut(this@SplashActivity,
                    MyNfcActivity::class.java,
                    "nfc_id",
                    "余额查询",
                    R.mipmap.menu_card)

                //            ivLogo.setImageResource(R.mipmap.ic_launcher);
            }
            1 -> {
    //            ivLogo.setImageResource(R.mipmap.ic_book);
            }
            else -> {
                ToastUtils.showShort(this, "找不到合适的应用类型:" + BuildConfig.appType)
                finish()
                return
            }
        }
        /*        Calendar date = Calendar.getInstance();
        String year = String.valueOf(date.get(Calendar.YEAR));
        copyright.setText(getResources().getString(R.string.str_copyright, year));*/
        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= VERSION_CODES.M) {
            val toApplyList = ArrayList<String?>()
            for (perm in permissions) {
                //检查该权限是否已经获取
                if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(
                        this,
                        perm!!
                    )
                ) {
                    toApplyList.add(perm) //把没有授权的权限放在数组里面
                }
            }
            if (toApplyList.isNotEmpty()) {
                val tmpList = arrayOfNulls<String>(toApplyList.size)
                permissions = toApplyList.toArray(tmpList)
                showDialogTipUserRequestPermission() //如果没有授予该权限，就去提示用户请求
            } else { //已经有权限
                requestFilePermission()
            }
        } else {
            requestFilePermission()
        }
    }
    private fun requestFilePermission() {
        PermissionsManager.getInstance().requestSpecialPermission(SpecialPermission.MANAGE_ALL_FILES_ACCESS,this,object : PermissionsResultAction() {
            override fun onGranted() {
                LogUtils.d("MANAGE_EXTERNAL_STORAGE权限授权通过")
                openMainActivity()
            }

            override fun onDenied(permission: String) {
                LogUtils.d("MANAGE_EXTERNAL_STORAGE权限拒绝：$permission")
                finish()
            }
        })
    }

    /**
     * 提示用户该请求权限的弹出框
     */
    private fun showDialogTipUserRequestPermission() {
        startRequestPermission()
    }

    /**
     * 开始提交请求权限
     */
    private fun startRequestPermission() {
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE)
    }

    /**
     * 提示用户去应用设置界面手动开启权限
     *
     * @param denied 拒绝的权限集合
     */
    private fun showDialogTipUserGoToAppSetting(denied: Set<String>) {
        val str = PermissionRequestUtils.getPermissionName(denied)
        val string = getString(R.string.permission_set_hint, str)
        AlertDialog.Builder(this)
            .setTitle(R.string.nb_common_tip)
            .setMessage(string)
            .setPositiveButton(R.string.permission_open) { dialog, which -> // 跳转到应用设置界面
                goToAppSetting()
            }
            .setNegativeButton(R.string.cancel) { dialog, which -> finish() }.setCancelable(false)
            .show()

//
    }

    /**
     * 跳转到设置-允许安装未知来源-页面
     */
    @Deprecated("")
    @RequiresApi(api = VERSION_CODES.O)
    private fun startInstallPermissionSettingActivity() {
        //注意这个是8.0新API
        val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivityForResult(intent, GET_UNKNOWN_APP_SOURCES_CODE)
    }

    /**
     * 跳转到当前应用的设置界面
     */
    private fun goToAppSetting() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, SETTING_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= VERSION_CODES.M) {
                val denied = checkPermissionState(grantResults, permissions)
                if (denied.isEmpty()) {
                    openMainActivity()
                } else {
                    //只要权限没有全部授权
                    showDialogTipUserGoToAppSetting(denied)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SETTING_REQUEST_CODE) {
            finish()
        } else if (requestCode == GET_UNKNOWN_APP_SOURCES_CODE) {
            LogUtils.i("允许安装未知来源应用")
        }
    }

    @RequiresApi(api = VERSION_CODES.M)
    private fun checkPermissionState(
        grantResults: IntArray,
        permissions: Array<String>
    ): Set<String> {
        val denied: MutableSet<String> = HashSet()
        for (i in grantResults.indices) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                // 如果需要（即返回true），则可以弹出对话框提示用户申请权限原因，用户确认后申请权限requestPermissions()，如果不需要（即返回false），则直接申请权限requestPermissions()。
                val b = shouldShowRequestPermissionRationale(permissions[i])
                if (!b) {
                    LogUtils.i("拒绝的权限名称：" + permissions[i])
                    denied.add(permissions[i])
                } else {
                    LogUtils.i("通过的权限名称：" + permissions[i])
                }
            }
        }
        return denied
    }

    companion object {
        /**
         * 权限请求码
         */
        private const val PERMISSION_REQUEST_CODE = 1000

        /**
         * 系统设置请求码
         */
        private const val SETTING_REQUEST_CODE = 2000

        /**
         * 安装应用未知来源请求码
         */
        private const val GET_UNKNOWN_APP_SOURCES_CODE = 3000
    }
}