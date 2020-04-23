package com.sjl.bookmark.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.sjl.bookmark.R;
import com.sjl.bookmark.entity.Article;
import com.sjl.bookmark.entity.Category;
import com.sjl.bookmark.entity.HotKey;
import com.sjl.bookmark.net.HttpConstant;
import com.sjl.bookmark.ui.adapter.ArticleAdapter;
import com.sjl.bookmark.ui.contract.ArticleSearchContract;
import com.sjl.bookmark.ui.presenter.ArticleSearchPresenter;
import com.sjl.core.mvp.BaseActivity;
import com.sjl.core.util.log.LogUtils;
import com.sjl.core.util.SnackbarUtils;
import com.sjl.core.util.ViewUtils;
import com.sjl.core.widget.FlowLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 文章搜索
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ArticleSearchActivity.java
 * @time 2018/3/30 16:00
 * @copyright(C) 2018 song
 */
public class ArticleSearchActivity extends BaseActivity<ArticleSearchPresenter> implements ArticleSearchContract.View, ArticleAdapter.OnItemClickListener,
        ArticleAdapter.OnItemChildClickListener,ArticleAdapter.RequestLoadMoreListener,View.OnClickListener {
    @BindView(R.id.cet_word)
    EditText etSearch;//输入框
    @BindView(R.id.btn_search)
    TextView mSearch;//取消和搜索
    //热门搜索
    @BindView(R.id.layout_hot_key)
    FlowLayout mFlowLayout;
    @BindView(R.id.ll_hot_key)
    LinearLayout llHotKey;

    //历史搜索
    @BindView(R.id.search_history_ll)
    LinearLayout historySearchKey;
    @BindView(R.id.search_history_lv)
    ListView mListView;
    @BindView(R.id.clear_history_btn)
    Button clearHistory;

    //搜索为空
    @BindView(R.id.ll_empty_view)
    LinearLayout mEmptyView;

    @BindView(R.id.rv_content)
    RecyclerView recyclerView;

    @BindView(R.id.sv_content)
    ScrollView scrollView;


    private ArticleAdapter mArticleAdapter;

    private LayoutInflater mInflater;

    public static final String KEY_SEARCH_HISTORY_KEYWORD = "key_search_history_keyword2";
    private SharedPreferences mPref;//使用SharedPreferences记录搜索历史
    private List<String> mHistoryKeywords;//记录文本
    private ArrayAdapter<String> mArrAdapter;//搜索历史适配器


    @Override
    protected int getLayoutId() {
        return R.layout.article_search_activity;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {
        mSearch.setOnClickListener(this);
        clearHistory.setOnClickListener(this);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {//没有输入
                    scrollView.setVisibility(View.VISIBLE);

                    llHotKey.setVisibility(View.VISIBLE);//显示热门搜索
                    recyclerView.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.GONE);
                    if (mHistoryKeywords.size() > 0) {
                        historySearchKey.setVisibility(View.VISIBLE);
                    } else {
                        historySearchKey.setVisibility(View.GONE);
                    }
                } else {
                    //有输入，不管是否进行了搜索操作，保持搜索框下面原貌
                    historySearchKey.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId== EditorInfo.IME_ACTION_SEARCH ||(event!=null&&event.getKeyCode()== KeyEvent.KEYCODE_ENTER)) {
                    String keywords = etSearch.getText().toString().trim();
                    if (!TextUtils.isEmpty(keywords)) {//回车搜索
                        save();
                        ViewUtils.hideKeyBoard(ArticleSearchActivity.this,etSearch);
                        mPresenter.searchData(keywords);
                    }
                    return true;
                }
                return false;
            }
        });
    }


    @Override
    protected void initData() {
        mHistoryKeywords = new ArrayList<String>();
        initSearchHistory();
        mInflater = LayoutInflater.from(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mArticleAdapter = new ArticleAdapter(R.layout.home_article_recycle_item,null);
        recyclerView.setAdapter(mArticleAdapter);
        /**设置事件监听*/
        mArticleAdapter.setOnItemClickListener(this);
        mArticleAdapter.setOnItemChildClickListener(this);

        mArticleAdapter.setOnLoadMoreListener(this, recyclerView);
        mPresenter.getHotKeyData();

    }

    @Override
    public void getHotKeySuccess(final List<HotKey> data) {
        int size = data.size();
        for (int i = 0; i < size; i++) {
            TextView tv = (TextView) mInflater.inflate(
                    R.layout.search_label_tv, mFlowLayout, false);
            tv.setText(data.get(i).getName());
            final String str = tv.getText().toString();
            final int finalI = i;
            //点击事件
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    etSearch.setText(data.get(finalI).getName());
                    // 将光标移至字符串尾部
                    CharSequence charSequence = etSearch.getText();
                    if (charSequence instanceof Spannable) {
                        Spannable spanText = (Spannable) charSequence;
                        Selection.setSelection(spanText, charSequence.length());
                    }
                    ViewUtils.hideKeyBoard(ArticleSearchActivity.this,etSearch);
                    mPresenter.searchData(etSearch.getText().toString().trim());
                }
            });
            mFlowLayout.addView(tv);
        }
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
            historySearchKey.setVisibility(View.VISIBLE);
        } else {
            historySearchKey.setVisibility(View.GONE);
        }
        mArrAdapter = new ArrayAdapter<String>(this, R.layout.item_search_history, mHistoryKeywords);
        mListView.setAdapter(mArrAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                etSearch.setText(mHistoryKeywords.get(i));
                CharSequence charSequence = etSearch.getText();
                if (charSequence instanceof Spannable) {
                    Spannable spanText = (Spannable) charSequence;
                    Selection.setSelection(spanText, charSequence.length());
                }
                ViewUtils.hideKeyBoard(ArticleSearchActivity.this,etSearch);
                mPresenter.searchData(mHistoryKeywords.get(i));
            }
        });
        mArrAdapter.notifyDataSetChanged();
    }



    /**
     * 储存搜索历史
     */
    public void save() {
        String text = etSearch.getText().toString();
        String oldText = mPref.getString(KEY_SEARCH_HISTORY_KEYWORD, "");
        LogUtils.i("oldText:"+oldText+","+text+",oldText.contains(text):"+oldText.contains(text));
        if (!TextUtils.isEmpty(text) && !(oldText.contains(text))) {
            if (mHistoryKeywords.size() > 20) {//最多保存条数
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
                        historySearchKey.setVisibility(View.GONE);
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
    public void showFailMsg(String message) {
        SnackbarUtils.makeShort(getWindow().getDecorView(),message).danger();
    }

    @Override
    public void searchDataSuccess(List<Article.DatasBean> data) {
        if (data == null || data.size() == 0) {//没有搜索到数据
//            llHotKey.setVisibility(BaseView.GONE);
//            historySearchKey.setVisibility(BaseView.GONE);
            scrollView.setVisibility(View.GONE);

            recyclerView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);

        } else {
//            llHotKey.setVisibility(BaseView.GONE);
//            historySearchKey.setVisibility(BaseView.GONE);
            scrollView.setVisibility(View.GONE);

            recyclerView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
            mArticleAdapter.setNewData(data);
        }

    }


    @Override
    public void loadMoreDataSuccess(List<Article.DatasBean> data) {
        if (data == null || data.size() == 0) {
            mArticleAdapter.loadMoreEnd();
        } else {
            mArticleAdapter.addData(data);
            mArticleAdapter.loadMoreComplete();
        }
    }


    @Override
    public void onLoadMoreRequested() {
        String keyWord = etSearch.getText().toString().trim();
        if (!TextUtils.isEmpty(keyWord)) {
            mPresenter.getMoreData(keyWord);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                finish();
                break;
            case R.id.clear_history_btn:
                cleanHistory();
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        BrowserActivity.startWithParams(this,mArticleAdapter.getItem(position).getTitle(),
                mArticleAdapter.getItem(position).getLink());
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if (view.getId() == R.id.tvChapterName) {
            Intent intent = new Intent(this, ArticleTypeActivity.class);
            intent.putExtra(HttpConstant.CONTENT_TITLE_KEY, mArticleAdapter.getItem(position).getChapterName());
            List<Category.ChildrenBean> children = new ArrayList<>();
            children.add(new Category.ChildrenBean(mArticleAdapter.getItem(position).getChapterId(),
                    mArticleAdapter.getItem(position).getChapterName()));
            intent.putParcelableArrayListExtra(HttpConstant.CONTENT_CHILDREN_DATA_KEY, (ArrayList<? extends Parcelable>) children);
            intent.putExtra(HttpConstant.CONTENT_OPEN_FLAG, "0");

            startActivity(intent);
        }
    }
}
