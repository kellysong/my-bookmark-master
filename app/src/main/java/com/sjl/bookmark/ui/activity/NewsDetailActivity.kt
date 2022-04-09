package com.sjl.bookmark.ui.activity

import android.content.*
import android.graphics.*
import android.os.Build.VERSION_CODES
import android.text.TextUtils
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import cn.feng.skin.manager.loader.SkinManager
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.sjl.bookmark.R
import com.sjl.bookmark.dao.util.BrowseMapper
import com.sjl.bookmark.entity.zhihu.NewsDetailDto
import com.sjl.bookmark.entity.zhihu.NewsExtraDto
import com.sjl.bookmark.ui.contract.NewsDetailContract
import com.sjl.bookmark.ui.presenter.NewsDetailPresenter
import com.sjl.bookmark.widget.AppBarStateChangeListener
import com.sjl.core.mvp.BaseActivity
import com.sjl.core.util.log.LogUtils
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import kotlinx.android.synthetic.main.news_detail_activity.*

/**
 * 知乎日报详情
 *
 * @author Kelly
 * @version 1.0.0
 * @filename NewsDetailActivity.java
 * @time 2018/12/19 10:43
 * @copyright(C) 2018 song
 */
class NewsDetailActivity : BaseActivity<NewsDetailPresenter>(),
    NewsDetailContract.View {


    private var progressBar: ProgressBar? = null
    private var newsDetail: NewsDetailDto? = null
    private var scrollY: Int = 0
    private var id: String? = null
    private var title: String? = null
    private var image: String? = null
    private var longCommentsCount: Int = 0
    private var shortCommentsCount: Int = 0
    private var commentsCount: Int = 0
    override fun getLayoutId(): Int {
        return R.layout.news_detail_activity
    }

    override fun changeStatusBarColor() {}

    @RequiresApi(api = VERSION_CODES.M)
    override fun initView() {
        tv_toolbar_title.isSelected = true //跑马灯效果必须加
        progressBar = webView.progressBar
        val settings: WebSettings = webView.settings
        settings.useWideViewPort = false //不缩放
        settings.setSupportZoom(false)
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                progressBar?.apply {
                    if (newProgress == 100) {
                        visibility = View.GONE
                    } else {
                        if (View.GONE == visibility) {
                            visibility = View.VISIBLE
                        }
                        progress = newProgress
                    }
                }

            }
        }
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(webView: WebView, s: String): Boolean {
                return super.shouldOverrideUrlLoading(webView, s)
            }

            override fun onPageFinished(view: WebView, url: String) {
                if (!isActivityExist(NewsDetailActivity::class.java)) { //防止快速打开和关闭webView时发生控件空指针
                    return
                }
                if (webView == null) {
                    return
                }
                webView.addImageClickListenerByClass("content") //绑定图片点击事件
                //smoothScrollTo无效的解决办法
                view.postDelayed(object : Runnable {
                    override fun run() {
                        val positionY: Int = BrowseMapper.get(newsDetail!!.id.toString())
                        LogUtils.i("positionY...." + positionY)
                        if (positionY > 0) {
                            scrollY = positionY
                            nsv_content!!.smoothScrollTo(
                                0,
                                scrollY
                            ) //不能通过WebView滚动，发现无法得到理想效果
                        }
                    }
                }, 100)
            }
        }
    }

    override fun initListener() {
        bindingToolbar(toolbar, "")
        //分享
        share.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                val content: String = getString(
                    R.string.share_article_url,
                    getString(R.string.app_name),
                    newsDetail?.title,
                    newsDetail?.share_url
                )
                mPresenter.shareNews(
                    content,
                    if (newsDetail?.images != null) newsDetail!!.images[0] else ""
                )
            }
        })
        comment.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                val intent: Intent =
                    Intent(this@NewsDetailActivity, NewsCommentActivity::class.java)
                intent.putExtra("id", id)
                intent.putExtra("long_comments", longCommentsCount)
                intent.putExtra("short_comments", shortCommentsCount)
                intent.putExtra("comments", commentsCount)
                startActivity(intent)
            }
        })
        appbar.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onShadowChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                val color: Int = SkinManager.getInstance().colorPrimary
                val bgColor: Int
                if (color != -1) {
                    bgColor = changeAlpha(
                        color,
                        Math.abs(verticalOffset * 1.0f) / appBarLayout.totalScrollRange
                    )
                } else {
                    bgColor = changeAlpha(
                        resources.getColor(R.color.colorPrimary),
                        Math.abs(verticalOffset * 1.0f) / appBarLayout.totalScrollRange
                    )
                }
                setStatusBar(bgColor)
                toolbar.setBackgroundColor(bgColor)
            }

            override fun onStateChanged(appBarLayout: AppBarLayout, state: State) {
                if (state == State.EXPANDED) {
                    //展开状态
                    tv_toolbar_title!!.text = ""
                    //                    setStatusBar(Color.TRANSPARENT);
//                    toolbar.setBackgroundColor(Color.TRANSPARENT);
                } else if (state == State.COLLAPSED) {
                    //折叠状态
                    collapsing_toolbar_layout!!.title = title
                    tv_toolbar_title!!.text = title
                    tv_toolbar_title!!.isSelected = true //跑马灯效果必须加

//                    int color = SkinManager.getInstance().getColorPrimary();
//                    if (color != -1) {//不能少，否则状态栏无效
//                        setStatusBar(color);
//                        toolbar.setBackgroundColor(color);
//                    }else {
//                        int colorPrimary = NewsDetailActivity.this.getResources().getColor(R.color.colorPrimary);
//                        setStatusBar(colorPrimary);
//                        toolbar.setBackgroundColor(colorPrimary);
//                    }
                } else {

                    //中间状态
                }
            }
        })
    }

    /**
     * 根据百分比改变颜色透明度
     */
    fun changeAlpha(color: Int, fraction: Float): Int {
        val red: Int = Color.red(color)
        val green: Int = Color.green(color)
        val blue: Int = Color.blue(color)
        val alpha: Int = (Color.alpha(color) * fraction).toInt()
        return Color.argb(alpha, red, green, blue)
    }

    override fun initData() {
        id = intent.getStringExtra("id")
        title = intent.getStringExtra("title")
        image = intent.getStringExtra("image")
        if (TextUtils.isEmpty(id)) {
            return
        }

//        mCollapsingToolbarLayout.setCollapsedTitleGravity(Gravity.CENTER);//设置收缩后标题的位置
//        mCollapsingToolbarLayout.setExpandedTitleGravity(Gravity.CENTER);////设置展开后标题的位置
//        mCollapsingToolbarLayout.setTitle(title);//设置标题的名字
//        mCollapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);//设置展开后标题的颜色
//        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);//设置收缩后标题的颜色
        showLoadingDialog()
        mPresenter.loadNewsExtra(id)
        mPresenter.loadNewsDetail(id)
    }

    override fun showNewsExtra(newsExtra: NewsExtraDto) {
        longCommentsCount = newsExtra.long_comments
        shortCommentsCount = newsExtra.short_comments
        commentsCount = newsExtra.comments
        comment.text = commentsCount.toString()
    }

    override fun showNewsDetail(newsDetail: NewsDetailDto) {
        this.newsDetail = newsDetail
        if (newsDetail.image != null) {
            Glide.with(this@NewsDetailActivity)
                .load(newsDetail.image)
                .into((story_detail_image)!!)
            story_detail_description!!.text = newsDetail.title
            image_source!!.text = newsDetail.image_source
        } else {
            story_detail_description!!.setTextColor(Color.BLACK)
            story_detail_description!!.text = newsDetail.title
        }
        /**
         * 第一个参数需要传入与HTML相关的路径，由于我们的CSS文件存放在assets文件下，
         * 所以第一个参数传入“file:///android_asset/”，第二、三、四三个参数与loadData方法类似，
         * 所以第一和第五个参数为空时，两个方法是等价的。
         * 第五个参数，我目前也不是很理解，传入空即可
         */
        webView!!.loadDataWithBaseURL(
            "file:///android_asset/",
            newsDetail.contentBody,
            "text/html",
            "UTF-8",
            null
        )
        hideLoadingDialog()
    }

    override fun showError(errorMsg: String?) {
        showLongToast(errorMsg)
        hideLoadingDialog()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            hideLoadingDialog()
            if (webView!!.canGoBack()) {
                webView!!.goBack()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onPause() {
        super.onPause()
        if (webView != null) {
            /**
             * 当webView外层是FrameLayout
             * 由于x5WebView的本质为FrameLayout，无法重载View的getScrollY()，调用系统的getScrollY()方法实际调用的是FrameLayout的getScrollY()，所以返回值为0。因此，提供了getWebScrollY的方法获取对应数值
             */
            scrollY = nsv_content!!.scrollY
            LogUtils.i("scrollY....$scrollY")
            if (scrollY >= 0) {
                id?.let { BrowseMapper.put(it, scrollY) }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (webView != null) {
            webView.release()
        }
    }

    companion object {
        /**
         * 跳转
         *
         * @param context
         * @param id      新闻id
         * @param title   标题
         * @param image   图片url
         */
        @kotlin.jvm.JvmStatic
        fun startActivity(context: Context, id: Int, title: String?, image: String?) {
            val intent: Intent = Intent(context, NewsDetailActivity::class.java)
            intent.putExtra("id", id.toString()) //整形是传不过去的，要转成字符串
            if (!TextUtils.isEmpty(title)) {
                intent.putExtra("title", title)
            }
            if (!TextUtils.isEmpty(image)) {
                intent.putExtra("image", image)
            }
            context.startActivity(intent)
        }
    }
}