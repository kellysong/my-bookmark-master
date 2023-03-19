package com.sjl.bookmark.ui.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Html
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.webkit.JavascriptInterface
import android.widget.FrameLayout
import android.widget.ProgressBar
import com.sjl.bookmark.R
import com.sjl.bookmark.dao.util.CollectUtils
import com.sjl.bookmark.util.WebViewPool
import com.sjl.bookmark.widget.WebViewJavaScriptFunction
import com.sjl.bookmark.widget.X5WebView
import com.sjl.core.mvp.BaseActivity
import com.sjl.core.mvp.NoPresenter
import com.sjl.core.util.SnackbarUtils
import com.sjl.core.util.log.LogUtils
import com.tencent.smtt.sdk.*
import kotlinx.android.synthetic.main.activity_browser.*
import java.util.*
import kotlin.collections.ArrayList


/**
 * 集成腾讯x5内核webview add by Kelly on 20170209
 */
class BrowserActivity : BaseActivity<NoPresenter>() {
    /**
     * 作为一个浏览器的示例展示出来，采用android+web的模式
     */
    private lateinit var mWebView: X5WebView
    private lateinit var mProgressBar: ProgressBar

    /**
     * 用于展示在web端<input type=text></input>的标签被选择之后，文件选择器的制作和生成
     */
    private var uploadFile: ValueCallback<Uri?>? = null
    private var uploadFiles: ValueCallback<Array<Uri>>? = null

    /**
     * setDisplayShowTitleEnabled(boolean showTitle)方法：设置是否显示标题
     *
     *
     * setDisplayUseLogoEnabled(boolean useLogo)方法：设置是否显示logo
     *
     *
     * setDisplayShowHomeEnabled(boolean showHome)方法：设置是否显示返回
     */
    private var url: String? = null
    private var title: String? = null
    private var savedInstanceState: Bundle? = null
    private var isAnimStart = false
    private var currentProgress = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.savedInstanceState = savedInstanceState
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_browser
    }

    /*  @Override
    protected void changeStatusBarColor() {
        setColorForSwipeBack();
    }
*/
    override fun initView() {
        setSupportActionBar(common_toolbar)
        //关键下面两句话，设置了回退按钮，及点击事件的效果
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        common_toolbar.setNavigationOnClickListener { finish() }

//        mWebView = new X5WebView(this, null);
        mWebView = WebViewPool.getInstance().webView
        if (savedInstanceState != null) {
            LogUtils.i("webview状态不为空，正在恢复")
            mWebView.restoreState(savedInstanceState)
        } else {
            val intent = intent
            url = intent.getStringExtra(WEBVIEW_URL)
            title = intent.getStringExtra(WEBVIEW_TITLE)
            initWebView()
        }
    }

    override fun initListener() {}
    override fun initData() {}
    private var clear = false

    /**
     * activity意外被杀
     *
     * @param outState
     * @param outPersistentState
     */
    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        LogUtils.i("触发保存webview状态")
        if (mWebView != null) {
            mWebView.saveState(outState)
        }
    }

    private fun initWebView() {
        clear = true
        //        mWebView = new X5WebView(this, null);
        mProgressBar = mWebView.progressBar
        fl_webView.addView(
            mWebView, FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
        mWebView.webViewClient = object : WebViewClient() {
            /*  @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setAlpha(1.0f);
            }*/
            override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
                return check302(url)
            }

            /**
             * 防止加载网页时调起系统浏览器
             */
            /*
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                super.shouldOverrideUrlLoading(view, url);//表示开发者自己不处理，交给系统处理
                return true;//表示自己处理，不需要系统处理，比如如果是true，重定向就不会跳转
            }*/
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                if (!isActivityExist(BrowserActivity::class.java)) { //防止快速打开和关闭webView时发生控件空指针
                    return
                }
                if (common_toolbar != null) {
                    common_toolbar!!.title = view.title
                }
                //                addImageClickListener(view);//待网页加载完全后设置图片点击的监听方法
            }

            override fun doUpdateVisitedHistory(webView: WebView, s: String, b: Boolean) {
                if (clear) {
                    webView.clearHistory()
                    clear = false
                }
            }
        }
        mWebView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {

                /* if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    if (View.GONE == mProgressBar.getVisibility()) {
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                    mProgressBar.setProgress(newProgress);
                }*/
                if (null == mProgressBar) return
                currentProgress = mProgressBar.progress
                if (newProgress >= 100 && !isAnimStart) {
                    // 防止调用多次动画
                    isAnimStart = true
                    mProgressBar.progress = newProgress
                    // 开启属性动画让进度条平滑消失
                    startDismissAnimation(mProgressBar.progress)
                } else {
                    if (View.GONE == mProgressBar.visibility) {
                        isAnimStart = false
                        mProgressBar.visibility = View.VISIBLE
                        mProgressBar.alpha = 1.0f
                    }
                    // 开启属性动画让进度条平滑递增
                    startProgressAnimation(newProgress)
                }
            }

            // For Android 3.0+
            fun openFileChooser(uploadMsg: ValueCallback<Uri?>?, acceptType: String?) {
                Log.i("test", "openFileChooser 1")
                uploadFile = uploadFile
                openFileChooseProcess()
            }

            // For Android < 3.0
            fun openFileChooser(uploadMsgs: ValueCallback<Uri?>?) {
                Log.i("test", "openFileChooser 2")
                uploadFile = uploadFile
                openFileChooseProcess()
            }

            // For Android  > 4.1.1
            override fun openFileChooser(
                uploadMsg: ValueCallback<Uri>,
                acceptType: String,
                capture: String
            ) {
                Log.i("test", "openFileChooser 3")
                uploadFile = uploadFile
                openFileChooseProcess()
            }

            // For Android  >= 5.0
            override fun onShowFileChooser(
                webView: WebView,
                filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                Log.i("test", "openFileChooser 4:$filePathCallback")
                uploadFiles = filePathCallback
                openFileChooseProcess()
                return true
            }
        }
        mWebView.setDownloadListener { url, arg1, arg2, arg3, arg4 ->
            try {
                val uri = Uri.parse(url)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                this@BrowserActivity.startActivity(intent)
            } catch (e: Exception) {
                LogUtils.e("调用系统浏览器下载异常", e)
            }
        }
        mWebView.addJavascriptInterface(object : WebViewJavaScriptFunction {
            override fun onJsFunctionCalled(tag: String) {
                LogUtils.i("WebViewJavaScriptFunction,tag:$tag")
            }

            @JavascriptInterface
            fun onX5ButtonClicked() {
                enableX5FullscreenFunc() //开启X5全屏播放模式
            }

            @JavascriptInterface
            fun onCustomButtonClicked() {
                disableX5FullscreenFunc() //恢复webkit初始状态
            }

            @JavascriptInterface
            fun onLiteWndButtonClicked() { //开启小窗模式
                enableLiteWndFunc()
            }

            @JavascriptInterface
            fun onPageVideoClicked() { //页面内全屏播放模式
                enablePageVideoFunc()
            }
        }, "Android")
        if (url == null) {
            finish()
            return
        } else {
            mWebView.loadUrl(url)
        }
        CookieSyncManager.createInstance(this)
        CookieSyncManager.getInstance().sync()

        //去除QQ浏览器推广
        window.decorView.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            val outView = ArrayList<View>()
            window.decorView.findViewsWithText(outView, "QQ浏览器", View.FIND_VIEWS_WITH_TEXT)
            if (outView != null && outView.size > 0) {
                outView[0].visibility = View.GONE
            }
        }
    }

    private fun check302(webUrl: String?): Boolean {
        for (url in URLS_302) {
            if (webUrl != null && webUrl.startsWith(url)) {
                return true
            }
        }
        return false
    }

    /**
     * progressBar递增动画
     */
    private fun startProgressAnimation(newProgress: Int) {
        val animator = ObjectAnimator.ofInt(mProgressBar, "progress", currentProgress, newProgress)
        animator.duration = 300
        animator.interpolator = DecelerateInterpolator()
        animator.start()
    }

    /**
     * progressBar消失动画
     */
    private fun startDismissAnimation(progress: Int) {
        val anim = ObjectAnimator.ofFloat(mProgressBar, "alpha", 1.0f, 0.0f)
        anim.duration = 1500 // 动画时长
        anim.interpolator = DecelerateInterpolator() // 减速
        // 关键, 添加动画进度监听器
        anim.addUpdateListener { valueAnimator ->
            val fraction = valueAnimator.animatedFraction // 0.0f ~ 1.0f
            val offset = 100 - progress
            if (null != mProgressBar) {
                mProgressBar.progress = (progress + offset * fraction).toInt()
            }
        }
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                // 动画结束
                mProgressBar.progress = 0
                mProgressBar.visibility = View.GONE
                isAnimStart = false
            }
        })
        anim.start()
    }

    /**
     * 用于演示X5webview实现视频的全屏播放功能 其中注意 X5的默认全屏方式 与 android 系统的全屏方式
     */
    // /////////////////////////////////////////
    // 向webview发出信息
    private fun enableX5FullscreenFunc() {
        if (mWebView.x5WebViewExtension != null) {
            LogUtils.i("enableX5FullscreenFunc:开启X5全屏播放模式")
            val data = Bundle()
            data.putBoolean("standardFullScreen", false) // true表示标准全屏，false表示X5全屏；不设置默认false，
            data.putBoolean("supportLiteWnd", true) // false：关闭小窗；true：开启小窗；不设置默认true，
            data.putInt("DefaultVideoScreen", 1) // 1：以页面内开始播放，2：以全屏开始播放；不设置默认：1
            mWebView.x5WebViewExtension.invokeMiscMethod(
                "setVideoParams",
                data
            )
        }
    }

    private fun disableX5FullscreenFunc() {
        if (mWebView.x5WebViewExtension != null) {
            LogUtils.i("disableX5FullscreenFunc:恢复webkit初始状态")
            val data = Bundle()
            data.putBoolean(
                "standardFullScreen",
                false
            ) // true表示标准全屏，会调起onShowCustomView()，false表示X5全屏；不设置默认false，
            data.putBoolean("supportLiteWnd", true) // false：关闭小窗；true：开启小窗；不设置默认true，
            data.putInt("DefaultVideoScreen", 1) // 1：以页面内开始播放，2：以全屏开始播放；不设置默认：1
            mWebView.x5WebViewExtension.invokeMiscMethod(
                "setVideoParams",
                data
            )
        }
    }

    private fun enableLiteWndFunc() {
        if (mWebView.x5WebViewExtension != null) {
            LogUtils.i("enableLiteWndFunc:开启小窗模式")
            val data = Bundle()
            data.putBoolean(
                "standardFullScreen",
                false
            ) // true表示标准全屏，会调起onShowCustomView()，false表示X5全屏；不设置默认false，
            data.putBoolean("supportLiteWnd", true) // false：关闭小窗；true：开启小窗；不设置默认true，
            data.putInt("DefaultVideoScreen", 1) // 1：以页面内开始播放，2：以全屏开始播放；不设置默认：1
            mWebView.x5WebViewExtension.invokeMiscMethod(
                "setVideoParams",
                data
            )
        }
    }

    private fun enablePageVideoFunc() {
        if (mWebView.x5WebViewExtension != null) {
            LogUtils.i("enablePageVideoFunc:页面内全屏播放模式")
            val data = Bundle()
            data.putBoolean(
                "standardFullScreen",
                false
            ) // true表示标准全屏，会调起onShowCustomView()，false表示X5全屏；不设置默认false，
            data.putBoolean("supportLiteWnd", true) // false：关闭小窗；true：开启小窗；不设置默认true，
            data.putInt("DefaultVideoScreen", 1) // 1：以页面内开始播放，2：以全屏开始播放；不设置默认：1
            mWebView.x5WebViewExtension.invokeMiscMethod(
                "setVideoParams",
                data
            )
        }
    }

    private fun openFileChooseProcess() {
        val i = Intent(Intent.ACTION_GET_CONTENT)
        i.addCategory(Intent.CATEGORY_OPENABLE)
        i.type = "*/*"
        startActivityForResult(Intent.createChooser(i, "test"), 0)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView != null && mWebView.canGoBack()) {
                mWebView.goBack()
                true
            } else super.onKeyDown(keyCode, event)
        } else super.onKeyDown(keyCode, event)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                0 -> {
                    if (null != uploadFile) {
                        val result =
                            if (data == null || resultCode != RESULT_OK) null else data.data
                        uploadFile!!.onReceiveValue(result)
                        uploadFile = null
                    }
                    if (null != uploadFiles) {
                        if (data == null || resultCode != RESULT_OK) {
                            return
                        }
                        val result = data.data
                        uploadFiles!!.onReceiveValue(arrayOf<Uri>(result!!))
                        uploadFiles = null
                    }
                }
                else -> {}
            }
        } else if (resultCode == RESULT_CANCELED) {
            if (null != uploadFile) {
                uploadFile!!.onReceiveValue(null)
                uploadFile = null
            }
        }
    }

    public override fun onResume() {
        super.onResume()
        mWebView.resumeTimers()
    }

    public override fun onPause() {
        super.onPause()
        mWebView.pauseTimers()
    }

    override fun onDestroy() {
        super.onDestroy()
        WebViewPool.getInstance().recycleWebView(fl_webView, mWebView)
        /* if (mWebView != null) {
            mWebView.release();
        }*/
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.webview_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.nav_share) {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(
                Intent.EXTRA_TEXT,
                getString(
                    R.string.share_article_url,
                    getString(R.string.about_app_name),
                    Html.fromHtml(title),
                    url
                )
            )
            intent.type = "text/plain"
            startActivity(intent)
            return true
        } else if (id == R.id.nav_collect) {
            val ret = CollectUtils.collectWebPage(this, title, url)
            if (ret == 0) {
                SnackbarUtils.makeShort(mWebView, getString(R.string.browser_collect_success))
                    .show()
            } else if (ret == 1) {
                SnackbarUtils.makeShort(mWebView, getString(R.string.browser_collection_exists))
                    .show()
            } else if (ret == -1) {
                SnackbarUtils.makeShort(mWebView, getString(R.string.browser_collect_failed)).show()
            }
            return true
        } else if (id == R.id.nav_copy_href) {
            val cmd = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            cmd.setPrimaryClip(ClipData.newPlainText(getString(R.string.copy_link), url))
            SnackbarUtils.makeShort(mWebView, R.string.copy_link_to_clipboard).show()
            return true
        } else if (id == R.id.nav_open_on_browser) {
            val intent = Intent(Intent.ACTION_VIEW) //"android.intent.action.VIEW"
            intent.data = Uri.parse(url)
            startActivity(intent)
            return true
        } else if (id == R.id.nav_refresh) {
            mWebView.reload()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val WEBVIEW_URL = "WEBVIEW_URL"
        const val WEBVIEW_TITLE = "WEBVIEW_TITLE"
        private val URLS_302 = Arrays.asList("jianshu", "wtloginmqq")

        /**
         * 启动浏览器
         *
         * @param context 上下文
         * @param title   标题
         * @param url     链接
         */
        @kotlin.jvm.JvmStatic
        fun startWithParams(context: Context, title: String?, url: String?) {
            val intent = Intent(context, BrowserActivity::class.java)
            intent.putExtra(WEBVIEW_TITLE, title)
            intent.putExtra(WEBVIEW_URL, url)
            context.startActivity(intent)
        }
    }
}