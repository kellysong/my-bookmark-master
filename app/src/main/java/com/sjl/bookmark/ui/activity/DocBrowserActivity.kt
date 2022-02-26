package com.sjl.bookmark.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.sjl.bookmark.R;
import com.sjl.bookmark.widget.FileReaderView;
import com.sjl.core.mvp.BaseActivity;

/**
 * 文件浏览
 *
 * @author Kelly
 * @version 1.0.0
 * @filename DocBrowserActivity.java
 * @time 2019/8/5 9:23
 * @copyright(C) 2019 song
 */
public class DocBrowserActivity extends BaseActivity {
    private FileReaderView mDocumentReaderView;

    @Override
    protected int getLayoutId() {
        return R.layout.doc_browser_activity;
    }

    @Override
    protected void initView() {
        mDocumentReaderView = findViewById(R.id.documentReaderView);

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {
        mDocumentReaderView.show(getIntent().getStringExtra("path"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDocumentReaderView != null) {
            mDocumentReaderView.stop();
        }
    }

    public static void show(Context context, String url) {
        Intent intent = new Intent(context, DocBrowserActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("path", url);
        intent.putExtras(bundle);
        context.startActivity(intent);

    }
}
