package com.sjl.bookmark.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.sjl.bookmark.R
import com.sjl.core.mvp.BaseActivity
import com.sjl.core.mvp.NoPresenter
import kotlinx.android.synthetic.main.doc_browser_activity.*


/**
 * 文件浏览
 *
 * @author Kelly
 * @version 1.0.0
 * @filename DocBrowserActivity.java
 * @time 2019/8/5 9:23
 * @copyright(C) 2019 song
 */
class DocBrowserActivity : BaseActivity<NoPresenter>() {
    override fun getLayoutId(): Int {
        return R.layout.doc_browser_activity
    }

    override fun initView() {
    }

    override fun initListener() {}
    override fun initData() {
        documentReaderView.show(intent.getStringExtra("path"))
    }

    public override fun onDestroy() {
        super.onDestroy()
        if (documentReaderView != null) {
            documentReaderView.stop()
        }
    }

    companion object {
        fun show(context: Context, url: String?) {
            val intent: Intent = Intent(context, DocBrowserActivity::class.java)
            val bundle: Bundle = Bundle()
            bundle.putSerializable("path", url)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }
}