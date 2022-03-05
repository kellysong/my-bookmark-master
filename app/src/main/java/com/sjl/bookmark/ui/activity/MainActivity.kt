package com.sjl.bookmark.ui.activity

import android.content.*
import android.content.res.Configuration
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Environment
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager.widget.ViewPager
import cn.feng.skin.manager.loader.SkinManager
import cn.feng.skin.manager.statusbar.StatusBarUtil
import cn.feng.skin.manager.util.ObjectUtils
import com.google.android.material.navigation.NavigationView
import com.orhanobut.logger.Logger
import com.renny.zxing.Activity.CaptureActivity
import com.sjl.bookmark.R
import com.sjl.bookmark.api.WanAndroidApiService
import com.sjl.bookmark.app.AppConstant
import com.sjl.bookmark.dao.impl.CollectDaoImpl
import com.sjl.bookmark.dao.util.BookmarkParse
import com.sjl.bookmark.dao.util.BrowseMapper
import com.sjl.bookmark.entity.DataResponse
import com.sjl.bookmark.entity.UserInfo
import com.sjl.bookmark.entity.UserLogin
import com.sjl.bookmark.ui.adapter.MainViewPagerAdapter
import com.sjl.bookmark.ui.fragment.CategoryFragment
import com.sjl.bookmark.ui.fragment.HomeFragment
import com.sjl.bookmark.ui.fragment.ToolFragment
import com.sjl.bookmark.util.NavigationViewHelper
import com.sjl.bookmark.util.NotificationUtils
import com.sjl.bookmark.util.WebViewPool
import com.sjl.bookmark.widget.WaveView
import com.sjl.core.entity.EventBusDto
import com.sjl.core.mvp.BaseActivity
import com.sjl.core.mvp.BaseFragment
import com.sjl.core.mvp.NoPresenter
import com.sjl.core.net.RetrofitHelper
import com.sjl.core.net.RxBus
import com.sjl.core.net.RxObserver
import com.sjl.core.net.RxSchedulers
import com.sjl.core.util.AppUtils
import com.sjl.core.util.PreferencesHelper
import com.sjl.core.util.SerializeUtils
import com.sjl.core.util.datetime.TimeUtils
import com.sjl.core.util.log.LogUtils
import com.sjl.core.widget.imageview.CircleImageView
import com.uber.autodispose.AutoDispose
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.*
import java.lang.reflect.Proxy
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : BaseActivity<NoPresenter>(),
    NavigationView.OnNavigationItemSelectedListener {

    private var mNavHeader: LinearLayout? = null
    private lateinit var sharedPreferences: SharedPreferences
    private var circleImageView: CircleImageView? = null
    private var tvNickname: TextView? = null
    private var tvPersonality: TextView? = null
    private lateinit var mFragments: MutableList<BaseFragment<*>>
    private var mLastFgIndex = 0
    private var searchMenuItem: MenuItem? = null
    private val mToggle: ActionBarDrawerToggle? = null
    private var DOUBLE_CLICK_TIME: Long = 0
    private var notificationUtils: NotificationUtils? = null

    /**
     * 更换语言标志
     */
    var executeChangeLanguage = false
    override fun getLayoutId(): Int {
        setMourningDaysTheme()
        return R.layout.activity_main
    }

    /**
     * 主页设置公祭日和悼念日主题
     */
    private fun setMourningDaysTheme() {
        val currentDate = TimeUtils.formatDateToStr(Date(), "MM-dd")
        //        MOURNING_DAYS.add(currentDate);//测试
        for (day in MOURNING_DAYS) {
            if (day == currentDate) {
                val view = window.decorView
                val paint = Paint()
                val cm = ColorMatrix()
                cm.setSaturation(0f)
                paint.colorFilter = ColorMatrixColorFilter(cm)
                view.setLayerType(View.LAYER_TYPE_HARDWARE, paint)
                break
            }
        }
    }

    override fun initView() {
        common_toolbar.title = ""
        setSupportActionBar(common_toolbar)
        //        NavigationViewHelper.disableShiftMode(mNavigation);
        //下面控制菜单显示
        initStatusColor()

//        mToggle = new ActionBarDrawerToggle(
//                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        mDrawer.setDrawerListener(mToggle);
//        mToggle.syncState();


        //headerLayout id获取比较特殊
        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        NavigationViewHelper.disableNavigationViewScrollbars(navigationView)
        navigationView.setNavigationItemSelectedListener(this)
        navigationView.itemIconTintList = null //使菜单项图标显示图标原始颜色
        val headerView = navigationView.getHeaderView(0)
        mNavHeader = headerView.findViewById<View>(R.id.ll_nav_header) as LinearLayout
        val waveView = headerView.findViewById<View>(R.id.wave_view) as WaveView
        circleImageView = headerView.findViewById<View>(R.id.iv_head_img) as CircleImageView
        tvNickname = headerView.findViewById<View>(R.id.tv_name) as TextView
        tvPersonality = headerView.findViewById<View>(R.id.tv_personality) as TextView
        initWaveView(waveView)

        //switchFragment(0);
        //注意：高版本肯能无效或报错
//        hookToast();
    }

    private fun initStatusColor() {
        var color = SkinManager.getInstance().colorPrimary
        color =
            if (color != -1) color else resources.getColor(R.color.colorPrimary) //必须指定定一个颜色值，不然更换主题时出现两个StatusBar
        StatusBarUtil.setColorNoTranslucentForDrawerLayout(this, drawer_layout, color)
    }

    override fun changeStatusBarColor() {
        val color = SkinManager.getInstance().colorPrimary
        if (color != -1 && drawer_layout != null) { //不能少，否则状态栏无效
            StatusBarUtil.setColorNoTranslucentForDrawerLayout(this, drawer_layout, color)
        }
    }

    private fun initWaveView(waveView: WaveView) {
        //设置头像跟着波浪背景浮动
        val lp = circleImageView!!.layoutParams as LinearLayout.LayoutParams
        waveView.setOnWaveAnimationListener { y ->
            lp.setMargins(0, 0, 0, y.toInt() + 2)
            circleImageView!!.layoutParams = lp
        }
    }

    /**
     * 初始化fragment
     */
    private fun initFragment() {
        mFragments = ArrayList()
        /*       mFragments.add(new HomeFragment());
        mFragments.add(new CategoryFragment());
        mFragments.add(new ToolFragment());*/
        var position = 0
        mFragments.add(instantiateFragment(vp_main, position, HomeFragment()))
        position++
        mFragments.add(instantiateFragment(vp_main, position, CategoryFragment()))
        position++
        mFragments.add(instantiateFragment(vp_main, position, ToolFragment()))
        //为viewpager设置adapter
        vp_main.offscreenPageLimit = 2
        vp_main.adapter = MainViewPagerAdapter(supportFragmentManager, mFragments)
        mLastFgIndex = 0
        switchFragmentNew(mLastFgIndex)
    }

    private fun instantiateFragment(
        viewPager: ViewPager,
        position: Int,
        defaultResult: BaseFragment<*>
    ): BaseFragment<*> {
        val tag = "android:switcher:" + viewPager.id + ":" + position
        val fragment = supportFragmentManager.findFragmentByTag(tag)
        return if (fragment == null) defaultResult else (fragment as BaseFragment<*>)
    }

    /**
     * 切换fragment
     *
     * @param position 要显示的fragment的下标
     */
    private fun switchFragment(position: Int) {
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

    override fun initListener() {
        //设置mToolBarIcon监听
        tool_bar_icon.setOnClickListener { drawer_layout!!.openDrawer(GravityCompat.START) }
        circleImageView?.setOnClickListener { openActivity(PersonCenterActivity::class.java) }
        /**
         * 点击底部tab切换fragment
         */
        bnv_navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    //                        mToggle.setDrawerIndicatorEnabled(true);//隐藏侧滑菜单按钮
                    if (searchMenuItem != null) {
                        searchMenuItem!!.isVisible = true
                    }
                    setToolbarTitle(getString(R.string.bottom_tab_home), true)
                    switchFragmentNew(0)
                }
                R.id.nav_category -> {
                    //                        mToggle.setDrawerIndicatorEnabled(false);
                    if (searchMenuItem != null) {
                        searchMenuItem!!.isVisible = false
                    }
                    setToolbarTitle(getString(R.string.bottom_tab_category), false)
                    switchFragmentNew(1)
                }
                R.id.nav_tool -> {
                    //                        mToggle.setDrawerIndicatorEnabled(false);
                    if (searchMenuItem != null) {
                        searchMenuItem!!.isVisible = false
                    }
                    setToolbarTitle(getString(R.string.bottom_tab_tool), false)
                    switchFragmentNew(2)
                }
                else -> {}
            }
            true
        }
        vp_main.addOnPageChangeListener(pageChangeListener)

    }

    var pageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageSelected(position: Int) {
            //ViewPager和BottomNaviationView联动,当ViewPager的某个页面被选中了,同时设置BottomNaviationView对应的tab按钮被选中
            when (position) {
                0 -> bnv_navigation!!.selectedItemId = R.id.nav_home
                1 -> bnv_navigation!!.selectedItemId = R.id.nav_category
                2 -> bnv_navigation!!.selectedItemId = R.id.nav_tool
                else -> {}
            }
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }

    /**
     * 设置定义toolbar标题和图标
     *
     * @param title
     * @param iconShow
     */
    private fun setToolbarTitle(title: String, iconShow: Boolean) {
        tool_bar_title.text = title
        if (iconShow) {
            tool_bar_icon.visibility = View.VISIBLE
        } else {
            tool_bar_icon.visibility = View.GONE
        }
    }

    private fun switchFragmentNew(position: Int) {
        vp_main.setCurrentItem(position, false)
    }

    override fun initData() {
        initFragment()
        loadBookmark()
        initHeadImg()
        registerUpdateHeadImg()
        //限时大促
//        runnable.run();
        autoBackupCollection()
        //        CrashReport.testJavaCrash();
        autoLoginWanAndroid()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            LogUtils.i("SUPPORTED ABI:" + Arrays.toString(Build.SUPPORTED_ABIS));
        }
    }

    /**
     * 自动登录WanAndroid，获取积分
     */
    private fun autoLoginWanAndroid() {
        val preferencesHelper = PreferencesHelper.getInstance(mContext)
        val date = preferencesHelper[AppConstant.SETTING.LOGIN_DATE, ""] as String
        val currentDate = TimeUtils.formatDateToStr(Date(), TimeUtils.DATE_FORMAT_4)
        if (currentDate == date) {
            return
        }
        RetrofitHelper.getInstance().getApiService(WanAndroidApiService::class.java)
            .login("songjiali", "songjiali")
            .compose(RxSchedulers.applySchedulers()).`as`(bindLifecycle())
            .subscribe(object : RxObserver<DataResponse<UserLogin>>() {
                override fun _onNext(userLoginDataResponse: DataResponse<UserLogin>) {
                    if (userLoginDataResponse.errorCode == 0) {
                        LogUtils.i("登录成功")
                        preferencesHelper.put(AppConstant.SETTING.LOGIN_DATE, currentDate)
                    }
                }

                override fun _onError(msg: String) {}
                override fun _onComplete() {}
            })
    }

    /**
     * 自动备份
     */
    private fun autoBackupCollection() {
        val preferencesHelper = PreferencesHelper.getInstance(mContext)
        val isAutoBackup =
            preferencesHelper[AppConstant.SETTING.AUTO_BACKUP_COLLECTION, true] as Boolean
        if (isAutoBackup) {
            val time =
                preferencesHelper[AppConstant.SETTING.AUTO_BACKUP_COLLECTION_TIME, -1L] as Long
            val dateDiff = TimeUtils.dateDiff(Date(time), Date())
            if (time == -1L || dateDiff > 2) { //两天备份一次
                val collectService = CollectDaoImpl(mContext)
                val collection = collectService.findAllCollection()
                if (AppUtils.isEmpty(collection)) {
                    return
                }
                //开始序列化数据
                Observable.just(collection)
                    .map(object :
                        Function<List<com.sjl.bookmark.entity.table.Collection>, Boolean> {

                        @Throws(Exception::class)
                        override fun apply(collections: List<com.sjl.bookmark.entity.table.Collection>): Boolean {
                            return SerializeUtils.serialize("collection", collections)
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()) //                    .as(MainActivity.this.<Boolean>bindLifecycle())//不知道为啥转换失败
                    .`as`(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                    .subscribe(object : Observer<Boolean> {

                        override fun onSubscribe(d: Disposable) {}
                        override fun onNext(ret: Boolean) {
                            notificationUtils = NotificationUtils(baseContext)
                            var msg = ""
                            msg = if (ret) {
                                "本次备份我的收藏共" + collection.size + "条"
                            } else {
                                "备份我的收藏失败"
                            }
                            notificationUtils!!.sendNotification(
                                1,
                                getString(R.string.auto_backup),
                                msg,
                                null,
                                null
                            )
                            preferencesHelper.put(
                                AppConstant.SETTING.AUTO_BACKUP_COLLECTION_TIME,
                                System.currentTimeMillis()
                            )
                        }

                        override fun onError(e: Throwable) {
                            LogUtils.e("备份收藏异常", e)
                        }

                        override fun onComplete() {
                            LogUtils.i("完成备份收藏")
                        }
                    })
            }
        }
    }

    var handler = Handler()
    var runnable: Runnable = object : Runnable {
        override fun run() {
            val time = TimeUtils.getTimeDifference(Date(), "2018-04-15 12:00:00", 0)
            if ("-1" == time) {
                LogUtils.i("已经发完，欢迎下月再来")
            } else {
                LogUtils.i("离发工资还剩：$time")
            }
            handler.postDelayed(this, 1000)
        }
    }

    private fun initHeadImg() {
        Observable.create<Any> { emitter ->
            val temp = File(AppConstant.USER_HEAD_PATH + File.separator + "head_crop.jpg")
            if (temp.exists()) {
                val fis = FileInputStream(temp)
                val bitmap = BitmapFactory.decodeStream(fis) ///把流转化为Bitmap图片
                emitter.onNext(bitmap)
            }
            val userInfo = SerializeUtils.deserialize<UserInfo>("userInfo", UserInfo::class.java)
            if (userInfo != null) {
                emitter.onNext(userInfo)
            }
        }.compose(RxSchedulers.applySchedulers())
            .`as`(bindLifecycle())
            .subscribe({ o ->
                if (o is Bitmap) {
                    val bitmap = o
                    circleImageView!!.setImageBitmap(bitmap)
                    tool_bar_icon!!.setImageBitmap(bitmap)
                }
                if (o is UserInfo) {
                    val userInfo = o
                    tvNickname!!.text = userInfo.name
                    tvPersonality!!.text = userInfo.personality
                }
            }) { throwable -> LogUtils.e("初始化头像失败", throwable) }
    }

    /**
     * 监听头像更新
     */
    fun registerUpdateHeadImg() {
        RxBus.getInstance()
            .toObservable(AppConstant.RxBusFlag.FLAG_2, EventBusDto::class.java)
            .compose(RxSchedulers.applySchedulers())
            .`as`(bindLifecycle())
            .subscribe({ s ->
                LogUtils.i("接收到头像更新：$s")
                if (s.eventCode == 0) {
                    initHeadImg()
                }
            }) { }
    }

    private fun loadBookmark() {
        val fileName = "bookmarks/bookmarks_2021_12_12.html"
        sharedPreferences = getSharedPreferences("bookmark", MODE_PRIVATE)
        val flag = sharedPreferences.getBoolean("readFlag", false)
        val oldFileName = sharedPreferences.getString("fileName", "")
        if (!flag) {
            initBookmarkData(fileName)
        } else {
            if (!ObjectUtils.isEquals(fileName, oldFileName)) { //书签文本发生改变，重新解析
                initBookmarkData(fileName)
            } else {
                LogUtils.i("已经读取过书签")
            }
        }
    }

    private fun initBookmarkData(fileName: String) {
        Thread {
            try {
                val start = System.currentTimeMillis()
                val bookmarkUtils = BookmarkParse()
                bookmarkUtils.readBookmarkHtml(this@MainActivity, fileName)
                val editor = sharedPreferences.edit()
                editor.putBoolean("readFlag", true)
                editor.putString("fileName,", fileName)
                editor.apply()
                val end = System.currentTimeMillis()
                LogUtils.i("读取书签文件耗时：" + (end - start) / 1000.0 + "s")
            } catch (e: IOException) {
                Logger.e(e, "读取书签文件异常")
            }
        }.start()
    }

    /**
     * onCreate之后执行
     * 每次在display Menu之前，都会去调用，只要按一次Menu按鍵，就会调用一次。所以可以在这里动态的改变menu。
     *
     * @param menu
     * @return
     */
    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        searchMenuItem = menu.getItem(0)
        if (mLastFgIndex != 0) {
            searchMenuItem?.isVisible = false
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify item_bookmark_title parent activity in AndroidManifest.xml.
        val id = item.itemId
        if (id == R.id.action_search) {
            startActivity(Intent(this, ArticleSearchActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId
        if (id == R.id.nav_business_card) {
            startActivity(Intent(this, MyCardActivity::class.java))
        } else if (id == R.id.nav_my_collection) { //我的收藏
            startActivity(Intent(this, MyCollectionActivity::class.java))
        } else if (id == R.id.nav_bookmark) {
            startActivity(Intent(this, BookmarkActivity::class.java))
        } else if (id == R.id.nav_skin) {
            startActivity(Intent(this, ChangeSkinActivity::class.java))
        } else if (id == R.id.nav_scan) {
            val intent = Intent(this, CaptureActivity::class.java)
            intent.putExtra("scan_title", getString(R.string.scan))
            startActivityForResult(intent, REQUEST_SCAN)
        } else if (id == R.id.nav_settings) { //设置
            startActivity(Intent(this, SettingActivity::class.java))
            //            String path = AppConstant.ROOT_PATH + "sample.pdf";
//            DocBrowserActivity.show(this,path);
        }
        //防止卡顿
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
        return true
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LogUtils.i("==========================================语言切换了")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        LogUtils.i("requestCode=$requestCode,resultCode=$resultCode")
        when (requestCode) {
            300 -> if (data != null) {
                val extras = data.extras
                val head = extras.getParcelable<Bitmap>("data")
                if (head != null) {
                    /**
                     * 上传服务器代码
                     */
                    saveHeadPic(head) // 保存在SD卡中
                    circleImageView!!.setImageBitmap(head) // 用ImageView显示出来
                    head.recycle()
                }
            }
            REQUEST_SCAN -> if (resultCode == RESULT_OK) {
                val barCode = data!!.getStringExtra("barCode")
                if (!TextUtils.isEmpty(barCode)) {
                    if (barCode.startsWith("http")) {
                        scanResult(barCode, DialogInterface.OnClickListener { dialog, which ->
                            dialog.dismiss()
                            val intent = Intent(this@MainActivity, BrowserActivity::class.java)
                            intent.putExtra(BrowserActivity.Companion.WEBVIEW_URL, barCode)
                            startActivity(intent)
                        }, DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                    } else {
                        scanResult(barCode, null, null)
                    }
                }
            }
            else -> {}
        }
    }

    private fun scanResult(
        text: String,
        okListener: DialogInterface.OnClickListener?,
        cancelListener: DialogInterface.OnClickListener?
    ) {
        val builder = AlertDialog.Builder(this)
            .setTitle(R.string.nb_common_tip)
            .setMessage(text)
        builder.setPositiveButton(R.string.sure, okListener)
        if (cancelListener != null) {
            builder.setPositiveButton(R.string.cancel, cancelListener)
        }
        builder.show()
    }

    /**
     * 调用系统的裁剪功能,个别机型有bug,小米
     *
     * @param uri
     */
    @Deprecated("")
    fun cropPhoto(uri: Uri?) {
        try {
            val intent = Intent("com.android.camera.action.CROP")
            if (Build.VERSION.SDK_INT >= VERSION_CODES.N) {
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION //授权标志
            }
            intent.setDataAndType(uri, "image/*")
            intent.putExtra("crop", "true")
            // aspectX aspectY 是裁剪框宽高的比例
            intent.putExtra("aspectX", 1)
            intent.putExtra("aspectY", 1)
            // outputX outputY 是裁剪图片宽高
            intent.putExtra("outputX", 200)
            intent.putExtra("outputY", 200)
            intent.putExtra("return-data", true)
            startActivityForResult(intent, 300)
        } catch (e: Exception) {
            LogUtils.e("调用系统的裁剪功能异常", e)
        }
    }

    @Deprecated("")
    private fun saveHeadPic(mBitmap: Bitmap) {
        val sdStatus = Environment.getExternalStorageState()
        if (sdStatus != Environment.MEDIA_MOUNTED) { // 检测sd是否可用
            return
        }
        var b: FileOutputStream? = null
        val file = File(AppConstant.USER_HEAD_PATH)
        if (!file.exists()) {
            file.mkdirs() // 创建文件夹
        }
        val fileName = file.toString() + File.separator + "head.jpg" // 图片名字
        try {
            b = FileOutputStream(fileName)
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, b) // 把数据写入文件
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } finally {
            try {
                // 关闭流
                b!!.flush()
                b.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mFragments.clear()
        vp_main.removeOnPageChangeListener(pageChangeListener)
        if (!executeChangeLanguage) {
            BrowseMapper.clearAll()
            WebViewPool.destroyPool()
            killAll()
            System.exit(0) //退出虚拟机
        }
    }

    override fun onBackPressed() {
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            if (System.currentTimeMillis() - DOUBLE_CLICK_TIME > 2000) {
                DOUBLE_CLICK_TIME = System.currentTimeMillis()
                Toast.makeText(this, R.string.exit_app_hint, Toast.LENGTH_SHORT).show()
            } else {
                mFragments.clear()
                super.onBackPressed()
            }
        }
    }

    /**
     * 基于hook去除 Toast前面的应用名,https://mp.weixin.qq.com/s/B3ooLGa-uQK4pf9RrZvY5g
     */
    private fun hookToast() {
        try {
            val toastClass = Toast::class.java
            //获取sService的Field
            val sServiceField = toastClass.getDeclaredField("sService")
            sServiceField.isAccessible = true
            //获取sService原始对象
            val getServiceMethod = toastClass.getDeclaredMethod("getService", *arrayOfNulls(0))
            getServiceMethod.isAccessible = true
            val service = getServiceMethod.invoke(null)
            //动态代理替换
            val aClass = Class.forName("android.app.INotificationManager")
            val proxy = Proxy.newProxyInstance(
                Thread::class.java.classLoader,
                arrayOf(aClass)
            ) { proxy, method, args -> // 判断enqueueToast()方法时执行操作
                if (method.name == "enqueueToast") {
                    Log.e("hook", method.name)
                    try {
                        getContent(args[1])
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                method.invoke(service, *args)
            }
            // 用代理对象给sService赋值
            sServiceField[null] = proxy
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val REQUEST_SCAN = 0
        private val MOURNING_DAYS: List<String> =
            ArrayList(Arrays.asList("04-04", "05-12", "09-03", "12-13"))

        @Throws(
            ClassNotFoundException::class,
            NoSuchFieldException::class,
            IllegalAccessException::class
        )
        private fun getContent(arg: Any) {
            // 获取TN的class
            val tnClass = Class.forName(Toast::class.java.name + "\$TN")
            // 获取mNextView的Field
            val mNextViewField = tnClass.getDeclaredField("mNextView")
            mNextViewField.isAccessible = true
            // 获取mNextView实例
            val mNextView = mNextViewField[arg] as LinearLayout
            // 获取textview
            val childView = mNextView.getChildAt(0) as TextView
            // 获取文本内容
            val text = childView.text
            //        LogUtils.i("hook toast content before: " + text);
            // 替换文本并赋值
            childView.text = text.toString().split("：").toTypedArray()[1]
            //        LogUtils.i("hook toast content after: " + childView.getText());
        }
    }
}