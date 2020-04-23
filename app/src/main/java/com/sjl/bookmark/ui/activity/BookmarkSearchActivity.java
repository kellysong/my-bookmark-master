package com.sjl.bookmark.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sjl.bookmark.R;
import com.sjl.core.mvp.BaseActivity;
import com.sjl.core.util.log.LogUtils;
import com.sjl.core.widget.FlowLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookmarkSearchActivity.java
 * @time 2018/2/9 17:54
 * @copyright(C) 2018 song
 */
public class BookmarkSearchActivity extends BaseActivity implements View.OnClickListener{
    @BindView(R.id.search_toolbar)
    Toolbar toolbar;

    @BindView(R.id.cet_search_word)
    EditText cet_search_word;

    @BindView(R.id.iv_search)
    ImageView mSearch;
    @BindView(R.id.flowlayout)
    FlowLayout mFlowLayout;

    @BindView(R.id.search_history_ll)
    LinearLayout mSearchHistoryLl;
    @BindView(R.id.search_history_lv)
    ListView listView;
    @BindView(R.id.clear_history_btn)
    Button mClearHistory;



    private LayoutInflater mInflater;

    /**
     * 搜索标签，暂时写死
     */
    private String[] mVals = new String[]{"JavaSE", "Android", "JavaEE", "Mui",
            "数据库", "前端", "应用商店", "其它",
            "基础知识", "推送", "css", "h5", "网站"};//数据模拟，实际应从网络获取此数据

    public static final String KEY_SEARCH_HISTORY_KEYWORD = "key_search_history_keyword";
    public static final String KEY_SEARCH_TEXT = "key_search_text";//搜索文本
    private SharedPreferences mPref;//使用SharedPreferences记录搜索历史
    private List<String> mHistoryKeywords;//记录文本
    private ArrayAdapter<String> mArrAdapter;//搜索历史适配器


    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected void initView() {
        setSupportActionBar(toolbar);
        cet_search_word.requestFocus();//获取焦点 光标出现
        initHistoryView();
    }

    @Override
    protected void initListener() {
        mSearch.setOnClickListener(this);
        mClearHistory.setOnClickListener(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void initData() {
        mInflater = LayoutInflater.from(this);
        for (int i = 0; i < mVals.length; i++) {
            TextView tv = (TextView) mInflater.inflate(
                    R.layout.search_label_tv, mFlowLayout, false);
            tv.setText(mVals[i]);
            final String str = tv.getText().toString();
            //点击事件
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //加入搜索历史纪录记录
                    showSearchList(str);
                }
            });
            mFlowLayout.addView(tv);
        }
    }


    /************
     * 以上为流式标签相关
     ************/

    private void initHistoryView() {

        mHistoryKeywords = new ArrayList<String>();

        cet_search_word.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    if (mHistoryKeywords.size() > 0) {
                        mSearchHistoryLl.setVisibility(View.VISIBLE);
                    } else {
                        mSearchHistoryLl.setVisibility(View.GONE);
                    }
                } else {
                    mSearchHistoryLl.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        cet_search_word.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId== EditorInfo.IME_ACTION_SEARCH ||(event!=null&&event.getKeyCode()== KeyEvent.KEYCODE_ENTER)) {
                    String keywords = cet_search_word.getText().toString();
                    if (!TextUtils.isEmpty(keywords)) {
                        save();
                        showSearchList(keywords);
                    }
                    return true;
                }
                return false;
            }
        });
        initSearchHistory();
    }

    /**
     * 初始化搜索历史记录
     */
    public void initSearchHistory() {
        mPref = getSharedPreferences("search_config", MODE_PRIVATE);
        String history = mPref.getString(KEY_SEARCH_HISTORY_KEYWORD, "");
        if (!TextUtils.isEmpty(history)) {
            List<String> list = new ArrayList<String>();
            for (Object o : history.split(",")) {
                list.add((String) o);
            }
            mHistoryKeywords = list;
        }
        if (mHistoryKeywords.size() > 0) {
            mSearchHistoryLl.setVisibility(View.VISIBLE);
        } else {
            mSearchHistoryLl.setVisibility(View.GONE);
        }
        mArrAdapter = new ArrayAdapter<String>(this, R.layout.item_search_history, mHistoryKeywords);
        listView.setAdapter(mArrAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showSearchList(mHistoryKeywords.get(i));
            }
        });
        mArrAdapter.notifyDataSetChanged();
    }

    /**
     * 储存搜索历史
     */
    public void save() {
        String text = cet_search_word.getText().toString();
        String oldText = mPref.getString(KEY_SEARCH_HISTORY_KEYWORD, "");
        LogUtils.i("oldText:"+oldText+","+text+",oldText.contains(text):"+oldText.contains(text));

        if (!TextUtils.isEmpty(text) && !(oldText.contains(text))) {
            if (mHistoryKeywords.size() > 5) {//最多保存条数
                return;
            }
            SharedPreferences.Editor editor = mPref.edit();
            editor.putString(KEY_SEARCH_HISTORY_KEYWORD, text + "," + oldText);
            editor.commit();
            mHistoryKeywords.add(0, text);
        }
        mArrAdapter.notifyDataSetChanged();
    }

    /**
     * 清除历史纪录
     */
    public void cleanHistory() {
        // 创建构建器
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 设置参数
        builder.setTitle("提示")
                .setMessage("确定清空历史搜索记录？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {// 积极

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = mPref.edit();
                        editor.remove(KEY_SEARCH_HISTORY_KEYWORD).commit();
                        mHistoryKeywords.clear();
                        mArrAdapter.notifyDataSetChanged();
                        mSearchHistoryLl.setVisibility(View.GONE);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {// 消极

            @Override
            public void onClick(DialogInterface dialog, int which) {
             dialog.dismiss();
            }
        });
        builder.create().show();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_search:
                String keywords = cet_search_word.getText().toString();
                if (!TextUtils.isEmpty(keywords)) {
                    save();
                    showSearchList(keywords);
                } else {
                    Toast.makeText(BookmarkSearchActivity.this, "请输入搜索内容", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.clear_history_btn:
                cleanHistory();
                break;
            default:
                break;
        }

    }

    /**
     * 显示搜索列表
     * @param keywords 搜索关键字
     */
    private void showSearchList(String keywords) {
        Intent intent = new Intent(this, BookmarkActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_SEARCH_TEXT,keywords);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }
}
