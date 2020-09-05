package com.sjl.bookmark.ui.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sinpo.xnfc.NFCardActivity;
import com.sjl.bookmark.BuildConfig;
import com.sjl.bookmark.R;
import com.sjl.bookmark.app.AppConstant;
import com.sjl.bookmark.kotlin.language.LanguageManager;
import com.sjl.bookmark.util.PermissionRequestUtils;
import com.sjl.core.mvp.BaseActivity;
import com.sjl.core.util.PreferencesHelper;
import com.sjl.core.util.ShortcutUtils;
import com.sjl.core.util.ToastUtils;
import com.sjl.core.util.ViewUtils;
import com.sjl.core.util.log.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.os.Build.VERSION_CODES.M;

/**
 * 对BaseActivity不指定泛型当做普通类使用
 *
 * @author Kelly
 * @version 1.0.0
 * @filename SplashActivity.java
 * @time 2018/3/5 9:31
 * @copyright(C) 2018 song
 */
public class SplashActivity extends BaseActivity {

    /**
     * 要申请的权限,
     * Manifest.permission.REQUEST_INSTALL_PACKAGES
     * 存储权限，相机权限，8.0安装权限
     */
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE
            , Manifest.permission.CAMERA
            , Manifest.permission.ACCESS_FINE_LOCATION};
    /**
     * 权限请求码
     */
    private static final int PERMISSION_REQUEST_CODE = 1000;
    /**
     * 系统设置请求码
     */
    private static final int SETTING_REQUEST_CODE = 2000;
    /**
     * 安装应用未知来源请求码
     */
    private static final int GET_UNKNOWN_APP_SOURCES_CODE = 3000;
    private TextView copyright;
    private ImageView ivLogo;

    @Override
    protected void changeStatusBarColor() {
        //启动页覆写不要改变状态栏颜色
    }

    @Override
    protected int getLayoutId() {
        //        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
           getWindow().getDecorView().setBackground(null);
        }
        setStatusBar(0xffAFAFAF);
        LanguageManager.INSTANCE.initAppLanguage(this);//初始化语言
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {
        copyright = findViewById(R.id.tv_copyright);
        ivLogo = findViewById(R.id.iv_icon);

    }



    /**
     * 打开主页activity
     */
    private void openMainActivity() {
        if (BuildConfig.appType == 0) {//默认应用
            openGoogleBookmark();
        } else if (BuildConfig.appType == 1) {
            openAPPReader();
        } else {
            ToastUtils.showShort(this, "找不到合适的应用类型:" + BuildConfig.appType);
            finish();
        }

    }

    /**
     * 打开Google书签应用
     */
    private void openGoogleBookmark() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               /* RelativeLayout rl = findViewById(R.id.rl_content);
                File file = new File(AppConstant.ROOT_PATH+"splash.png");
                ViewUtils.saveBitmap(rl,rl.getWidth(),rl.getHeight(),file.getAbsolutePath());*/

//                ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//                int memorySize = activityManager.getMemoryClass();
//                LogUtils.i("分配给应用的内存上限：" + memorySize);
                PreferencesHelper preferencesHelper = PreferencesHelper.getInstance(SplashActivity.this);
                Intent intent;
                boolean isOpen = (boolean) preferencesHelper.get(AppConstant.SETTING.OPEN_GESTURE, false);
                if (isOpen) {
                    intent = new Intent(SplashActivity.this, CheckLockActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                }
                startActivity(intent);
                SplashActivity.this.overridePendingTransition(R.anim.splash_fade_in, R.anim.splash_fade_out);
                SplashActivity.this.finish();
            }
        }, 100);
    }

    /**
     * 打开小说阅读应用
     */
    private void openAPPReader() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, BookShelfActivity.class));
                SplashActivity.this.overridePendingTransition(R.anim.splash_fade_in, R.anim.splash_fade_out);
                SplashActivity.this.finish();
            }
        }, 200);
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {
        if (BuildConfig.appType == 0) {//默认应用
            //创建快捷方式
            ShortcutUtils.addShortcut(SplashActivity.this, R.string.app_name, R.mipmap.ic_shortcut);
            ShortcutUtils.addDyShortcut(SplashActivity.this, NFCardActivity.class, "nfc_id", "余额查询", R.mipmap.icon_nfc);
            ivLogo.setImageResource(R.mipmap.ic_launcher);
        } else if (BuildConfig.appType == 1) {
            ivLogo.setImageResource(R.mipmap.ic_book);
        } else {
            ToastUtils.showShort(this, "找不到合适的应用类型:" + BuildConfig.appType);
            finish();
            return;
        }
        Calendar date = Calendar.getInstance();
        String year = String.valueOf(date.get(Calendar.YEAR));
        copyright.setText(getResources().getString(R.string.str_copyright, year));
        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= M) {
            ArrayList<String> toApplyList = new ArrayList<String>();
            for (String perm : permissions) {
                //检查该权限是否已经获取
                if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                    toApplyList.add(perm);//把没有授权的权限放在数组里面
                }
            }
            if (!toApplyList.isEmpty()) {
                String[] tmpList = new String[toApplyList.size()];
                permissions = toApplyList.toArray(tmpList);
                showDialogTipUserRequestPermission();//如果没有授予该权限，就去提示用户请求
            } else {//已经有权限
                openMainActivity();
            }
        } else {
            openMainActivity();
        }

    }


    /**
     * 提示用户该请求权限的弹出框
     */
    private void showDialogTipUserRequestPermission() {
        startRequestPermission();
    }

    /**
     * 开始提交请求权限
     */
    private void startRequestPermission() {
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
    }

    /**
     * 提示用户去应用设置界面手动开启权限
     *
     * @param denied 拒绝的权限集合
     */
    private void showDialogTipUserGoToAppSetting(Set<String> denied) {
        final String str = PermissionRequestUtils.getPermissionName(denied);
        new AlertDialog.Builder(this)
                .setTitle("权限不足")
                .setMessage("请在-应用设置-权限管理中，允许该应用使用" + str + "权限")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 跳转到应用设置界面
                        goToAppSetting();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).show();

//
    }

    /**
     * 跳转到设置-允许安装未知来源-页面
     */
    @Deprecated
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startInstallPermissionSettingActivity() {
        //注意这个是8.0新API
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(intent, GET_UNKNOWN_APP_SOURCES_CODE);
    }

    /**
     * 跳转到当前应用的设置界面
     */
    private void goToAppSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, SETTING_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= M) {
                Set<String> denied = checkPermissionState(grantResults, permissions);
                if (denied.size() == 0) {
                    openMainActivity();
                } else {
                    //只要权限没有全部授权
                    showDialogTipUserGoToAppSetting(denied);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTING_REQUEST_CODE) {
            finish();
        } else if (requestCode == GET_UNKNOWN_APP_SOURCES_CODE) {
            LogUtils.i("允许安装未知来源应用");
        }
    }

    private Set<String> checkPermissionState(int[] grantResults, String[] permissions) {
        Set<String> denied = new HashSet<String>();
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                // 如果需要（即返回true），则可以弹出对话框提示用户申请权限原因，用户确认后申请权限requestPermissions()，如果不需要（即返回false），则直接申请权限requestPermissions()。
                boolean b = shouldShowRequestPermissionRationale(permissions[i]);
                if (!b) {
                    LogUtils.i("拒绝的权限名称：" + permissions[i]);
                    denied.add(permissions[i]);
                } else {
                    LogUtils.i("通过的权限名称：" + permissions[i]);
                }
            }
        }
        return denied;
    }
}
