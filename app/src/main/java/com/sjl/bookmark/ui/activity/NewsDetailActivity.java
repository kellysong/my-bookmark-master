package com.sjl.bookmark.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import androidx.annotation.RequiresApi;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sjl.bookmark.R;
import com.sjl.bookmark.dao.util.BrowseMapper;
import com.sjl.bookmark.entity.zhihu.NewsDetailDto;
import com.sjl.bookmark.entity.zhihu.NewsExtraDto;
import com.sjl.bookmark.ui.contract.NewsDetailContract;
import com.sjl.bookmark.ui.presenter.NewsDetailPresenter;
import com.sjl.bookmark.widget.AppBarStateChangeListener;
import com.sjl.bookmark.widget.X5WebView;
import com.sjl.core.mvp.BaseActivity;
import com.sjl.core.util.log.LogUtils;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

import butterknife.BindView;
import cn.feng.skin.manager.loader.SkinManager;

/**
 * 知乎日报详情
 *
 * @author Kelly
 * @version 1.0.0
 * @filename NewsDetailActivity.java
 * @time 2018/12/19 10:43
 * @copyright(C) 2018 song
 */
public class NewsDetailActivity extends BaseActivity<NewsDetailPresenter> implements NewsDetailContract.View {

    @BindView(R.id.webView)
    X5WebView mWebView;
    @BindView(R.id.comment)
    TextView comment;//评论数量

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @BindView(R.id.appbar)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.story_detail_image)
    ImageView storyDetailImage;
    @BindView(R.id.story_detail_description)
    TextView storyDetailDescription;
    @BindView(R.id.tv_toolbar_title)
    TextView toolbarTitle;

    @BindView(R.id.image_source)
    TextView imageSource;

    @BindView(R.id.share)
    ImageView share;//分享
    @BindView(R.id.nsv_content)
    NestedScrollView mNestedScrollView;
    private ProgressBar progressBar;
    private NewsDetailDto newsDetail;

    private int scrollY;
    private String id;
    private String title;
    private String image;
    private int longCommentsCount;
    private int shortCommentsCount;
    private int commentsCount;


    @Override
    protected int getLayoutId() {

        return R.layout.news_detail_activity;
    }


    @Override
    protected void changeStatusBarColor() {

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void initView() {

        toolbarTitle.setSelected(true); //跑马灯效果必须加
        progressBar = mWebView.getProgressBar();
        final WebSettings settings = mWebView.getSettings();
        settings.setUseWideViewPort(false);//不缩放
        settings.setSupportZoom(false);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(com.tencent.smtt.sdk.WebView view, int newProgress) {

                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    if (View.GONE == progressBar.getVisibility()) {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                    progressBar.setProgress(newProgress);
                }

            }
        });
        mWebView.setWebViewClient(new com.tencent.smtt.sdk.WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String s) {
                return super.shouldOverrideUrlLoading(webView, s);
            }


            @Override
            public void onPageFinished(final WebView view, final String url) {
                if (!isActivityExist(NewsDetailActivity.class)) {//防止快速打开和关闭webView时发生控件空指针
                    return;
                }
                if (mWebView == null) {
                    return;
                }
                mWebView.addImageClickListenerByClass("content");//绑定图片点击事件
                //smoothScrollTo无效的解决办法
                view.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        int positionY = BrowseMapper.get(String.valueOf(newsDetail.getId()));
                        LogUtils.i("positionY...." + positionY);
                        if (positionY > 0) {
                            scrollY = positionY;
                            mNestedScrollView.smoothScrollTo(0, scrollY);//不能通过WebView滚动，发现无法得到理想效果
                        }
                    }
                }, 100);

            }
        });

    }

    @Override
    protected void initListener() {
        bindingToolbar(toolbar, "");
        //分享
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = getString(R.string.share_article_url, getString(R.string.app_name), newsDetail.getTitle(), newsDetail.getShare_url());
                mPresenter.shareNews(content, newsDetail.getImages() != null ? newsDetail.getImages().get(0) : "");
            }
        });

        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewsDetailActivity.this, NewsCommentActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("long_comments", longCommentsCount);
                intent.putExtra("short_comments", shortCommentsCount);
                intent.putExtra("comments", commentsCount);
                startActivity(intent);
            }
        });

        mAppBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onShadowChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int color = SkinManager.getInstance().getColorPrimary();
                int bgColor;
                if (color != - 1){
                    bgColor = changeAlpha(color, Math.abs(verticalOffset * 1.0f) / appBarLayout.getTotalScrollRange());
                }else {
                    bgColor = changeAlpha(getResources().getColor(R.color.colorPrimary), Math.abs(verticalOffset * 1.0f) / appBarLayout.getTotalScrollRange());
                }
                setStatusBar(bgColor);
                toolbar.setBackgroundColor(bgColor);
            }

            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if (state == AppBarStateChangeListener.State.EXPANDED) {
                    //展开状态
                    toolbarTitle.setText("");
//                    setStatusBar(Color.TRANSPARENT);
//                    toolbar.setBackgroundColor(Color.TRANSPARENT);
                } else if (state == State.COLLAPSED) {
                    //折叠状态
                    mCollapsingToolbarLayout.setTitle(title);
                    toolbarTitle.setText(title);
                    toolbarTitle.setSelected(true); //跑马灯效果必须加

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
        });
    }

    /**
     * 根据百分比改变颜色透明度
     */
    public int changeAlpha(int color, float fraction) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        int alpha = (int) (Color.alpha(color) * fraction);
        return Color.argb(alpha, red, green, blue);
    }


    @Override
    protected void initData() {
        id = getIntent().getStringExtra("id");
        title = getIntent().getStringExtra("title");
        image = getIntent().getStringExtra("image");
        if (TextUtils.isEmpty(id)) {
            return;
        }

//        mCollapsingToolbarLayout.setCollapsedTitleGravity(Gravity.CENTER);//设置收缩后标题的位置
//        mCollapsingToolbarLayout.setExpandedTitleGravity(Gravity.CENTER);////设置展开后标题的位置
//        mCollapsingToolbarLayout.setTitle(title);//设置标题的名字
//        mCollapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);//设置展开后标题的颜色
//        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);//设置收缩后标题的颜色

        showLoadingDialog();
        mPresenter.loadNewsExtra(id);
        mPresenter.loadNewsDetail(id);
    }


    /**
     * 跳转
     *
     * @param context
     * @param id      新闻id
     * @param title   标题
     * @param image   图片url
     */
    public static void startActivity(Context context, int id, String title, String image) {
        Intent intent = new Intent(context, NewsDetailActivity.class);
        intent.putExtra("id", String.valueOf(id));//整形是传不过去的，要转成字符串
        if (!TextUtils.isEmpty(title)) {
            intent.putExtra("title", title);
        }
        if (!TextUtils.isEmpty(image)) {
            intent.putExtra("image", image);
        }
        context.startActivity(intent);
    }

    @Override
    public void showNewsExtra(NewsExtraDto newsExtra) {
        longCommentsCount = newsExtra.getLong_comments();
        shortCommentsCount = newsExtra.getShort_comments();
        commentsCount = newsExtra.getComments();
        comment.setText(String.valueOf(commentsCount));
    }

    @Override
    public void showNewsDetail(final NewsDetailDto newsDetail) {
        this.newsDetail = newsDetail;
        if (newsDetail.getImage() != null) {
            Glide.with(NewsDetailActivity.this)
                    .load(newsDetail.getImage())
                    .into(storyDetailImage);
            storyDetailDescription.setText(newsDetail.getTitle());
            imageSource.setText(newsDetail.getImage_source());
        } else {
            storyDetailDescription.setTextColor(Color.BLACK);
            storyDetailDescription.setText(newsDetail.getTitle());
        }
        /**
         * 第一个参数需要传入与HTML相关的路径，由于我们的CSS文件存放在assets文件下，
         * 所以第一个参数传入“file:///android_asset/”，第二、三、四三个参数与loadData方法类似，
         * 所以第一和第五个参数为空时，两个方法是等价的。
         * 第五个参数，我目前也不是很理解，传入空即可
         */
        mWebView.loadDataWithBaseURL("file:///android_asset/", newsDetail.getContentBody(), "text/html", "UTF-8", null);
        hideLoadingDialog();
    }

    @Override
    public void showError(String errorMsg) {
        showLongToast(errorMsg);
        hideLoadingDialog();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            hideLoadingDialog();
            if (mWebView.canGoBack()) {
                mWebView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mWebView != null) {
            /**
             * 当webView外层是FrameLayout
             * 由于x5WebView的本质为FrameLayout，无法重载View的getScrollY()，调用系统的getScrollY()方法实际调用的是FrameLayout的getScrollY()，所以返回值为0。因此，提供了getWebScrollY的方法获取对应数值
             */
            scrollY = mNestedScrollView.getScrollY();
            LogUtils.i("scrollY...." + scrollY);
            if (scrollY >= 0) {
                BrowseMapper.put(id, scrollY);
            }

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            mWebView.release();
        }
    }
}
