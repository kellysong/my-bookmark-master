package com.sjl.bookmark.ui.activity

import android.annotation.SuppressLint
import android.content.*
import android.database.ContentObserver
import android.net.Uri
import android.os.*
import android.os.Build.VERSION_CODES
import android.os.PowerManager.WakeLock
import android.provider.Settings
import android.view.*
import android.view.ViewGroup.MarginLayoutParams
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.sjl.bookmark.R
import com.sjl.bookmark.dao.impl.DaoFactory
import com.sjl.bookmark.entity.zhuishu.table.BookChapter
import com.sjl.bookmark.entity.zhuishu.table.CollectBook
import com.sjl.bookmark.ui.activity.BookDetailActivity
import com.sjl.bookmark.ui.activity.BookReadActivity
import com.sjl.bookmark.ui.contract.BookReadContract
import com.sjl.bookmark.ui.presenter.BookReadPresenter
import com.sjl.bookmark.util.BrightnessUtils
import com.sjl.bookmark.util.SystemBarUtils
import com.sjl.bookmark.util.WordUtils
import com.sjl.bookmark.widget.reader.BookCategoryAdapter
import com.sjl.bookmark.widget.reader.PageView
import com.sjl.bookmark.widget.reader.PageView.TouchListener
import com.sjl.bookmark.widget.reader.ReadSettingDialog
import com.sjl.bookmark.widget.reader.ReadSettingManager
import com.sjl.bookmark.widget.reader.bean.TxtChapter
import com.sjl.bookmark.widget.reader.loader.PageLoader
import com.sjl.core.mvp.BaseActivity
import com.sjl.core.net.RxBus
import com.sjl.core.net.RxSchedulers
import com.sjl.core.util.*
import com.sjl.core.util.datetime.TimeUtils
import com.sjl.core.util.log.LogUtils
import com.sjl.core.util.log.LoggerUtils
import io.reactivex.Observable
import io.reactivex.functions.Function
import kotlinx.android.synthetic.main.book_read_activity.*
import java.lang.ref.WeakReference

/**
 * 小说阅读器Activity
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookReadActivity.java
 * @time 2018/12/3 15:53
 * @copyright(C) 2018 song
 */
class BookReadActivity : BaseActivity<BookReadPresenter>(), BookReadContract.View {
    // 注册 Brightness 的 uri
    private val BRIGHTNESS_MODE_URI =
        Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS_MODE)
    private val BRIGHTNESS_URI = Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS)
    private val BRIGHTNESS_ADJ_URI = Settings.System.getUriFor("screen_auto_brightness_adj")


    var mPageView: PageView? = null


    /*****************view */
    private var mSettingDialog: ReadSettingDialog? = null
    private var mPageLoader: PageLoader? = null
    private var mTopInAnim: Animation? = null
    private var mTopOutAnim: Animation? = null
    private var mBottomInAnim: Animation? = null
    private var mBottomOutAnim: Animation? = null
    private lateinit var mCategoryAdapter: BookCategoryAdapter
    private lateinit var mCollBook: CollectBook

    //控制屏幕常亮
    private var mWakeLock: WakeLock? = null

    /***************params */
    private var isCollected = false // isFromSDCard
    private var isNightMode = false
    private var isFullScreen = false
    private var isRegistered = false

    /**
     * 书籍章节id
     */
    private var mBookId: String? = null
    private lateinit var mHandler: MyHandler
    private var convertType = 0
    override fun getLayoutId(): Int {
        //半透明化StatusBar并全屏
        SystemBarUtils.transparentStatusBar(this)
        return R.layout.book_read_activity
    }

    override fun initView() {
        //禁止滑动展示DrawerLayout
        read_dl_slide.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        //侧边打开后，返回键能够起作用
        read_dl_slide.isFocusableInTouchMode = false
    }

    @SuppressLint("WrongConstant")
    override fun initListener() {

        //章节拖动
        read_sb_chapter_progress.setOnSeekBarChangeListener(
            object : OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    if (read_ll_bottom_menu.visibility == View.VISIBLE) {
                        //显示标题
                        read_tv_page_tip.text =
                            (progress + 1).toString() + "/" + (read_sb_chapter_progress!!.max + 1)
                        read_tv_page_tip.visibility = View.VISIBLE
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    //进行切换
                    val pagePos = read_sb_chapter_progress.progress
                    if (pagePos != mPageLoader!!.pagePos) {
                        mPageLoader!!.skipToPage(pagePos)
                    }
                    //隐藏提示
                    read_tv_page_tip.visibility = View.GONE
                }
            }
        )
        /**
         * 章节目录条目点击
         */
        read_iv_category.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                read_dl_slide.closeDrawer(Gravity.START)
                mPageLoader!!.skipToChapter(position)
            }

        //菜单目录按钮
        read_tv_category.setOnClickListener { //移动到指定位置
            if (mCategoryAdapter.count > 0) {
                read_iv_category.setSelection(mPageLoader!!.chapterPos)
            }
            //切换菜单
            toggleMenu(true)
            //打开侧滑动栏
            read_dl_slide.openDrawer(Gravity.START)
        }

        //字体设置
        read_tv_setting.setOnClickListener {
            toggleMenu(false)
            mSettingDialog!!.show()
        }

        //菜单上一章按钮
        read_tv_pre_chapter.setOnClickListener {
            if (mPageLoader!!.skipPreChapter()) {
                mCategoryAdapter.setChapter(mPageLoader!!.chapterPos)
            }
        }
        //菜单下一章按钮
        read_tv_next_chapter.setOnClickListener {
            if (mPageLoader!!.skipNextChapter()) {
                mCategoryAdapter.setChapter(mPageLoader!!.chapterPos)
            }
        }

        //日夜间模式切换
        read_tv_night_mode.setOnClickListener {
            isNightMode = !isNightMode
            mPageLoader!!.setNightMode(isNightMode)
            toggleNightMode()
        }
        //书评
        read_tv_comment.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(BookDetailActivity.Companion.EXTRA_BOOK_ID, mBookId)
            openActivity(BookMoreCommentActivity::class.java, bundle)
        }

        //简介
        read_tv_brief.setOnClickListener {
            BookDetailActivity.Companion.startActivity(
                this@BookReadActivity,
                mBookId
            )
        }
    }

    override fun initData() {
        mHandler = MyHandler(this)
        setUpAdapter()

        //夜间模式按钮的状态
        toggleNightMode()

        //初始化屏幕亮度
        initBrightness()

        //初始化TopMenu
        initTopMenu()

        //初始化BottomMenu
        initBottomMenu()
        requestData()
    }

    /**
     * 请求参数
     */
    private fun requestData() {
        mCollBook = intent.getParcelableExtra(EXTRA_COLL_BOOK)
        LogUtils.i(mCollBook.toString())
        //设置标题
        bindingToolbar(common_toolbar, mCollBook.title)
        isCollected = intent.getBooleanExtra(EXTRA_IS_COLLECTED, false)
        if (mCollBook.isLocal()) { //本地小说
            read_tv_comment.visibility = View.INVISIBLE
            read_tv_brief.visibility = View.INVISIBLE
        } else { //在线小说
            read_tv_comment.visibility = View.VISIBLE
            read_tv_brief.visibility = View.VISIBLE
        }
        isNightMode = ReadSettingManager.getInstance().isNightMode
        isFullScreen = ReadSettingManager.getInstance().isFullScreen
        convertType = ReadSettingManager.getInstance().convertType
        val id = mCollBook._id
        if (mBookId != null) {
            if (mBookId == id) { //说明是从详情页继续阅读同一本书
                LogUtils.w("当前正在阅读同一本书")
                return
            } else {
                LogUtils.w("当前正在阅读其它书")
            }
        }
        mBookId = id

        //获取页面加载器
        initPageLoader()


        // 如果是已经收藏的，那么就从数据库中获取目录
        if (isCollected) {
            LogUtils.i("本地收藏阅读")
            DaoFactory.getBookChapterDao()
                .getBookChaptersInRx(mBookId)
                .compose(RxSchedulers.applySingle())
                .`as`(bindLifecycle())
                .subscribe({ bookChapters -> // 设置 CollBook
                    mPageLoader!!.collBook.bookChapters = bookChapters
                    // 刷新章节列表
                    mPageLoader!!.refreshChapterList()
                    // 如果是网络小说并被标记更新的，则从网络下载目录
                    if (mCollBook.isUpdate() && !mCollBook.isLocal()) {
                        mPresenter!!.loadCategory(mBookId!!)
                    }
                }) { throwable -> LogUtils.e(throwable) }
        } else {
            LogUtils.i("在线阅读")

            // 从网络中获取目录
            mPresenter!!.loadCategory(mBookId!!)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        LogUtils.i("onNewIntent栈内复用，从详情页进入阅读")
        requestData()
    }

    private fun initPageLoader() {
        read_pv_page!!.removeAllViews()
        mPageView = null
        mPageView = PageView(this)
        read_pv_page!!.addView(mPageView)
        // 如果 API < 18 取消硬件加速
        if (Build.VERSION.SDK_INT < VERSION_CODES.JELLY_BEAN_MR2
            && Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB
        ) {
            mPageView!!.setLayerType(ViewCompat.LAYER_TYPE_SOFTWARE, null)
        }
        mPageView!!.setTouchListener(object : TouchListener {
            override fun onTouch(): Boolean {
                return !hideReadMenu()
            }

            override fun center() {
                toggleMenu(true) //点击中间显示菜单
            }

            override fun prePage() {}
            override fun nextPage() {}
            override fun cancel() {}
        })
        //隐藏StatusBar
        mPageView!!.post { hideSystemBar() }
        mPageLoader = mPageView!!.getPageLoader(mCollBook)
        mPageLoader?.setOnPageChangeListener(
            object : PageLoader.OnPageChangeListener {
                override fun onChapterChange(pos: Int) {
                    mCategoryAdapter.setChapter(pos)
                }

                override fun requestChapters(requestChapters: List<TxtChapter>) {
                    //章节切换时回调
                    mPresenter!!.loadChapter(mBookId!!, requestChapters)
                    mHandler.sendEmptyMessage(WHAT_CATEGORY)
                    //隐藏提示
                    read_tv_page_tip.visibility = View.GONE
                }

                override fun onCategoryFinish(chapters: List<TxtChapter>) {
                    for (chapter in chapters) {
                        chapter.title = WordUtils.convertCC(chapter.title, mPageView!!.context)
                    }
                    mCategoryAdapter.refreshItems(chapters)
                }

                override fun onPageCountChange(count: Int) {
                    read_sb_chapter_progress!!.max = Math.max(0, count - 1)
                    read_sb_chapter_progress!!.progress = 0
                    // 如果处于错误状态，那么就冻结使用
                    read_sb_chapter_progress!!.isEnabled =
                        !(mPageLoader?.pageStatus == PageLoader.STATUS_LOADING
                                || mPageLoader?.pageStatus == PageLoader.STATUS_ERROR)
                }

                override fun onPageChange(pos: Int) {
                    read_sb_chapter_progress!!.post { read_sb_chapter_progress!!.progress = pos }
                }
            }
        )
        mSettingDialog = ReadSettingDialog(this, mPageLoader)
        //设置dialog
        mSettingDialog!!.setOnDismissListener { hideSystemBar() }
    }

    /**
     * 适配器初始化
     */
    private fun setUpAdapter() {
        mCategoryAdapter = BookCategoryAdapter(this, R.layout.book_category_recycle_item, null)
        read_iv_category.adapter = mCategoryAdapter
        read_iv_category.isFastScrollEnabled = true //禁用快速滚动
    }

    /**
     * 日夜模式图标切换
     */
    private fun toggleNightMode() {
        if (isNightMode) {
            read_tv_night_mode.text = getString(R.string.nb_mode_morning)
            val drawable = ContextCompat.getDrawable(this, R.drawable.ic_read_menu_morning)
            read_tv_night_mode.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
        } else {
            read_tv_night_mode.text = getString(R.string.nb_mode_night)
            val drawable = ContextCompat.getDrawable(this, R.drawable.ic_read_menu_night)
            read_tv_night_mode.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
        }
    }

    /**
     * 初始化屏幕亮度
     */
    @SuppressLint("InvalidWakeLockTag")
    private fun initBrightness() {
        //设置当前Activity的Brightness
        if (ReadSettingManager.getInstance().isBrightnessAuto) {
            BrightnessUtils.setDefaultBrightness(this)
        } else {
            BrightnessUtils.setBrightness(this, ReadSettingManager.getInstance().brightness)
        }

        //初始化屏幕常亮类
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "keep bright")
    }

    /**
     * 隐藏阅读界面的菜单显示
     *
     * @return 是否隐藏成功
     */
    private fun hideReadMenu(): Boolean {
        hideSystemBar()
        if (read_abl_top_menu.visibility == View.VISIBLE) {
            toggleMenu(true)
            return true
        } else if (mSettingDialog!!.isShowing) {
            mSettingDialog!!.dismiss()
            return true
        }
        return false
    }

    /**
     * 切换菜单栏的可视状态
     * 默认是隐藏的
     */
    private fun toggleMenu(hideStatusBar: Boolean) {
        initMenuAnim()
        if (read_abl_top_menu.visibility == View.VISIBLE) {
            //关闭
            read_abl_top_menu.startAnimation(mTopOutAnim)
            read_ll_bottom_menu.startAnimation(mBottomOutAnim)
            read_abl_top_menu.visibility = View.GONE
            read_ll_bottom_menu.visibility = View.GONE
            read_tv_page_tip.visibility = View.GONE
            if (hideStatusBar) {
                hideSystemBar()
            }
        } else {
            read_abl_top_menu.visibility = View.VISIBLE
            read_ll_bottom_menu.visibility = View.VISIBLE
            read_abl_top_menu.startAnimation(mTopInAnim)
            read_ll_bottom_menu.startAnimation(mBottomInAnim)
            showSystemBar()
        }
    }

    //初始化菜单动画
    private fun initMenuAnim() {
        if (mTopInAnim != null) return
        mTopInAnim = AnimationUtils.loadAnimation(this, R.anim.slide_top_in)
        mTopOutAnim = AnimationUtils.loadAnimation(this, R.anim.slide_top_out)
        mBottomInAnim = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_in)
        mBottomOutAnim = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_out)
        //退出的速度要快
        mTopOutAnim?.duration = 200
        mBottomOutAnim?.duration = 200
    }

    /**
     * 显示状态栏
     */
    private fun showSystemBar() {
        SystemBarUtils.showUnStableStatusBar(this)
        if (isFullScreen) {
            SystemBarUtils.showUnStableNavBar(this)
        }
    }

    /**
     * 隐藏状态栏
     */
    private fun hideSystemBar() {
        SystemBarUtils.hideStableStatusBar(this)
        if (isFullScreen) {
            SystemBarUtils.hideStableNavBar(this)
        }
    }

    private fun initTopMenu() {
        if (Build.VERSION.SDK_INT >= 19) {
            read_abl_top_menu.setPadding(0, ViewUtils.getStatusBarHeight(), 0, 0)
        }
    }

    private fun initBottomMenu() {
        //判断是否需要全屏
        if (ReadSettingManager.getInstance().isFullScreen) {
            //还需要设置mBottomMenu的底部高度
            val params = read_ll_bottom_menu!!.layoutParams as MarginLayoutParams
            params.bottomMargin = ViewUtils.getNavigationBarHeight()
            read_ll_bottom_menu.layoutParams = params
        } else {
            //设置mBottomMenu的底部距离
            val params = read_ll_bottom_menu!!.layoutParams as MarginLayoutParams
            params.bottomMargin = 0
            read_ll_bottom_menu.layoutParams = params
        }
    }

    override fun showCategory(bookChapterList: List<BookChapter>) {
        mPageLoader!!.collBook.bookChapters = bookChapterList
        mPageLoader!!.refreshChapterList()
        mCollBook.bookChapters = bookChapterList //从搜索过来的时候特别注意设置更新目录

        // 如果是目录更新的情况，那么就需要存储更新数据
        if (mCollBook.isUpdate() && isCollected) {
            DaoFactory.getBookChapterDao()
                .saveBookChaptersWithAsync(bookChapterList)
        }
    }

    override fun finishChapter() {
        if (mPageLoader!!.pageStatus == PageLoader.STATUS_LOADING) {
            mHandler.sendEmptyMessage(WHAT_CHAPTER)
        }
        // 当完成章节的时候，刷新列表
        mCategoryAdapter.notifyDataSetChanged()
    }

    override fun errorChapter() {
        if (mPageLoader!!.pageStatus == PageLoader.STATUS_LOADING) {
            mPageLoader!!.chapterError()
        }
    }

    private class MyHandler(context: Context) : Handler() {
        private val reference: WeakReference<Context>
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val activity = reference.get() as BookReadActivity?
            if (activity != null) {
                when (msg.what) {
                    WHAT_CATEGORY -> activity.read_iv_category!!.setSelection(
                        activity.mPageLoader!!.chapterPos
                    )
                    WHAT_CHAPTER -> activity.mPageLoader!!.openChapter()
                }
            }
        }

        init {
            reference = WeakReference(context)
        }
    }

    // 接收电池信息和时间更新的广播
    private val mReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_BATTERY_CHANGED) {
                val level = intent.getIntExtra("level", 0)
                if (mPageLoader != null) {
                    mPageLoader!!.updateBattery(level)
                }
            } else if (intent.action == Intent.ACTION_TIME_TICK) {
                if (mPageLoader != null) {
                    mPageLoader!!.updateTime()
                }
            }
        }
    }

    // 亮度调节监听
    // 由于亮度调节没有 Broadcast 而是直接修改 ContentProvider 的。所以需要创建一个 Observer 来监听 ContentProvider 的变化情况。
    private val mBrightObserver: ContentObserver = object : ContentObserver(Handler()) {
        override fun onChange(selfChange: Boolean) {
            onChange(selfChange, null)
        }

        override fun onChange(selfChange: Boolean, uri: Uri?) {
            super.onChange(selfChange)

            // 判断当前是否跟随屏幕亮度，如果不是则返回
            if (selfChange || !mSettingDialog!!.isBrightFollowSystem) return

            // 如果系统亮度改变，则修改当前 Activity 亮度
            if (BRIGHTNESS_MODE_URI == uri) {
                LoggerUtils.d("亮度模式改变")
            } else if (BRIGHTNESS_URI == uri && !BrightnessUtils.isAutoBrightness(this@BookReadActivity)) {
                LoggerUtils.d("亮度模式为手动模式 值改变")
                BrightnessUtils.setBrightness(
                    this@BookReadActivity,
                    BrightnessUtils.getScreenBrightness(this@BookReadActivity)
                )
            } else if (BRIGHTNESS_ADJ_URI == uri && BrightnessUtils.isAutoBrightness(this@BookReadActivity)) {
                LoggerUtils.d("亮度模式为自动模式 值改变")
                BrightnessUtils.setDefaultBrightness(this@BookReadActivity)
            } else {
                LoggerUtils.d("亮度调整 其他")
            }
        }
    }

    /**
     * 注册亮度观察者
     */
    private fun registerBrightObserver() {
        try {
            if (mBrightObserver != null) {
                if (!isRegistered) {
                    val cr = contentResolver
                    cr.unregisterContentObserver(mBrightObserver)
                    cr.registerContentObserver(BRIGHTNESS_MODE_URI, false, mBrightObserver)
                    cr.registerContentObserver(BRIGHTNESS_URI, false, mBrightObserver)
                    cr.registerContentObserver(BRIGHTNESS_ADJ_URI, false, mBrightObserver)
                    isRegistered = true
                }
            }
        } catch (throwable: Throwable) {
            LogUtils.e("register mBrightObserver error! $throwable")
        }
    }

    /**
     * 解注册
     */
    private fun unregisterBrightObserver() {
        try {
            if (mBrightObserver != null) {
                if (isRegistered) {
                    contentResolver.unregisterContentObserver(mBrightObserver)
                    isRegistered = false
                }
            }
        } catch (throwable: Throwable) {
            LogUtils.e("unregister BrightnessObserver error! $throwable")
        }
    }

    @SuppressLint("WrongConstant")
    override fun onBackPressed() {
        if (read_abl_top_menu.visibility == View.VISIBLE) {
            // 非全屏下才收缩，全屏下直接退出
            if (!ReadSettingManager.getInstance().isFullScreen) {
                toggleMenu(true)
                return
            }
        } else if (mSettingDialog!!.isShowing) {
            mSettingDialog!!.dismiss()
            return
        } else if (read_dl_slide!!.isDrawerOpen(Gravity.START)) {
            read_dl_slide!!.closeDrawer(Gravity.START)
            return
        }
        if (!mCollBook.isLocal() && !isCollected
            && mCollBook.bookChapters != null && !mCollBook.bookChapters.isEmpty()
        ) {
            val alertDialog = AlertDialog.Builder(this)
                .setTitle(R.string.nb_file_add_shelf)
                .setMessage(R.string.nb_read_add_book_hint)
                .setPositiveButton(R.string.sure) { dialog, which -> //设置为已收藏
                    isCollected = true
                    //设置阅读时间
                    mCollBook.lastRead = TimeUtils.formatDateToStr(
                        System.currentTimeMillis(),
                        TimeUtils.DATE_FORMAT_7
                    )
                    DaoFactory.getCollectBookDao()
                        .saveCollBookWithAsync(mCollBook)
                    RxBus.getInstance().post(true) //刷新书架
                    exit()
                }
                .setNegativeButton(R.string.cancel) { dialog, which -> exit() }.create()
            alertDialog.show()
        } else {
            exit()
        }
    }

    // 退出
    private fun exit() {
        //从详情页进入阅读，再返回给详情页,回调给详情页的onActivityResult
        val result = Intent()
        result.putExtra(BookDetailActivity.Companion.RESULT_IS_COLLECTED, isCollected)
        setResult(RESULT_OK, result)
        // 退出
        super.onBackPressed()
    }

    override fun onStart() {
        super.onStart()
        registerBrightObserver()
    }

    override fun onResume() {
        super.onResume()
        mWakeLock!!.acquire() //保持唤醒
        registerBatteryReceiver()
    }

    /**
     * 注册电池广播
     * activity的生命周期方法基本上是成对出现的，例如onCreate对应onDestory，onStart对应onStop，onResume对于onPause
     */
    private fun registerBatteryReceiver() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED)
        intentFilter.addAction(Intent.ACTION_TIME_TICK)
        registerReceiver(mReceiver, intentFilter)
    }

    private fun unregisterBatteryReceiver() {
        mReceiver?.let { unregisterReceiver(it) }
    }

    override fun onPause() {
        super.onPause()
        mWakeLock!!.release() //释放掉该锁
        if (isCollected) {
            mPageLoader!!.saveRecord()
        }
        unregisterBatteryReceiver()
    }

    override fun onStop() {
        super.onStop()
        unregisterBrightObserver()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mHandler != null) {
            mHandler.removeMessages(WHAT_CATEGORY)
            mHandler.removeMessages(WHAT_CHAPTER)
            mHandler.removeCallbacksAndMessages(null)
        }
        mPageLoader!!.closeBook()
        mPageLoader = null
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        val isVolumeTurnPage = ReadSettingManager
            .getInstance().isVolumeTurnPage
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> if (isVolumeTurnPage) {
                return mPageLoader!!.skipToPrePage()
            }
            KeyEvent.KEYCODE_VOLUME_DOWN -> if (isVolumeTurnPage) {
                return mPageLoader!!.skipToNextPage()
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        SystemBarUtils.hideStableStatusBar(this)
        if (requestCode == REQUEST_MORE_SETTING) {
            val fullScreen = ReadSettingManager.getInstance().isFullScreen
            if (isFullScreen != fullScreen) {
                isFullScreen = fullScreen
                // 刷新BottomMenu
                initBottomMenu()
            }

            // 设置显示状态
            if (isFullScreen) {
                SystemBarUtils.hideStableNavBar(this) //隐藏虚拟导航栏
            } else {
                SystemBarUtils.showStableNavBar(this) //显示虚拟导航栏
            }
            val tempConvertType = ReadSettingManager.getInstance().convertType
            LogUtils.i("currentConvertType:$convertType,tempConvertType:$tempConvertType")
            if (convertType != tempConvertType && tempConvertType != 0) {
                //修复语言改变时不能刷新章节语言问题
                val chapterPos = mPageLoader!!.chapterPos
                Observable.just(chapterPos).map(object : Function<Int, Boolean> {
                    @Throws(Exception::class)
                    override fun apply(integer: Int): Boolean {
                        return mPageLoader!!.refreshChapter(chapterPos)
                    }
                }).compose(RxSchedulers.applySchedulers()).`as`(bindLifecycle()).subscribe(
                    { ret ->
                        if (ret) {
                            mPageLoader!!.skipToChapter(chapterPos)
                            convertType = tempConvertType
                        }
                    }) { throwable -> LogUtils.e("语言转换异常", throwable) }
            }
        }
    }

    companion object {
        const val REQUEST_MORE_SETTING = 1
        const val EXTRA_COLL_BOOK = "extra_coll_book"
        const val EXTRA_IS_COLLECTED = "extra_is_collected"
        private const val WHAT_CATEGORY = 1
        private const val WHAT_CHAPTER = 2

        /**
         * @param context
         * @param collBook
         * @param isCollected true表示来自书架
         */
        fun startActivity(context: Context, collBook: CollectBook?, isCollected: Boolean) {
            context.startActivity(
                Intent(context, BookReadActivity::class.java)
                    .putExtra(EXTRA_IS_COLLECTED, isCollected)
                    .putExtra(EXTRA_COLL_BOOK, collBook)
            )
        }
    }
}