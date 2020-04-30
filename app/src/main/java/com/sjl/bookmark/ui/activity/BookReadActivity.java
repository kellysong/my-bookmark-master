package com.sjl.bookmark.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;
import com.google.android.material.appbar.AppBarLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sjl.bookmark.R;
import com.sjl.bookmark.dao.impl.DaoFactory;
import com.sjl.bookmark.entity.zhuishu.table.BookChapter;
import com.sjl.bookmark.entity.zhuishu.table.CollectBook;
import com.sjl.bookmark.ui.contract.BookReadContract;
import com.sjl.bookmark.ui.presenter.BookReadPresenter;
import com.sjl.bookmark.util.BrightnessUtils;
import com.sjl.bookmark.util.SystemBarUtils;
import com.sjl.bookmark.util.WordUtils;
import com.sjl.bookmark.widget.reader.BookCategoryAdapter;
import com.sjl.bookmark.widget.reader.PageView;
import com.sjl.bookmark.widget.reader.ReadSettingDialog;
import com.sjl.bookmark.widget.reader.ReadSettingManager;
import com.sjl.bookmark.widget.reader.bean.TxtChapter;
import com.sjl.bookmark.widget.reader.loader.PageLoader;
import com.sjl.core.mvp.BaseActivity;
import com.sjl.core.net.RxBus;
import com.sjl.core.net.RxSchedulers;
import com.sjl.core.util.log.LogUtils;
import com.sjl.core.util.log.LoggerUtils;
import com.sjl.core.util.ViewUtils;
import com.sjl.core.util.datetime.TimeUtils;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import io.reactivex.functions.Consumer;

/**
 * 小说阅读器Activity
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookReadActivity.java
 * @time 2018/12/3 15:53
 * @copyright(C) 2018 song
 */
public class BookReadActivity extends BaseActivity<BookReadPresenter> implements BookReadContract.View {

    public static final int REQUEST_MORE_SETTING = 1;
    public static final String EXTRA_COLL_BOOK = "extra_coll_book";
    public static final String EXTRA_IS_COLLECTED = "extra_is_collected";

    // 注册 Brightness 的 uri
    private final Uri BRIGHTNESS_MODE_URI =
            Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS_MODE);
    private final Uri BRIGHTNESS_URI =
            Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
    private final Uri BRIGHTNESS_ADJ_URI =
            Settings.System.getUriFor("screen_auto_brightness_adj");

    private static final int WHAT_CATEGORY = 1;
    private static final int WHAT_CHAPTER = 2;

    @BindView(R.id.common_toolbar)
    Toolbar mToolBar;

    @BindView(R.id.read_dl_slide)
    DrawerLayout mDlSlide;
    /*************top_menu_view*******************/
    @BindView(R.id.read_abl_top_menu)
    AppBarLayout mAblTopMenu;
    @BindView(R.id.read_tv_brief)
    TextView mTvBrief;
    @BindView(R.id.read_tv_comment)
    TextView mBookComment;

    /***************content_view******************/
    @BindView(R.id.read_pv_page)
    FrameLayout mPageViewFrameLayout;
    PageView mPageView;
    /***************bottom_menu_view***************************/
    @BindView(R.id.read_tv_page_tip)
    TextView mTvPageTip;

    @BindView(R.id.read_ll_bottom_menu)
    LinearLayout mLlBottomMenu;
    @BindView(R.id.read_tv_pre_chapter)
    TextView mTvPreChapter;
    @BindView(R.id.read_sb_chapter_progress)
    SeekBar mSbChapterProgress;
    @BindView(R.id.read_tv_next_chapter)
    TextView mTvNextChapter;
    @BindView(R.id.read_tv_category)
    TextView mTvCategory;
    @BindView(R.id.read_tv_night_mode)
    TextView mTvNightMode;
    /*    @BindView(R.id.read_tv_download)
        TextView mTvDownload;*/
    @BindView(R.id.read_tv_setting)
    TextView mTvSetting;
    /***************left slide*******************************/
    @BindView(R.id.read_iv_category)
    ListView mLvCategory;
    /*****************view******************/
    private ReadSettingDialog mSettingDialog;
    private PageLoader mPageLoader;
    private Animation mTopInAnim;
    private Animation mTopOutAnim;
    private Animation mBottomInAnim;
    private Animation mBottomOutAnim;
    private BookCategoryAdapter mCategoryAdapter;
    private CollectBook mCollBook;
    //控制屏幕常亮
    private PowerManager.WakeLock mWakeLock;

    /***************params*****************/
    private boolean isCollected = false; // isFromSDCard
    private boolean isNightMode = false;
    private boolean isFullScreen = false;
    private boolean isRegistered = false;


    /**
     * 书籍章节id
     */
    private String mBookId;
    private MyHandler mHandler;
    private int convertType;


    @Override
    protected int getLayoutId() {
        //半透明化StatusBar并全屏
        SystemBarUtils.transparentStatusBar(this);
        return R.layout.book_read_activity;
    }

    @Override
    protected void initView() {
        //禁止滑动展示DrawerLayout
        mDlSlide.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        //侧边打开后，返回键能够起作用
        mDlSlide.setFocusableInTouchMode(false);
    }


    @Override
    protected void initListener() {

        //章节拖动
        mSbChapterProgress.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (mLlBottomMenu.getVisibility() == View.VISIBLE) {
                            //显示标题
                            mTvPageTip.setText((progress + 1) + "/" + (mSbChapterProgress.getMax() + 1));
                            mTvPageTip.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        //进行切换
                        int pagePos = mSbChapterProgress.getProgress();
                        if (pagePos != mPageLoader.getPagePos()) {
                            mPageLoader.skipToPage(pagePos);
                        }
                        //隐藏提示
                        mTvPageTip.setVisibility(View.GONE);
                    }
                }
        );


        /**
         * 章节目录条目点击
         */
        mLvCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mDlSlide.closeDrawer(Gravity.START);
                mPageLoader.skipToChapter(position);
            }
        });

        //菜单目录按钮
        mTvCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //移动到指定位置
                if (mCategoryAdapter.getCount() > 0) {
                    mLvCategory.setSelection(mPageLoader.getChapterPos());
                }
                //切换菜单
                toggleMenu(true);
                //打开侧滑动栏
                mDlSlide.openDrawer(Gravity.START);
            }
        });

        //字体设置
        mTvSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMenu(false);
                mSettingDialog.show();
            }
        });

        //菜单上一章按钮
        mTvPreChapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPageLoader.skipPreChapter()) {
                    mCategoryAdapter.setChapter(mPageLoader.getChapterPos());
                }
            }
        });
        //菜单下一章按钮
        mTvNextChapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPageLoader.skipNextChapter()) {
                    mCategoryAdapter.setChapter(mPageLoader.getChapterPos());
                }
            }
        });

        //日夜间模式切换
        mTvNightMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNightMode) {
                    isNightMode = false;
                } else {
                    isNightMode = true;
                }
                mPageLoader.setNightMode(isNightMode);
                toggleNightMode();
            }
        });
        //书评
        mBookComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(BookDetailActivity.EXTRA_BOOK_ID, mBookId);
                openActivity(BookMoreCommentActivity.class, bundle);
            }
        });

        //简介
        mTvBrief.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookDetailActivity.startActivity(BookReadActivity.this, mBookId);
            }
        });


    }

    @Override
    protected void initData() {
        mHandler = new MyHandler(this);
        setUpAdapter();

        //夜间模式按钮的状态
        toggleNightMode();

        //初始化屏幕亮度
        initBrightness();

        //初始化TopMenu
        initTopMenu();

        //初始化BottomMenu
        initBottomMenu();
        requestData();

    }


    /**
     * 请求参数
     */
    private void requestData() {
        mCollBook = getIntent().getParcelableExtra(EXTRA_COLL_BOOK);
        LogUtils.i(mCollBook.toString());
        //设置标题
        bindingToolbar(mToolBar, mCollBook.getTitle());

        isCollected = getIntent().getBooleanExtra(EXTRA_IS_COLLECTED, false);
        if (mCollBook.isLocal()) {//本地小说
            mBookComment.setVisibility(View.INVISIBLE);
            mTvBrief.setVisibility(View.INVISIBLE);
        } else {//在线小说
            mBookComment.setVisibility(View.VISIBLE);
            mTvBrief.setVisibility(View.VISIBLE);
        }
        isNightMode = ReadSettingManager.getInstance().isNightMode();
        isFullScreen = ReadSettingManager.getInstance().isFullScreen();
        convertType = ReadSettingManager.getInstance().getConvertType();
        String id = mCollBook.get_id();
        if (mBookId != null) {
            if (mBookId.equals(id)) {//说明是从详情页继续阅读同一本书
                LogUtils.w("当前正在阅读同一本书");
                return;
            } else {
                LogUtils.w("当前正在阅读其它书");
            }
        }
        mBookId = id;

        //获取页面加载器
        initPageLoader();


        // 如果是已经收藏的，那么就从数据库中获取目录
        if (isCollected) {
            LogUtils.i("本地收藏阅读");
            DaoFactory.getBookChapterDao()
                    .getBookChaptersInRx(mBookId)
                    .compose(RxSchedulers.<List<BookChapter>>applySingle())
                    .as(this.<List<BookChapter>>bindLifecycle())
                    .subscribe(new Consumer<List<BookChapter>>() {
                        @Override
                        public void accept(List<BookChapter> bookChapters) throws Exception {
                            // 设置 CollBook
                            mPageLoader.getCollBook().setBookChapters(bookChapters);
                            // 刷新章节列表
                            mPageLoader.refreshChapterList();
                            // 如果是网络小说并被标记更新的，则从网络下载目录
                            if (mCollBook.isUpdate() && !mCollBook.isLocal()) {
                                mPresenter.loadCategory(mBookId);
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            LogUtils.e(throwable);
                        }
                    });
        } else {
            LogUtils.i("在线阅读");

            // 从网络中获取目录
            mPresenter.loadCategory(mBookId);
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        LogUtils.i("onNewIntent栈内复用，从详情页进入阅读");
        requestData();
    }

    private void initPageLoader() {
        mPageViewFrameLayout.removeAllViews();
        mPageView = null;
        mPageView = new PageView(this);
        mPageViewFrameLayout.addView(mPageView);
        // 如果 API < 18 取消硬件加速
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mPageView.setLayerType(ViewCompat.LAYER_TYPE_SOFTWARE, null);
        }

        mPageView.setTouchListener(new PageView.TouchListener() {
            @Override
            public boolean onTouch() {
                return !hideReadMenu();
            }

            @Override
            public void center() {
                toggleMenu(true);//点击中间显示菜单
            }

            @Override
            public void prePage() {
            }

            @Override
            public void nextPage() {
            }

            @Override
            public void cancel() {
            }
        });
        //隐藏StatusBar
        mPageView.post(new Runnable() {
            @Override
            public void run() {
                hideSystemBar();
            }
        });

        mPageLoader = mPageView.getPageLoader(mCollBook);
        mPageLoader.setOnPageChangeListener(
                new PageLoader.OnPageChangeListener() {

                    @Override
                    public void onChapterChange(int pos) {
                        mCategoryAdapter.setChapter(pos);
                    }

                    @Override
                    public void requestChapters(List<TxtChapter> requestChapters) {
                        //章节切换时回调
                        mPresenter.loadChapter(mBookId, requestChapters);
                        mHandler.sendEmptyMessage(WHAT_CATEGORY);
                        //隐藏提示
                        mTvPageTip.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCategoryFinish(List<TxtChapter> chapters) {
                        for (TxtChapter chapter : chapters) {
                            chapter.title = WordUtils.convertCC(chapter.title, mPageView.getContext());
                        }
                        mCategoryAdapter.refreshItems(chapters);
                    }

                    @Override
                    public void onPageCountChange(int count) {
                        mSbChapterProgress.setMax(Math.max(0, count - 1));
                        mSbChapterProgress.setProgress(0);
                        // 如果处于错误状态，那么就冻结使用
                        if (mPageLoader.getPageStatus() == PageLoader.STATUS_LOADING
                                || mPageLoader.getPageStatus() == PageLoader.STATUS_ERROR) {
                            mSbChapterProgress.setEnabled(false);
                        } else {
                            mSbChapterProgress.setEnabled(true);
                        }
                    }

                    @Override
                    public void onPageChange(final int pos) {
                        mSbChapterProgress.post(new Runnable() {
                            @Override
                            public void run() {
                                mSbChapterProgress.setProgress(pos);
                            }
                        });
                    }
                }
        );

        mSettingDialog = new ReadSettingDialog(this, mPageLoader);
        //设置dialog
        mSettingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                hideSystemBar();
            }
        });
    }

    /**
     * 适配器初始化
     */
    private void setUpAdapter() {
        mCategoryAdapter = new BookCategoryAdapter(this, R.layout.book_category_recycle_item, null);
        mLvCategory.setAdapter(mCategoryAdapter);
        mLvCategory.setFastScrollEnabled(true);//禁用快速滚动
    }

    /**
     * 日夜模式图标切换
     */
    private void toggleNightMode() {
        if (isNightMode) {
            mTvNightMode.setText(getString(R.string.nb_mode_morning));
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_read_menu_morning);
            mTvNightMode.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
        } else {
            mTvNightMode.setText(getString(R.string.nb_mode_night));
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_read_menu_night);
            mTvNightMode.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
        }
    }

    /**
     * 初始化屏幕亮度
     */
    private void initBrightness() {
        //设置当前Activity的Brightness
        if (ReadSettingManager.getInstance().isBrightnessAuto()) {
            BrightnessUtils.setDefaultBrightness(this);
        } else {
            BrightnessUtils.setBrightness(this, ReadSettingManager.getInstance().getBrightness());
        }

        //初始化屏幕常亮类
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "keep bright");
    }

    /**
     * 隐藏阅读界面的菜单显示
     *
     * @return 是否隐藏成功
     */
    private boolean hideReadMenu() {
        hideSystemBar();
        if (mAblTopMenu.getVisibility() == View.VISIBLE) {
            toggleMenu(true);
            return true;
        } else if (mSettingDialog.isShowing()) {
            mSettingDialog.dismiss();
            return true;
        }
        return false;
    }

    /**
     * 切换菜单栏的可视状态
     * 默认是隐藏的
     */
    private void toggleMenu(boolean hideStatusBar) {
        initMenuAnim();

        if (mAblTopMenu.getVisibility() == View.VISIBLE) {
            //关闭
            mAblTopMenu.startAnimation(mTopOutAnim);
            mLlBottomMenu.startAnimation(mBottomOutAnim);
            mAblTopMenu.setVisibility(View.GONE);
            mLlBottomMenu.setVisibility(View.GONE);
            mTvPageTip.setVisibility(View.GONE);

            if (hideStatusBar) {
                hideSystemBar();
            }
        } else {
            mAblTopMenu.setVisibility(View.VISIBLE);
            mLlBottomMenu.setVisibility(View.VISIBLE);
            mAblTopMenu.startAnimation(mTopInAnim);
            mLlBottomMenu.startAnimation(mBottomInAnim);

            showSystemBar();
        }
    }

    //初始化菜单动画
    private void initMenuAnim() {
        if (mTopInAnim != null) return;

        mTopInAnim = AnimationUtils.loadAnimation(this, R.anim.slide_top_in);
        mTopOutAnim = AnimationUtils.loadAnimation(this, R.anim.slide_top_out);
        mBottomInAnim = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_in);
        mBottomOutAnim = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_out);
        //退出的速度要快
        mTopOutAnim.setDuration(200);
        mBottomOutAnim.setDuration(200);
    }


    /**
     * 显示状态栏
     */
    private void showSystemBar() {

        SystemBarUtils.showUnStableStatusBar(this);
        if (isFullScreen) {
            SystemBarUtils.showUnStableNavBar(this);
        }
    }

    /**
     * 隐藏状态栏
     */
    private void hideSystemBar() {
        SystemBarUtils.hideStableStatusBar(this);
        if (isFullScreen) {
            SystemBarUtils.hideStableNavBar(this);
        }
    }


    private void initTopMenu() {
        if (Build.VERSION.SDK_INT >= 19) {
            mAblTopMenu.setPadding(0, ViewUtils.getStatusBarHeight(), 0, 0);
        }
    }

    private void initBottomMenu() {
        //判断是否需要全屏
        if (ReadSettingManager.getInstance().isFullScreen()) {
            //还需要设置mBottomMenu的底部高度
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mLlBottomMenu.getLayoutParams();
            params.bottomMargin = ViewUtils.getNavigationBarHeight();
            mLlBottomMenu.setLayoutParams(params);
        } else {
            //设置mBottomMenu的底部距离
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mLlBottomMenu.getLayoutParams();
            params.bottomMargin = 0;
            mLlBottomMenu.setLayoutParams(params);
        }
    }

    /**
     * @param context
     * @param collBook
     * @param isCollected true表示来自书架
     */
    public static void startActivity(Context context, CollectBook collBook, boolean isCollected) {
        context.startActivity(new Intent(context, BookReadActivity.class)
                .putExtra(EXTRA_IS_COLLECTED, isCollected)
                .putExtra(EXTRA_COLL_BOOK, collBook));
    }

    @Override
    public void showCategory(List<BookChapter> bookChapterList) {
        mPageLoader.getCollBook().setBookChapters(bookChapterList);
        mPageLoader.refreshChapterList();
        mCollBook.setBookChapters(bookChapterList);//从搜索过来的时候特别注意设置更新目录

        // 如果是目录更新的情况，那么就需要存储更新数据
        if (mCollBook.isUpdate() && isCollected) {
            DaoFactory.getBookChapterDao()
                    .saveBookChaptersWithAsync(bookChapterList);
        }
    }

    @Override
    public void finishChapter() {
        if (mPageLoader.getPageStatus() == PageLoader.STATUS_LOADING) {
            mHandler.sendEmptyMessage(WHAT_CHAPTER);
        }
        // 当完成章节的时候，刷新列表
        mCategoryAdapter.notifyDataSetChanged();
    }

    @Override
    public void errorChapter() {
        if (mPageLoader.getPageStatus() == PageLoader.STATUS_LOADING) {
            mPageLoader.chapterError();
        }
    }

    private static class MyHandler extends Handler {
        private WeakReference<Context> reference;

        public MyHandler(Context context) {
            reference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BookReadActivity activity = (BookReadActivity) reference.get();
            if (activity != null) {
                switch (msg.what) {
                    case WHAT_CATEGORY://目录
                        activity.mLvCategory.setSelection(activity.mPageLoader.getChapterPos());
                        break;
                    case WHAT_CHAPTER://章节
                        activity.mPageLoader.openChapter();
                        break;
                }
            }
        }
    }

    // 接收电池信息和时间更新的广播
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
                int level = intent.getIntExtra("level", 0);
                if (mPageLoader != null) {
                    mPageLoader.updateBattery(level);

                }
            }
            // 监听分钟的变化
            else if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
                if (mPageLoader != null) {
                    mPageLoader.updateTime();
                }
            }
        }
    };

    // 亮度调节监听
    // 由于亮度调节没有 Broadcast 而是直接修改 ContentProvider 的。所以需要创建一个 Observer 来监听 ContentProvider 的变化情况。
    private ContentObserver mBrightObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange);

            // 判断当前是否跟随屏幕亮度，如果不是则返回
            if (selfChange || !mSettingDialog.isBrightFollowSystem()) return;

            // 如果系统亮度改变，则修改当前 Activity 亮度
            if (BRIGHTNESS_MODE_URI.equals(uri)) {
                LoggerUtils.d("亮度模式改变");
            } else if (BRIGHTNESS_URI.equals(uri) && !BrightnessUtils.isAutoBrightness(BookReadActivity.this)) {
                LoggerUtils.d("亮度模式为手动模式 值改变");
                BrightnessUtils.setBrightness(BookReadActivity.this, BrightnessUtils.getScreenBrightness(BookReadActivity.this));
            } else if (BRIGHTNESS_ADJ_URI.equals(uri) && BrightnessUtils.isAutoBrightness(BookReadActivity.this)) {
                LoggerUtils.d("亮度模式为自动模式 值改变");
                BrightnessUtils.setDefaultBrightness(BookReadActivity.this);
            } else {
                LoggerUtils.d("亮度调整 其他");
            }
        }
    };

    /**
     * 注册亮度观察者
     */
    private void registerBrightObserver() {
        try {
            if (mBrightObserver != null) {
                if (!isRegistered) {
                    final ContentResolver cr = getContentResolver();
                    cr.unregisterContentObserver(mBrightObserver);
                    cr.registerContentObserver(BRIGHTNESS_MODE_URI, false, mBrightObserver);
                    cr.registerContentObserver(BRIGHTNESS_URI, false, mBrightObserver);
                    cr.registerContentObserver(BRIGHTNESS_ADJ_URI, false, mBrightObserver);
                    isRegistered = true;
                }
            }
        } catch (Throwable throwable) {
            LogUtils.e("register mBrightObserver error! " + throwable);
        }
    }

    /**
     * 解注册
     */
    private void unregisterBrightObserver() {
        try {
            if (mBrightObserver != null) {
                if (isRegistered) {
                    getContentResolver().unregisterContentObserver(mBrightObserver);
                    isRegistered = false;
                }
            }
        } catch (Throwable throwable) {
            LogUtils.e("unregister BrightnessObserver error! " + throwable);
        }
    }

    @Override
    public void onBackPressed() {
        if (mAblTopMenu.getVisibility() == View.VISIBLE) {
            // 非全屏下才收缩，全屏下直接退出
            if (!ReadSettingManager.getInstance().isFullScreen()) {
                toggleMenu(true);
                return;
            }
        } else if (mSettingDialog.isShowing()) {
            mSettingDialog.dismiss();
            return;
        } else if (mDlSlide.isDrawerOpen(Gravity.START)) {
            mDlSlide.closeDrawer(Gravity.START);
            return;
        }

        if (!mCollBook.isLocal() && !isCollected
                && mCollBook.getBookChapters() != null && !mCollBook.getBookChapters().isEmpty()) {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("加入书架")
                    .setMessage("喜欢本书就加入书架吧")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //设置为已收藏
                            isCollected = true;
                            //设置阅读时间
                            mCollBook.setLastRead(TimeUtils.
                                    formatDateToStr(System.currentTimeMillis(), TimeUtils.DATE_FORMAT_7));

                            DaoFactory.getCollectBookDao()
                                    .saveCollBookWithAsync(mCollBook);
                            RxBus.getInstance().post(true);//刷新书架
                            exit();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            exit();
                        }
                    }).create();
            alertDialog.show();
        } else {
            exit();
        }
    }

    // 退出
    private void exit() {
        //从详情页进入阅读，再返回给详情页,回调给详情页的onActivityResult
        Intent result = new Intent();
        result.putExtra(BookDetailActivity.RESULT_IS_COLLECTED, isCollected);
        setResult(Activity.RESULT_OK, result);
        // 退出
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerBrightObserver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWakeLock.acquire();//保持唤醒
        registerBatteryReceiver();
    }

    /**
     * 注册电池广播
     * activity的生命周期方法基本上是成对出现的，例如onCreate对应onDestory，onStart对应onStop，onResume对于onPause
     */
    private void registerBatteryReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(mReceiver, intentFilter);

    }

    private void unregisterBatteryReceiver() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWakeLock.release();//释放掉该锁
        if (isCollected) {
            mPageLoader.saveRecord();
        }
        unregisterBatteryReceiver();
    }


    @Override
    protected void onStop() {
        super.onStop();
        unregisterBrightObserver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeMessages(WHAT_CATEGORY);
            mHandler.removeMessages(WHAT_CHAPTER);
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        mPageLoader.closeBook();
        mPageLoader = null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean isVolumeTurnPage = ReadSettingManager
                .getInstance().isVolumeTurnPage();
        //音量键翻页
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP://上一页
                if (isVolumeTurnPage) {
                    return mPageLoader.skipToPrePage();
                }
                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN://下一页
                if (isVolumeTurnPage) {
                    return mPageLoader.skipToNextPage();
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SystemBarUtils.hideStableStatusBar(this);
        if (requestCode == REQUEST_MORE_SETTING) {
            boolean fullScreen = ReadSettingManager.getInstance().isFullScreen();
            if (isFullScreen != fullScreen) {
                isFullScreen = fullScreen;
                // 刷新BottomMenu
                initBottomMenu();
            }

            // 设置显示状态
            if (isFullScreen) {
                SystemBarUtils.hideStableNavBar(this);//隐藏虚拟导航栏
            } else {
                SystemBarUtils.showStableNavBar(this);//显示虚拟导航栏
            }
            int tempConvertType = ReadSettingManager.getInstance().getConvertType();
            LogUtils.i("currentConvertType:" + convertType + ",tempConvertType:" + tempConvertType);
            if (convertType != tempConvertType && tempConvertType != 0) {
                //修复语言改变时不能刷新章节语言问题
                mPageLoader.refreshChapter(mPageLoader.getChapterPos());
                convertType = tempConvertType;

            }
        }
    }


}
