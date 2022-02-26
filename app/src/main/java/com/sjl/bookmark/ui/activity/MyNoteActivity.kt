package com.sjl.bookmark.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import butterknife.OnClick
import com.sjl.bookmark.R
import com.sjl.bookmark.app.AppConstant
import com.sjl.bookmark.dao.impl.DaoFactory
import com.sjl.bookmark.entity.table.Collection
import com.sjl.bookmark.kotlin.language.I18nUtils
import com.sjl.bookmark.ui.activity.MyNoteActivity
import com.sjl.core.mvp.BaseActivity
import com.sjl.core.mvp.NoPresenter
import kotlinx.android.synthetic.main.my_note_activity.*
import kotlinx.android.synthetic.main.toolbar_default.*
import java.util.*

/**
 * 笔记添加
 *
 * @author Kelly
 * @version 1.0.0
 * @filename MyNoteActivity.java
 * @time 2018/12/26 14:04
 * @copyright(C) 2018 song
 */
class MyNoteActivity : BaseActivity<NoPresenter>(), TextWatcher {

    private var noteFlag = false
    private var tempCollection: Collection? = null
    override fun getLayoutId(): Int {
        return R.layout.my_note_activity
    }

    override fun initView() {}
    override fun initListener() {
        bindingToolbar(common_toolbar, I18nUtils.getString(R.string.title_note))
        et_title.addTextChangedListener(this)
        et_content.addTextChangedListener(this)
    }

    override fun initData() {
        val intent = intent
        if (intent != null) {
            tempCollection = intent.getSerializableExtra("collection") as Collection
            noteFlag = if (tempCollection != null) {
                et_title.setText(tempCollection!!.title)
                et_content.setText(tempCollection!!.href)
                true //修改
            } else {
                false //添加
            }
        }
    }

    @OnClick(R.id.btn_save_note)
    fun onClick(view: View) {
        when (view.id) {
            R.id.btn_save_note -> saveNote()
            else -> {}
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (noteFlag) { //修改
                saveNote()
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun saveNote() {
        val title = et_title!!.text.toString()
        val content = et_content!!.text.toString()
        val collection: Collection?
        if (noteFlag) { //修改
            if (tempCollection!!.title == title && tempCollection!!.href == content) { //没有发生改变
                finish()
                return
            } else {
                tempCollection!!.title = title
                tempCollection!!.href = content
                collection = tempCollection
                DaoFactory.getCollectDao().updateCollection(collection)
            }
        } else { //新增
            collection = Collection(null, title, 1, content, Date(), 0, 0)
            DaoFactory.getCollectDao().saveCollection(collection)
        }
        val intent = Intent()
        val bundle = Bundle()
        bundle.putSerializable("collection", collection)
        bundle.putBoolean("noteFlag", noteFlag)
        intent.putExtras(bundle)
        setResult(AppConstant.RESULT_CODE, intent)
        finish()
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        val title = et_title!!.text.toString().trim { it <= ' ' }
        val content = et_content!!.text.toString().trim { it <= ' ' }
        btn_save_note!!.isEnabled = !TextUtils.isEmpty(title) && !TextUtils.isEmpty(content)
    }

    override fun afterTextChanged(s: Editable) {}

    companion object {
        /**
         * 查看我的笔记
         *
         * @param activity 上下文
         */
        fun startWithParams(activity: Activity, collection: Collection?) {
            val intent = Intent(activity, MyNoteActivity::class.java)
            intent.putExtra("collection", collection)
            activity.startActivityForResult(intent, AppConstant.REQUEST_CODE)
        }
    }
}