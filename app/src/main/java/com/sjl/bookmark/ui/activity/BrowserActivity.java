package com.sjl.bookmark.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.webkit.JavascriptInterface;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.sjl.bookmark.R;
import com.sjl.bookmark.dao.util.CollectUtils;
import com.sjl.bookmark.ui.base.extend.BaseSwipeBackActivity;
import com.sjl.bookmark.widget.WebViewJavaScriptFunction;
import com.sjl.bookmark.widget.X5WebView;
import com.sjl.core.util.SnackbarUtils;
import com.sjl.core.util.log.LogUtils;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

/**
 * 集成腾讯x5内核webview add by Kelly on 20170209
 */
public class BrowserActivity extends BaseSwipeBackActivity {
    /**
     * 作为一个浏览器的示例展示出来，采用android+web的模式
     */
    private X5WebView mWebView;
    private ProgressBar mProgressBar;
    private FrameLayout mViewParent;

    public static final String WEBVIEW_URL = "WEBVIEW_URL";
    public static final String WEBVIEW_TITLE = "WEBVIEW_TITLE";


    /**
     * 用于展示在web端<input type=text>的标签被选择之后，文件选择器的制作和生成
     */
    private ValueCallback<Uri> uploadFile;
    private ValueCallback<Uri[]> uploadFiles;
    private static final List<String> URLS_302 = Arrays.asList("jianshu");
    /**
     * setDisplayShowTitleEnabled(boolean showTitle)方法：设置是否显示标题
     * <p>
     * setDisplayUseLogoEnabled(boolean useLogo)方法：设置是否显示logo
     * <p>
     * setDisplayShowHomeEnabled(boolean showHome)方法：设置是否显示返回
     */
    private Toolbar toolbar;
    private String url;
    private String title;

    private Bundle savedInstanceState;
    private boolean isAnimStart = false;
    private int currentProgress = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_browser;
    }

    @Override
    protected void changeStatusBarColor() {
        setColorForSwipeBack();
    }

    @Override
    protected void initView() {
        toolbar = (Toolbar) findViewById(R.id.common_toolbar);
        setSupportActionBar(toolbar);
        //关键下面两句话，设置了回退按钮，及点击事件的效果
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mViewParent = (FrameLayout) findViewById(R.id.webView1);
        mWebView = new X5WebView(this, null);

        if (savedInstanceState != null) {
            LogUtils.i("webview状态不为空，正在恢复");
            mWebView.restoreState(savedInstanceState);
        } else {
            Intent intent = getIntent();
            url = intent.getStringExtra(BrowserActivity.WEBVIEW_URL);
            title = intent.getStringExtra(BrowserActivity.WEBVIEW_TITLE);
            initWebView();
        }

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {

    }

    /**
     * activity意外被杀
     *
     * @param outState
     * @param outPersistentState
     */
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        LogUtils.i("触发保存webview状态");
        if (mWebView != null) {
            mWebView.saveState(outState);
        }

    }

    private void initWebView() {
//        mWebView = new X5WebView(this, null);
        mProgressBar = mWebView.getProgressBar();

        mViewParent.addView(mWebView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.FILL_PARENT,
                FrameLayout.LayoutParams.FILL_PARENT));

        mWebView.setWebViewClient(new WebViewClient() {
          /*  @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setAlpha(1.0f);
            }*/


            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                if (check302(url)) {
                    return true;
                }
                return false;
            }

            /**
             * 防止加载网页时调起系统浏览器
             *//*
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                super.shouldOverrideUrlLoading(view, url);//表示开发者自己不处理，交给系统处理
                return true;//表示自己处理，不需要系统处理，比如如果是true，重定向就不会跳转
            }*/
            @Override
            public void onPageFinished(final WebView view, final String url) {
                super.onPageFinished(view, url);
                if (!isActivityExist(BrowserActivity.class)) {//防止快速打开和关闭webView时发生控件空指针
                    return;
                }
                if (toolbar != null) {
                    toolbar.setTitle(view.getTitle());
                }
//                addImageClickListener(view);//待网页加载完全后设置图片点击的监听方法
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {

               /* if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    if (View.GONE == mProgressBar.getVisibility()) {
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                    mProgressBar.setProgress(newProgress);
                }*/

                if (null == mProgressBar) return;
                currentProgress = mProgressBar.getProgress();
                if (newProgress >= 100 && !isAnimStart) {
                    // 防止调用多次动画
                    isAnimStart = true;
                    mProgressBar.setProgress(newProgress);
                    // 开启属性动画让进度条平滑消失
                    startDismissAnimation(mProgressBar.getProgress());
                } else {
                    if (View.GONE == mProgressBar.getVisibility()) {
                        isAnimStart = false;
                        mProgressBar.setVisibility(View.VISIBLE);
                        mProgressBar.setAlpha(1.0f);
                    }
                    // 开启属性动画让进度条平滑递增
                    startProgressAnimation(newProgress);
                }

            }

            // For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                Log.i("test", "openFileChooser 1");
                BrowserActivity.this.uploadFile = uploadFile;
                openFileChooseProcess();
            }

            // For Android < 3.0
            public void openFileChooser(ValueCallback<Uri> uploadMsgs) {
                Log.i("test", "openFileChooser 2");
                BrowserActivity.this.uploadFile = uploadFile;
                openFileChooseProcess();
            }

            // For Android  > 4.1.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                Log.i("test", "openFileChooser 3");
                BrowserActivity.this.uploadFile = uploadFile;
                openFileChooseProcess();
            }

            // For Android  >= 5.0
            public boolean onShowFileChooser(com.tencent.smtt.sdk.WebView webView,
                                             ValueCallback<Uri[]> filePathCallback,
                                             WebChromeClient.FileChooserParams fileChooserParams) {
                Log.i("test", "openFileChooser 4:" + filePathCallback.toString());
                BrowserActivity.this.uploadFiles = filePathCallback;
                openFileChooseProcess();
                return true;
            }

        });

        mWebView.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String url, String arg1, String arg2,
                                        String arg3, long arg4) {
                try {
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    BrowserActivity.this.startActivity(intent);
                } catch (Exception e) {
                    LogUtils.e("调用系统浏览器下载异常", e);
                }
            }
        });

        mWebView.addJavascriptInterface(new WebViewJavaScriptFunction() {

            @Override
            public void onJsFunctionCalled(String tag) {
                LogUtils.i("WebViewJavaScriptFunction,tag:" + tag);

            }

            @JavascriptInterface
            public void onX5ButtonClicked() {
                BrowserActivity.this.enableX5FullscreenFunc();//开启X5全屏播放模式
            }

            @JavascriptInterface
            public void onCustomButtonClicked() {
                BrowserActivity.this.disableX5FullscreenFunc();//恢复webkit初始状态
            }

            @JavascriptInterface
            public void onLiteWndButtonClicked() {//开启小窗模式
                BrowserActivity.this.enableLiteWndFunc();
            }

            @JavascriptInterface
            public void onPageVideoClicked() {//页面内全屏播放模式
                BrowserActivity.this.enablePageVideoFunc();
            }
        }, "Android");
        if (url == null) {
            finish();
            return;
        } else {
            mWebView.loadUrl(url);
        }
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();

        //去除QQ浏览器推广
        getWindow().getDecorView().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                ArrayList<View> outView = new ArrayList<View>();
                getWindow().getDecorView().findViewsWithText(outView, "QQ浏览器", View.FIND_VIEWS_WITH_TEXT);
                if (outView != null && outView.size() > 0) {
                    outView.get(0).setVisibility(View.GONE);
                }
            }
        });
    }

    private boolean check302(String webUrl) {
        for (String url : URLS_302) {
            if (webUrl != null && webUrl.startsWith(url)) {
                return true;
            }
        }
        return false;
    }

    /**
     * progressBar递增动画
     */
    private void startProgressAnimation(int newProgress) {
        ObjectAnimator animator = ObjectAnimator.ofInt(mProgressBar, "progress", currentProgress, newProgress);
        animator.setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    /**
     * progressBar消失动画
     */
    private void startDismissAnimation(final int progress) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(mProgressBar, "alpha", 1.0f, 0.0f);
        anim.setDuration(1500);  // 动画时长
        anim.setInterpolator(new DecelerateInterpolator());     // 减速
        // 关键, 添加动画进度监听器
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float fraction = valueAnimator.getAnimatedFraction();      // 0.0f ~ 1.0f
                int offset = 100 - progress;
                if (null != mProgressBar) {
                    mProgressBar.setProgress((int) (progress + offset * fraction));
                }
            }
        });

        anim.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                // 动画结束
                mProgressBar.setProgress(0);
                mProgressBar.setVisibility(View.GONE);
                isAnimStart = false;
            }
        });
        anim.start();
    }


    /**
     * 用于演示X5webview实现视频的全屏播放功能 其中注意 X5的默认全屏方式 与 android 系统的全屏方式
     */
    // /////////////////////////////////////////
    // 向webview发出信息
    private void enableX5FullscreenFunc() {

        if (mWebView.getX5WebViewExtension() != null) {
            LogUtils.i("enableX5FullscreenFunc:开启X5全屏播放模式");

            Bundle data = new Bundle();

            data.putBoolean("standardFullScreen", false);// true表示标准全屏，false表示X5全屏；不设置默认false，

            data.putBoolean("supportLiteWnd", true);// false：关闭小窗；true：开启小窗；不设置默认true，

            data.putInt("DefaultVideoScreen", 1);// 1：以页面内开始播放，2：以全屏开始播放；不设置默认：1

            mWebView.getX5WebViewExtension().invokeMiscMethod("setVideoParams",
                    data);
        }
    }

    private void disableX5FullscreenFunc() {
        if (mWebView.getX5WebViewExtension() != null) {
            LogUtils.i("disableX5FullscreenFunc:恢复webkit初始状态");
            Bundle data = new Bundle();

            data.putBoolean("standardFullScreen", false);// true表示标准全屏，会调起onShowCustomView()，false表示X5全屏；不设置默认false，

            data.putBoolean("supportLiteWnd", true);// false：关闭小窗；true：开启小窗；不设置默认true，

            data.putInt("DefaultVideoScreen", 1);// 1：以页面内开始播放，2：以全屏开始播放；不设置默认：1

            mWebView.getX5WebViewExtension().invokeMiscMethod("setVideoParams",
                    data);
        }
    }

    private void enableLiteWndFunc() {
        if (mWebView.getX5WebViewExtension() != null) {
            LogUtils.i("enableLiteWndFunc:开启小窗模式");
            Bundle data = new Bundle();

            data.putBoolean("standardFullScreen", false);// true表示标准全屏，会调起onShowCustomView()，false表示X5全屏；不设置默认false，

            data.putBoolean("supportLiteWnd", true);// false：关闭小窗；true：开启小窗；不设置默认true，

            data.putInt("DefaultVideoScreen", 1);// 1：以页面内开始播放，2：以全屏开始播放；不设置默认：1

            mWebView.getX5WebViewExtension().invokeMiscMethod("setVideoParams",
                    data);
        }
    }

    private void enablePageVideoFunc() {
        if (mWebView.getX5WebViewExtension() != null) {
            LogUtils.i("enablePageVideoFunc:页面内全屏播放模式");
            Bundle data = new Bundle();
            data.putBoolean("standardFullScreen", false);// true表示标准全屏，会调起onShowCustomView()，false表示X5全屏；不设置默认false，

            data.putBoolean("supportLiteWnd", true);// false：关闭小窗；true：开启小窗；不设置默认true，

            data.putInt("DefaultVideoScreen", 1);// 1：以页面内开始播放，2：以全屏开始播放；不设置默认：1

            mWebView.getX5WebViewExtension().invokeMiscMethod("setVideoParams",
                    data);
        }
    }


    private void openFileChooseProcess() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");
        startActivityForResult(Intent.createChooser(i, "test"), 0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView != null && mWebView.canGoBack()) {
                mWebView.goBack();
                return true;
            } else
                return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:
                    if (null != uploadFile) {
                        Uri result = data == null || resultCode != RESULT_OK ? null
                                : data.getData();
                        uploadFile.onReceiveValue(result);
                        uploadFile = null;
                    }
                    if (null != uploadFiles) {
                        Uri result = data == null || resultCode != RESULT_OK ? null
                                : data.getData();
                        uploadFiles.onReceiveValue(new Uri[]{result});
                        uploadFiles = null;
                    }
                    break;
                default:
                    break;
            }
        } else if (resultCode == RESULT_CANCELED) {
            if (null != uploadFile) {
                uploadFile.onReceiveValue(null);
                uploadFile = null;
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.webview_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_share) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_article_url, getString(R.string.app_name), Html.fromHtml(title), url));
            intent.setType("text/plain");
            startActivity(intent);
            return true;
        } else if (id == R.id.nav_collect) {
            int ret = CollectUtils.collectWebPage(this, title, url);
            if (ret == 0) {
                SnackbarUtils.makeShort(mWebView, "收藏成功").show();
            } else if (ret == 1) {
                SnackbarUtils.makeShort(mWebView, "收藏已存在").show();
            } else if (ret == -1) {
                SnackbarUtils.makeShort(mWebView, "收藏失败").show();
            }
            return true;
        } else if (id == R.id.nav_copy_href) {
            ClipboardManager cmd = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            cmd.setPrimaryClip(ClipData.newPlainText(getString(R.string.copy_link), url));
            SnackbarUtils.makeShort(mWebView, R.string.copy_link_to_clipboard).show();
            return true;
        } else if (id == R.id.nav_open_on_browser) {
            Intent intent = new Intent(Intent.ACTION_VIEW);//"android.intent.action.VIEW"
            intent.setData(Uri.parse(url));
            startActivity(intent);
            return true;
        } else if (id == R.id.nav_refresh) {
            mWebView.reload();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 启动浏览器
     *
     * @param context 上下文
     * @param title   标题
     * @param url     链接
     */
    public static void startWithParams(Context context, String title, String url) {
        Intent intent = new Intent(context, BrowserActivity.class);
        intent.putExtra(WEBVIEW_TITLE, title);
        intent.putExtra(WEBVIEW_URL, url);
        context.startActivity(intent);
    }
}
