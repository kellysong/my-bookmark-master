package com.sjl.bookmark.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.sjl.bookmark.R;
import com.sjl.bookmark.app.AppConstant;
import com.sjl.bookmark.dao.impl.DaoFactory;
import com.sjl.bookmark.entity.table.Collection;
import com.sjl.bookmark.kotlin.language.I18nUtils;
import com.sjl.core.mvp.BaseActivity;

import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 笔记添加
 *
 * @author Kelly
 * @version 1.0.0
 * @filename MyNoteActivity.java
 * @time 2018/12/26 14:04
 * @copyright(C) 2018 song
 */
public class MyNoteActivity extends BaseActivity implements TextWatcher {
    @BindView(R.id.common_toolbar)
    Toolbar mToolBar;
    @BindView(R.id.et_title)
    TextInputEditText mTitle;
    @BindView(R.id.et_content)
    TextInputEditText mContent;
    @BindView(R.id.btn_save_note)
    Button mSaveNote;
    private boolean noteFlag = false;
    private Collection tempCollection;

    @Override
    protected int getLayoutId() {
        return R.layout.my_note_activity;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {
        bindingToolbar(mToolBar, I18nUtils.getString(R.string.title_note));
        mTitle.addTextChangedListener(this);
        mContent.addTextChangedListener(this);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            tempCollection = (Collection) intent.getSerializableExtra("collection");
            if (tempCollection != null) {
                mTitle.setText(tempCollection.getTitle());
                mContent.setText(tempCollection.getHref());
                noteFlag = true;//修改
            } else {
                noteFlag = false;//添加

            }
        }

    }

    @OnClick({R.id.btn_save_note})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_save_note:
                saveNote();

                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (noteFlag){//修改
                saveNote();
            }
        }
        return super.onKeyDown(keyCode, event);
    }



    private void saveNote() {
        String title = mTitle.getText().toString();
        String content = mContent.getText().toString();
        Collection collection;
        if (noteFlag) {//修改
            if (tempCollection.getTitle().equals(title) && tempCollection.getHref().equals(content)) {//没有发生改变
                finish();
                return;
            } else {
                tempCollection.setTitle(title);
                tempCollection.setHref(content);
                collection = tempCollection;
                DaoFactory.getCollectDao().updateCollection(collection);
            }
        } else {//新增
            collection = new Collection(null, title, 1, content, new Date(),0,0);
            DaoFactory.getCollectDao().saveCollection(collection);
        }
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("collection", collection);
        bundle.putBoolean("noteFlag", noteFlag);
        intent.putExtras(bundle);
        setResult(AppConstant.RESULT_CODE, intent);
        finish();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String title = mTitle.getText().toString().trim();
        String content = mContent.getText().toString().trim();
        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(content)) {
            mSaveNote.setEnabled(true);
        } else {
            mSaveNote.setEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    /**
     * 查看我的笔记
     *
     * @param activity 上下文
     */
    public static void startWithParams(Activity activity, Collection collection) {
        Intent intent = new Intent(activity, MyNoteActivity.class);
        intent.putExtra("collection", collection);
        activity.startActivityForResult(intent, AppConstant.REQUEST_CODE);
    }
}
