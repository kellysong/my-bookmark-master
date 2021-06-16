package com.sjl.bookmark.util;

import android.view.ViewGroup;

import com.sjl.bookmark.app.MyApplication;
import com.sjl.bookmark.widget.X5WebView;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename WebViewPool
 * @time 2021/6/11 17:36
 * @copyright(C) 2021 song
 */
public class WebViewPool {
    private static List<WebVieWrap> webViewPool = new ArrayList<>();

    private static final byte[] lock = new byte[0];
    private static int maxSize = 2;


    private WebViewPool() {
        webViewPool = new ArrayList<>();

    }

    private static volatile WebViewPool instance = null;

    public static WebViewPool getInstance() {
        if (instance == null) {
            synchronized (WebViewPool.class) {
                if (instance == null) {
                    instance = new WebViewPool();
                }
            }
        }
        return instance;
    }

    /**
     * webView 初始化
     * 最好放在application onCreate里
     */
    public static void init() {
        for (int i = 0; i < maxSize; i++) {
            X5WebView webView = new X5WebView(MyApplication.getContext());
            WebVieWrap webVieWrap = new WebVieWrap();
            webVieWrap.x5WebView = webView;
            webViewPool.add(webVieWrap);
        }
    }


    /**
     * 获取webView
     */
    public synchronized X5WebView getWebView() {
        if (webViewPool.size() < maxSize) {
            return buildWebView();
        }
        X5WebView x5WebView = checkWebView();
        if (x5WebView != null) {//为空说明连接被销毁了
            return x5WebView;
        }
        //再次判断
        if (webViewPool.size() < maxSize) {
            return buildWebView();
        }
        //超出连接数,等待
        try {
            wait(2 * 1000);
            x5WebView = getWebView();
            return x5WebView;
        } catch (Exception e) {
        }
        throw new RuntimeException("webView池已满");
    }


    private  X5WebView checkWebView() {
        for (int i = webViewPool.size() - 1; i >= 0; i--) {
            WebVieWrap webVieWrap = webViewPool.get(i);
            if (webVieWrap.inUse) {
                continue;
            }
            X5WebView x5WebView = webVieWrap.x5WebView;
            webVieWrap.inUse = true;
            return x5WebView;
        }
        return null;
    }


    /**
     * 回收webView
     * @param webView
     */
    public synchronized void recycleWebView(X5WebView webView) {
        for (int i = 0; i < webViewPool.size(); i++) {
            WebVieWrap webVieWrap = webViewPool.get(i);
            X5WebView temp = webVieWrap.x5WebView;
            if (webView == temp) {
                temp.stopLoading();
                temp.setWebChromeClient(null);
                temp.setWebViewClient(null);
                temp.clearHistory();
//                temp.clearCache(true);
                temp.loadUrl("about:blank");
//                temp.pauseTimers();
                webVieWrap.inUse = false;
                break;
            }
        }
        notifyAll();
    }

    /**
     * 创建webView
     * @return
     */
    private X5WebView buildWebView() {
        X5WebView webView = new X5WebView(MyApplication.getContext());
        WebVieWrap webVieWrap = new WebVieWrap();
        webVieWrap.x5WebView = webView;
        webViewPool.add(webVieWrap);
        return webView;
    }


    /**
     * 销毁连接池
     */
    public static void destroyPool() {
        try {
            if (webViewPool.size() == 0) {
                return;
            }
            for (WebVieWrap webVieWrap : webViewPool) {
                X5WebView webView = webVieWrap.x5WebView;
                webView.destroy();
            }
            webViewPool.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 回收webView ,解绑
     *
     * @param webView 需要被回收的webView
     */
    public void recycleWebView(ViewGroup view, X5WebView webView) {
        if (view != null && webView != null){
            recycleWebView(webView);
            view.removeView(webView);

        }
    }

    /**
     * 设置webView池个数
     *
     * @param size webView池个数
     */
    public void setMaxPoolSize(int size) {
        synchronized (lock) {
            maxSize = size;
        }
    }

    public static class WebVieWrap {
        public X5WebView x5WebView;
        public boolean inUse;

    }
}
