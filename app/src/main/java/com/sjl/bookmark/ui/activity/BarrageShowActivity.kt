package com.sjl.bookmark.ui.activity

import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import com.sjl.bookmark.R
import com.sjl.bookmark.kotlin.language.LanguageManager.getLocalContext
import com.sjl.core.mvp.BaseActivity
import com.sjl.core.mvp.NoPresenter
import kotlinx.android.synthetic.main.barrage_show_activity.*


/**
 * 弹幕LED显示
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BarrageShowActivity
 * @time 2021/5/9 13:20
 * @copyright(C) 2021 song
 */
class BarrageShowActivity : BaseActivity<NoPresenter>() {

    override fun getLayoutId(): Int {
        return R.layout.barrage_show_activity
    }

    override fun initView() {}
    override fun attachBaseContext(newBase: Context) { //横屏要加这个才行，未知原因
        super.attachBaseContext(getLocalContext(newBase))
    }

    override fun initListener() {
        et_content.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                val trim: String = et_content!!.text.toString().trim { it <= ' ' }
                if (TextUtils.isEmpty(trim)) {
                    return
                }
                tv_barrage.initScrollTextView(windowManager, trim, 10f)
                tv_barrage.starScroll()
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    override fun onDestroy() {
        if (tv_barrage != null) {
            tv_barrage.stopScroll()
        }
        super.onDestroy()
    }

    override fun initData() {}
}