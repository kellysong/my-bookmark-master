package com.sjl.bookmark.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.orhanobut.logger.Logger;
import com.renny.zxing.Activity.CaptureActivity;
import com.sjl.bookmark.R;
import com.sjl.bookmark.app.AppConstant;
import com.sjl.bookmark.dao.impl.CollectDaoImpl;
import com.sjl.bookmark.dao.util.BookmarkParse;
import com.sjl.bookmark.dao.util.BrowseMapper;
import com.sjl.bookmark.entity.UserInfo;
import com.sjl.bookmark.entity.table.Collection;
import com.sjl.bookmark.kotlin.language.I18nUtils;
import com.sjl.bookmark.ui.adapter.MainViewPagerAdapter;
import com.sjl.bookmark.ui.fragment.CategoryFragment;
import com.sjl.bookmark.ui.fragment.HomeFragment;
import com.sjl.bookmark.ui.fragment.ToolFragment;
import com.sjl.bookmark.util.NavigationViewHelper;
import com.sjl.bookmark.util.NotificationUtils;
import com.sjl.bookmark.widget.WaveView;
import com.sjl.core.entity.EventBusDto;
import com.sjl.core.mvp.BaseActivity;
import com.sjl.core.mvp.BaseFragment;
import com.sjl.core.mvp.BasePresenter;
import com.sjl.core.net.RxBus;
import com.sjl.core.net.RxSchedulers;
import com.sjl.core.util.AppUtils;
import com.sjl.core.util.PreferencesHelper;
import com.sjl.core.util.SerializeUtils;
import com.sjl.core.util.datetime.TimeUtils;
import com.sjl.core.util.log.LogUtils;
import com.sjl.core.widget.imageview.CircleImageView;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import cn.feng.skin.manager.loader.SkinManager;
import cn.feng.skin.manager.statusbar.StatusBarUtil;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity<BasePresenter>
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mToolbar;
    private ImageView mToolBarIcon;
    private TextView mToolBarTitle;
    private DrawerLayout mDrawerLayout;
    private LinearLayout mNavHeader;

    private SharedPreferences sharedPreferences;
    private CircleImageView circleImageView;
    private TextView tvNickname;
    private TextView tvPersonality;

    private ViewPager mViewPager;
    private BottomNavigationView mNavigation;

    private List<BaseFragment> mFragments;
    private static final int REQUEST_SCAN = 0;
    private int mLastFgIndex;

    private MenuItem searchMenuItem;
    private ActionBarDrawerToggle mToggle;
    private long DOUBLE_CLICK_TIME = 0;
    private NotificationUtils notificationUtils;
    /**
     * 更换语言标志
     */
    public boolean executeChangeLanguage = false;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        mToolBarIcon = (ImageView) findViewById(R.id.tool_bar_icon);
        mToolBarTitle = findViewById(R.id.tool_bar_title);
        mToolbar = (Toolbar) findViewById(R.id.common_toolbar);

        mToolbar.setTitle("");

        setSupportActionBar(mToolbar);
        mViewPager = (ViewPager) findViewById(R.id.vp_main);

        mNavigation = (BottomNavigationView) findViewById(R.id.bnv_navigation);
//        NavigationViewHelper.disableShiftMode(mNavigation);
        //下面控制菜单显示
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        initStatusColor();

//        mToggle = new ActionBarDrawerToggle(
//                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        mDrawer.setDrawerListener(mToggle);
//        mToggle.syncState();


        //headerLayout id获取比较特殊
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        NavigationViewHelper.disableNavigationViewScrollbars(navigationView);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);//使菜单项图标显示图标原始颜色
        View headerView = navigationView.getHeaderView(0);
        mNavHeader = (LinearLayout) headerView.findViewById(R.id.ll_nav_header);

        WaveView waveView = (WaveView) headerView.findViewById(R.id.wave_view);
        circleImageView = (CircleImageView) headerView.findViewById(R.id.iv_head_img);
        tvNickname = (TextView) headerView.findViewById(R.id.tv_name);
        tvPersonality = (TextView) headerView.findViewById(R.id.tv_personality);
        initFragment();
        initWaveView(waveView);

        //switchFragment(0);
    }


    private void initStatusColor() {
        int color = SkinManager.getInstance().getColorPrimary();
        color = color != -1 ? color : getResources().getColor(R.color.colorPrimary);//必须指定定一个颜色值，不然更换主题时出现两个StatusBar
        StatusBarUtil.setColorNoTranslucentForDrawerLayout(this, mDrawerLayout, color);
    }


    @Override
    protected void changeStatusBarColor() {
        int color = SkinManager.getInstance().getColorPrimary();
        if (color != -1 && mDrawerLayout != null) {//不能少，否则状态栏无效
            StatusBarUtil.setColorNoTranslucentForDrawerLayout(this, mDrawerLayout, color);
        }
    }


    private void initWaveView(WaveView waveView) {
        //设置头像跟着波浪背景浮动
        final LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) circleImageView.getLayoutParams();
        waveView.setOnWaveAnimationListener(new WaveView.OnWaveAnimationListener() {
            @Override
            public void OnWaveAnimation(float y) {
                lp.setMargins(0, 0, 0, (int) y + 2);
                circleImageView.setLayoutParams(lp);
            }
        });
    }

    /**
     * 初始化fragment
     */
    private void initFragment() {
        mFragments = new ArrayList<>();
 /*       mFragments.add(new HomeFragment());
        mFragments.add(new CategoryFragment());
        mFragments.add(new ToolFragment());*/
        int position = 0;
        mFragments.add(instantiateFragment(mViewPager, position, new HomeFragment()));
        position++;
        mFragments.add(instantiateFragment(mViewPager, position, new CategoryFragment()));
        position++;
        mFragments.add(instantiateFragment(mViewPager, position, new ToolFragment()));
    }


    private BaseFragment instantiateFragment(ViewPager viewPager, int position, BaseFragment defaultResult) {
        String tag = "android:switcher:" + viewPager.getId() + ":" + position;
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        return fragment == null ? defaultResult : (BaseFragment) fragment;
    }

    /**
     * 切换fragment
     *
     * @param position 要显示的fragment的下标
     */

    private void switchFragment(int position) {
//        if (position >= mFragments.size()) {
//            return;
//        }
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        Fragment targetFg = mFragments.get(position);
//        Fragment lastFg = mFragments.get(mLastFgIndex);
//        mLastFgIndex = position;
//        ft.hide(lastFg);
//        if (!targetFg.isAdded()) {
//            ft.add(R.id.layout_fragment, targetFg);
//        }
//
//        ft.show(targetFg);
//        ft.commit();
    }

    @Override
    protected void initListener() {
        //设置mToolBarIcon监听
        mToolBarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(PersonCenterActivity.class);
            }
        });

        /**
         * 点击底部tab切换fragment
         */
        mNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
//                        mToggle.setDrawerIndicatorEnabled(true);//隐藏侧滑菜单按钮
                        if (searchMenuItem != null) {
                            searchMenuItem.setVisible(true);
                        }
                        setToolbarTitle(I18nUtils.getString(R.string.bottom_tab_home), true);
                        switchFragmentNew(0);
                        break;
                    case R.id.nav_category:
//                        mToggle.setDrawerIndicatorEnabled(false);
                        if (searchMenuItem != null) {
                            searchMenuItem.setVisible(false);
                        }
                        setToolbarTitle(I18nUtils.getString(R.string.bottom_tab_category), false);
                        switchFragmentNew(1);
                        break;
                    case R.id.nav_tool:
//                        mToggle.setDrawerIndicatorEnabled(false);
                        if (searchMenuItem != null) {
                            searchMenuItem.setVisible(false);
                        }
                        setToolbarTitle(I18nUtils.getString(R.string.bottom_tab_tool), false);
                        switchFragmentNew(2);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });


        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //ViewPager和BottomNaviationView联动,当ViewPager的某个页面被选中了,同时设置BottomNaviationView对应的tab按钮被选中
                switch (position) {
                    case 0:
                        mNavigation.setSelectedItemId(R.id.nav_home);
                        break;
                    case 1:
                        mNavigation.setSelectedItemId(R.id.nav_category);
                        break;
                    case 2:
                        mNavigation.setSelectedItemId(R.id.nav_tool);
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //为viewpager设置adapter
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(new MainViewPagerAdapter(getSupportFragmentManager(), mFragments));
    }

    /**
     * 设置定义toolbar标题和图标
     *
     * @param title
     * @param iconShow
     */
    private void setToolbarTitle(String title, boolean iconShow) {
        mToolBarTitle.setText(title);
        if (iconShow) {
            mToolBarIcon.setVisibility(View.VISIBLE);
        } else {
            mToolBarIcon.setVisibility(View.GONE);

        }
    }

    private void switchFragmentNew(int position) {
        mViewPager.setCurrentItem(position, false);

    }

    @Override
    protected void initData() {
        mLastFgIndex = 0;
        switchFragmentNew(mLastFgIndex);

        loadBookmark();
        initHeadImg();
        registerUpdateHeadImg();
        //限时大促
//        runnable.run();
        autoBackupCollection();
    }

    /**
     * 自动备份
     */
    private void autoBackupCollection() {
        final PreferencesHelper preferencesHelper = PreferencesHelper.getInstance(mContext);
        boolean isAutoBackup = (Boolean) preferencesHelper.get(AppConstant.SETTING.AUTO_BACKUP_COLLECTION, true);
        if (isAutoBackup) {
            long time = (long) preferencesHelper.get(AppConstant.SETTING.AUTO_BACKUP_COLLECTION_TIME, -1L);
            long dateDiff = TimeUtils.dateDiff(new Date(time), new Date());
            if (time == -1 || dateDiff > 2) {//两天备份一次
                CollectDaoImpl collectService = new CollectDaoImpl(mContext);
                final List<Collection> collection = collectService.findAllCollection();
                if (AppUtils.isEmpty(collection)) {
                    return;
                }
                //开始序列化数据
                Observable.just(collection)
                        .map(new Function<List<Collection>, Boolean>() {


                            @Override
                            public Boolean apply(List<Collection> collections) throws Exception {
                                boolean ret = SerializeUtils.serialize("collection", collections);
                                return ret;
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
//                    .as(MainActivity.this.<Boolean>bindLifecycle())//不知道为啥转换失败
                        .as(AutoDispose.<Boolean>autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                        .subscribe(new Observer<Boolean>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(Boolean ret) {
                                notificationUtils = new NotificationUtils(getBaseContext());
                                String msg = "";
                                if (ret) {
                                    msg = "本次备份我的收藏共" + collection.size() + "条";
                                } else {
                                    msg = "备份我的收藏失败";
                                }
                                notificationUtils.sendNotification(1, "自动备份", msg, null, null);
                                preferencesHelper.put(AppConstant.SETTING.AUTO_BACKUP_COLLECTION_TIME, System.currentTimeMillis());
                            }

                            @Override
                            public void onError(Throwable e) {
                                LogUtils.e("备份收藏异常", e);
                            }

                            @Override
                            public void onComplete() {
                                LogUtils.i("完成备份收藏");
                            }
                        });
            }

        }
    }


    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            String time = TimeUtils.getTimeDifference(new Date(), "2018-04-15 12:00:00", 0);
            if ("-1".equals(time)) {
                LogUtils.i("已经发完，欢迎下月再来");
            } else {
                LogUtils.i("离发工资还剩：" + time);
            }

            handler.postDelayed(this, 1000);
        }
    };

    private void initHeadImg() {

        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                File temp = new File(AppConstant.USER_HEAD_PATH + File.separator + "head_crop.jpg");
                if (!temp.exists()) {
                    LogUtils.i("头像不存在");
                    return;
                }
                FileInputStream fis = new FileInputStream(temp);
                Bitmap bitmap = BitmapFactory.decodeStream(fis);///把流转化为Bitmap图片
                emitter.onNext(bitmap);
                UserInfo userInfo = SerializeUtils.deserialize("userInfo", UserInfo.class);
                if (userInfo != null) {
                    emitter.onNext(userInfo);
                }

            }
        }).compose(RxSchedulers.applySchedulers())
                .as(bindLifecycle())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if (o instanceof Bitmap) {
                            Bitmap bitmap = (Bitmap) o;
                            circleImageView.setImageBitmap(bitmap);
                            mToolBarIcon.setImageBitmap(bitmap);
                        }
                        if (o instanceof UserInfo) {
                            UserInfo userInfo = (UserInfo) o;
                            tvNickname.setText(userInfo.getName());
                            tvPersonality.setText(userInfo.getPersonality());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("初始化头像失败",throwable);
                    }
                });
    }

    /**
     * 监听头像更新
     */
    public void registerUpdateHeadImg() {
       RxBus.getInstance()
                .toObservable(AppConstant.RxBusFlag.FLAG_2, EventBusDto.class)
                .compose(RxSchedulers.applySchedulers())
                .as(bindLifecycle())
                .subscribe(new Consumer<EventBusDto>() {
                    @Override
                    public void accept(EventBusDto s) throws Exception {
                        LogUtils.i("接收到头像更新：" + s);
                        if (s.getEventCode() == 0) {
                            initHeadImg();
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }


    private void loadBookmark() {
        sharedPreferences = this.getSharedPreferences("bookmark", Context.MODE_PRIVATE);
        boolean flag = sharedPreferences.getBoolean("readFlag", false);
        if (!flag) {
            initBookmarkData();
        } else {
            LogUtils.i("已经读取过书签");
        }
    }

    private void initBookmarkData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    long start = System.currentTimeMillis();
                    BookmarkParse bookmarkUtils = new BookmarkParse();
                    bookmarkUtils.readBookmarkHtml(MainActivity.this, "bookmarks_2019_12_24.html");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("readFlag", true);
                    editor.apply();
                    long end = System.currentTimeMillis();
                    LogUtils.i("读取书签文件耗时：" + (end - start));
                } catch (IOException e) {
                    Logger.e(e, "读取书签文件异常");
                }
            }
        }).start();
    }


    /**
     * onCreate之后执行
     * 每次在display Menu之前，都会去调用，只要按一次Menu按鍵，就会调用一次。所以可以在这里动态的改变menu。
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        searchMenuItem = menu.getItem(0);
        if (mLastFgIndex != 0) {
            searchMenuItem.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify item_bookmark_title parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            startActivity(new Intent(this, ArticleSearchActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_business_card) {
            startActivity(new Intent(this, MyCardActivity.class));
        } else if (id == R.id.nav_my_collection) {//我的收藏
            startActivity(new Intent(this, MyCollectionActivity.class));
        } else if (id == R.id.nav_bookmark) {
            startActivity(new Intent(this, BookmarkActivity.class));
        } else if (id == R.id.nav_skin) {
            startActivity(new Intent(this, ChangeSkinActivity.class));
        } else if (id == R.id.nav_scan) {
            Intent intent = new Intent(this, CaptureActivity.class);
            intent.putExtra("scan_title", getString(R.string.scan));
            startActivityForResult(intent, REQUEST_SCAN);
        } else if (id == R.id.nav_settings) {//设置
            startActivity(new Intent(this, SettingActivity.class));
//            String path = AppConstant.ROOT_PATH + "sample.pdf";
//            DocBrowserActivity.show(this,path);
        }
//防止卡顿
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        LogUtils.i("==========================================语言切换了");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.i("requestCode=" + requestCode + ",resultCode=" + resultCode);
        switch (requestCode) {
            case 300://原生裁剪后返回数据，暂时不用
                if (data != null) {
                    Bundle extras = data.getExtras();
                    Bitmap head = extras.getParcelable("data");

                    if (head != null) {
                        /**
                         * 上传服务器代码
                         */
                        saveHeadPic(head);// 保存在SD卡中
                        circleImageView.setImageBitmap(head);// 用ImageView显示出来
                        head.recycle();
                    }
                }
                break;

            case REQUEST_SCAN://扫一扫结果
                if (resultCode == RESULT_OK) {
                    String barCode = data.getStringExtra("barCode");
                    if (!TextUtils.isEmpty(barCode)) {
                        if (barCode.startsWith("http")) {
                            Intent intent = new Intent(this, BrowserActivity.class);
                            intent.putExtra(BrowserActivity.WEBVIEW_URL, barCode);
                            startActivity(intent);
                        } else {
                            scanResult(barCode);
                        }
                    }

                }
                break;
            default:
                break;

        }
    }

    private void scanResult(String text) {
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage(text)
                .setPositiveButton("确定", null)
                .show();
    }


    /**
     * 调用系统的裁剪功能,个别机型有bug,小米
     *
     * @param uri
     */
    @Deprecated
    public void cropPhoto(Uri uri) {
        try {
            Intent intent = new Intent("com.android.camera.action.CROP");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//授权标志
            }
            intent.setDataAndType(uri, "image/*");
            intent.putExtra("crop", "true");
            // aspectX aspectY 是裁剪框宽高的比例
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            // outputX outputY 是裁剪图片宽高
            intent.putExtra("outputX", 200);
            intent.putExtra("outputY", 200);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, 300);
        } catch (Exception e) {
            LogUtils.e("调用系统的裁剪功能异常", e);
        }
    }

    @Deprecated
    private void saveHeadPic(Bitmap mBitmap) {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            return;
        }
        FileOutputStream b = null;
        File file = new File(AppConstant.USER_HEAD_PATH);
        if (!file.exists()) {
            file.mkdirs();// 创建文件夹
        }
        String fileName = file + File.separator + "head.jpg";// 图片名字
        try {
            b = new FileOutputStream(fileName);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭流
                b.flush();
                b.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFragments = null;
        if (!executeChangeLanguage) {
            BrowseMapper.clearAll();
            killAll();
            System.exit(0);//退出虚拟机
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (System.currentTimeMillis() - DOUBLE_CLICK_TIME > 2000) {
                DOUBLE_CLICK_TIME = System.currentTimeMillis();
                Toast.makeText(this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
            } else {
                mFragments = null;
                super.onBackPressed();
            }

        }
    }
}
