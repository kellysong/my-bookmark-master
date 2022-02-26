package com.sjl.bookmark.ui.activity

import android.content.Intent
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.Toast
import butterknife.OnClick
import com.sjl.bookmark.R
import com.sjl.bookmark.kotlin.language.I18nUtils
import com.sjl.core.mvp.BaseActivity
import com.sjl.core.mvp.NoPresenter
import kotlinx.android.synthetic.main.personality_activity.*
import kotlinx.android.synthetic.main.toolbar_default.*

/**
 * 个性签名
 *
 * @author Kelly
 * @version 1.0.0
 * @filename PersonalityActivity.java
 * @time 2018/11/29 10:00
 * @copyright(C) 2018 song
 */
class PersonalityActivity : BaseActivity<NoPresenter>() {

    override fun getLayoutId(): Int {
        return R.layout.personality_activity
    }

    override fun initView() {}
    override fun initListener() {
        bindingToolbar(common_toolbar, I18nUtils.getString(R.string.title_signature))
        et_personality.addTextChangedListener(object : TextWatcher {
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
                if (count == 120) {
                    Toast.makeText(this@PersonalityActivity, R.string.word_full, Toast.LENGTH_SHORT)
                        .show()
                    et_personality.setText(
                        et_personality.text.toString().substring(0, MAX_WORD)
                    )
                } else {
                    tv_msg.text = getString(
                        R.string.input_hint,
                        (MAX_WORD - et_personality.text.toString().length)
                    )
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    override fun initData() {
        val personality: String = intent.getStringExtra("personality")
        if (!TextUtils.isEmpty(personality)) {
            et_personality.setText(personality)
            tv_msg.text = getString(
                R.string.input_hint,
                (MAX_WORD - et_personality.text.toString().length)
            )
        }
    }

    @OnClick(R.id.btn_commit)
    fun onClick() {
        if (TextUtils.isEmpty(et_personality.text.toString())) {
            Toast.makeText(this, R.string.input_empty_hint, Toast.LENGTH_SHORT).show()
        } else {
            setResult(
                RESULT_OK,
                Intent().putExtra("personality", et_personality.text.toString())
            )
            finish()
        }
    }

    companion object {
        private val MAX_WORD: Int = 120
    }
}