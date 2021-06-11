package com.sjl.bookmark.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.JavascriptInterface;
import android.widget.ProgressBar;

import com.sjl.bookmark.R;
import com.sjl.bookmark.ui.activity.PhotoBrowserActivity;
import com.sjl.core.util.log.LogUtils;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebSettings.LayoutAlgorithm;
import com.tencent.smtt.sdk.WebView;

import org.json.JSONArray;
import org.json.JSONException;

public class X5WebView extends WebView {

    /**
     * 自定义一个滚动完毕的监听接口
     */
    private OverScrolledListener overScrolledListener = null;

    public interface OverScrolledListener {
        void onOver(int scrollY);
    }

    public void setOverScrolledListener(OverScrolledListener overScrolledListener) {
        this.overScrolledListener = overScrolledListener;
    }

    private ProgressBar progressBar;

    public X5WebView(Context arg0) {
        this(arg0,null);
    }


    @SuppressLint("SetJavaScriptEnabled")
    public X5WebView(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
        setBackgroundColor(85621);
        if (!isInEditMode()) {
            initView(arg0);
        }
        initWebViewSettings();
        this.getView().setClickable(true);
        this.getView().setOverScrollMode(View.OVER_SCROLL_ALWAYS);

    }

    private void initView(Context context) {
        progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 5);
        progressBar.setLayoutParams(params);
        Drawable drawable;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            drawable = context.getResources().getDrawable(R.drawable.webview_pg, null);
        } else {
            drawable = context.getResources().getDrawable(R.drawable.webview_pg);

        }
        progressBar.setProgressDrawable(drawable);
        addView(progressBar);
        addJavascriptInterface(new MJavascriptInterface(context), "imagelistener");
        LogUtils.i(getX5WebViewExtension() == null ? "Sys Core" : "X5  Core:" + QbSdk.getTbsVersion(getContext()));

    }

    private void initWebViewSettings() {
        WebSettings webSettings = this.getSettings();
        webSettings.setAllowFileAccess(true);
        webSettings.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportMultipleWindows(false);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setAppCacheMaxSize(Long.MAX_VALUE);
        webSettings.setAppCachePath(this.getContext().getDir("appcache", 0).getPath());
        webSettings.setDatabasePath(this.getContext().getDir("databases", 0).getPath());
        webSettings.setGeolocationDatabasePath(this.getContext().getDir("geolocation", 0)
                .getPath());
        webSettings.setPluginState(WebSettings.PluginState.ON_DEMAND);
        //this.getSettingsExtension().setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);//extension// settings 的设计
    }

    //clampedX，clampedY用于判断是否发生了onOverScrolled
    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        //设置监听，如果没有滑动到底部，则记录下当前的位置，如果滑动完毕，则返回true
        if (overScrolledListener != null) {
            overScrolledListener.onOver(scrollY);
        }
    }


    /**
     * 释放webview资源
     */
    public void release() {
        ViewParent parent = this.getParent();
        if (parent != null) {
            ((ViewGroup) parent).removeView(this);
        }
        this.stopLoading();
        this.clearCache(true);//清除网页访问留下的缓存，由于内核缓存是全局的因此这个方法不仅仅针对webview而是针对整个应用程序.
        this.clearHistory();//清除当前webview访问的历史记录，只会webview访问历史记录里的所有记录除了当前访问记录.
        this.clearFormData();//这个api仅仅清除自动完成填充的表单数据，并不会清除WebView存储到本地的数据

        this.setWebChromeClient(null);
        this.setWebViewClient(null);
        // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
        this.getSettings().setJavaScriptEnabled(false);
        this.clearView();
        this.removeAllViews();
        this.destroy();
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    /**
     * 绑定所有img标签
     */
    public void addImageClickListenerByImg() {
        //!/\.(gif|jpg|jpeg|png)$/.test(url.toLowerCase())
        loadUrl("javascript:(function(){" +
                "var imgArr = new Array();" +
                "var objs = document.getElementsByTagName(\"img\");" +
                "for(var i = 0; i < objs.length; i++) {" +
                "var url = objs[i].src;" +
                "if(url != null && url.length > 0) {" +
                "if(false) {" +
                "continue;" +
                "} else {" +
                "imgArr.push(url);" +
                "objs[i].onclick = function() {" +
                "window.imagelistener.openImage(this.src);" +
                "}" +
                "}" +
                "}" +
                "if(i == objs.length - 1) {" +
                "window.imagelistener.setImage(JSON.stringify(imgArr));" +
                "}" +
                "}" +
                "})()");
    }

    /**
     * 通过className绑定其下img标签
     *
     * @param className
     */
    public void addImageClickListenerByClass(String className) {
        //!/\.(gif|jpg|jpeg|png)$/.test(url.toLowerCase())
        loadUrl("javascript:(function(){" +
                "var imgArr = new Array();" +
                "var classObjs = document.getElementsByClassName('" + className + "');" +
                "if(classObjs == null || classObjs.length == 0){ return false;}" +
                "var objs = classObjs[0].getElementsByTagName(\"img\");" +
                "for(var i = 0; i < objs.length; i++) {" +
                "var url = objs[i].src;" +
                "if(url != null && url.length > 0) {" +
                "if(false) {" +
                "continue;" +
                "} else {" +
                "imgArr.push(url);" +
                "objs[i].onclick = function() {" +
                "window.imagelistener.openImage(this.src);" +
                "}" +
                "}" +
                "}" +
                "if(i == objs.length - 1) {" +
                "window.imagelistener.setImage(JSON.stringify(imgArr));" +
                "}" +
                "}" +
                "})()");
    }


    public static class MJavascriptInterface {
        private Context context;
        private String[] imageUrls;

        public MJavascriptInterface(Context context) {
            this.context = context;
        }

        @JavascriptInterface
        public void openImage(String img) {
            Intent intent = new Intent();
            intent.putExtra("imageUrls", imageUrls);
            intent.putExtra("curImageUrl", img);
            intent.setClass(context, PhotoBrowserActivity.class);
            context.startActivity(intent);
//            if (context instanceof Activity){
//                ((Activity) context).overridePendingTransition(R.anim.splash_fade_in,R.anim.splash_fade_out);
//            }
        }

        @JavascriptInterface
        public void setImage(String imgArr) {
            try {
                JSONArray jsonArray = new JSONArray(imgArr);
                int length = jsonArray.length();
                imageUrls = new String[length];
                for (int i = 0; i < length; i++) {
                    String imgUrl = jsonArray.getString(i);
//                    LogUtils.i("图片地址:" + imgUrl);
                    imageUrls[i] = imgUrl;
                }
            } catch (JSONException e) {
                LogUtils.e("解析图片url异常", e);
            }
        }
    }


}
