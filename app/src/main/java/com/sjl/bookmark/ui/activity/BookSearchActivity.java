package com.sjl.bookmark.ui.activity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sjl.bookmark.R;
import com.sjl.bookmark.entity.zhuishu.SearchBookDto;
import com.sjl.bookmark.ui.adapter.BookKeyWordAdapter;
import com.sjl.bookmark.ui.adapter.RecyclerViewDivider;
import com.sjl.bookmark.ui.adapter.SearchBookAdapter;
import com.sjl.bookmark.ui.contract.BookSearchContract;
import com.sjl.bookmark.ui.presenter.BookSearchPresenter;
import com.sjl.core.mvp.BaseActivity;
import com.sjl.core.util.ViewUtils;
import com.sjl.core.widget.RefreshLayout;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;

import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import me.gujun.android.taggroup.TagGroup;

/**
 * 书籍搜索
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookSearchActivity.java
 * @time 2018/11/30 16:58
 * @copyright(C) 2018 song
 */
public class BookSearchActivity extends BaseActivity<BookSearchPresenter> implements BookSearchContract.View {
    private static final int TAG_LIMIT = 8;

    @BindView(R.id.search_iv_back)
    ImageView mIvBack;
    @BindView(R.id.search_et_input)
    EditText mEtInput;
    @BindView(R.id.search_iv_delete)
    ImageView mIvDelete;
    @BindView(R.id.search_iv_search)
    ImageView mIvSearch;
    @BindView(R.id.search_book_tv_refresh_hot)
    TextView mTvRefreshHot;
    @BindView(R.id.search_tg_hot)
    TagGroup mTgHot;
    /*    @BindView(R.id.search_rv_history)
        RecyclerView mRvHistory;*/
    @BindView(R.id.refresh_layout)
    RefreshLayout mRefreshLayout;
    @BindView(R.id.refresh_rv_content)
    RecyclerView mRecyclerView;

    private BookKeyWordAdapter mKeyWordAdapter;
    private SearchBookAdapter mSearchAdapter;
    /**
     * true显示关键字搜索自动补全列表，false显示搜索书籍列表
     */
    private boolean contentFlag = true;

    private boolean isTag;
    private List<String> mHotTagList;
    private int mTagStart = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.book_search_activity;
    }

    @Override
    protected void initView() {
        mRefreshLayout.setBackground(ContextCompat.getDrawable(this, R.color.white));

    }

    @Override
    protected void initListener() {
        //退出
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //输入框
        mEtInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().equals("")) {
                    //隐藏delete按钮和关键字显示内容
                    if (mIvDelete.getVisibility() == View.VISIBLE) {
                        mIvDelete.setVisibility(View.INVISIBLE);
                        mRefreshLayout.setVisibility(View.INVISIBLE);
                        //删除全部视图
                        mKeyWordAdapter.getDatas().clear();
                        mSearchAdapter.getDatas().clear();
                        mRecyclerView.removeAllViews();
                    }
                    return;
                }
                //由原来隐藏变可见，显示delete按钮
                if (mIvDelete.getVisibility() == View.INVISIBLE) {
                    mIvDelete.setVisibility(View.VISIBLE);
                    mRefreshLayout.setVisibility(View.VISIBLE);
                    //默认是显示完成状态
                    mRefreshLayout.showFinish();
                }
                //搜索
                String query = s.toString().trim();
                if (isTag) {
                    contentFlag = false;
                    mRefreshLayout.showLoading();
                    mPresenter.searchBook(query);
                    isTag = false;
                } else {
                    //传递
                    contentFlag = true;
                    mPresenter.searchKeyWord(query);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //键盘的搜索
        mEtInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //修改回车键功能
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    contentFlag = false;
                    searchBook();
                    return true;
                }
                return false;
            }
        });

        //进行搜索
        mIvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentFlag = false;
                searchBook();
            }
        });

        //删除字
        mIvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEtInput.setText("");
                ViewUtils.toggleKeyboard(BookSearchActivity.this);
            }
        });


        //Tag的点击事件
        mTgHot.setOnTagClickListener(new TagGroup.OnTagClickListener() {
            @Override
            public void onTagClick(String tag) {
                isTag = true;
                mEtInput.setText(tag);
            }
        });

        //Tag的刷新事件
        mTvRefreshHot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshTag();
            }
        });

    }

    @Override
    protected void initData() {
        mKeyWordAdapter = new BookKeyWordAdapter(this, R.layout.search_book_keyword_recycle_item, null);
        mSearchAdapter = new SearchBookAdapter(this, R.layout.search_book_recycle_item, null);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.VERTICAL));
        //点击关键字查书
        mKeyWordAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                //显示正在加载
                contentFlag = false;
                mRefreshLayout.showLoading();
                String book = mKeyWordAdapter.getDataItem(position);
                mPresenter.searchBook(book);
                ViewUtils.toggleKeyboard(BookSearchActivity.this);
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });

        //书本的点击事件
        mSearchAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                SearchBookDto.BooksBean item = mSearchAdapter.getDataItem(position);
                String bookId = item.get_id();
                BookDetailActivity.startActivity(BookSearchActivity.this, bookId);
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });

        //默认隐藏
        mRefreshLayout.setVisibility(View.GONE);
        //获取热词
        mPresenter.searchHotWord();

    }

    @Override
    public void finishHotWords(List<String> hotWords) {
        mHotTagList = hotWords;
        refreshTag();
    }


    @Override
    public void finishKeyWords(List<String> keyWords) {
        if (keyWords == null || keyWords.size() == 0) mRefreshLayout.setVisibility(View.INVISIBLE);
        //BookKeyWordAdapter、SearchBookAdapter共用一个RecyclerView,先设置适配器在刷新
        if (contentFlag) {
            //设置mRefreshLayout、mRecyclerView显示，否则搜索书籍搜索列表时导致mRecyclerView隐藏
            mRefreshLayout.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mRecyclerView.setAdapter(mKeyWordAdapter);
            mKeyWordAdapter.refreshItems(keyWords);

        }
    }

    @Override
    public void finishBooks(List<SearchBookDto.BooksBean> books) {
        if (books == null || books.size() == 0) {
            mRefreshLayout.showEmpty();
        } else {
            //显示完成
            mRefreshLayout.showFinish();
        }
        //加载
        if (!contentFlag) {
            mRecyclerView.setAdapter(mSearchAdapter);
            mSearchAdapter.refreshItems(books);

        }
    }

    @Override
    public void errorBooks() {
        mRefreshLayout.showEmpty();
    }

    /**
     * 换一批
     */
    private void refreshTag() {
        if (mHotTagList == null){
            return;
        }

        int last = mTagStart + TAG_LIMIT;
        if (mHotTagList.size() <= last) {
            mTagStart = 0;
            if (mHotTagList.size() < TAG_LIMIT){
                last = mTagStart + mHotTagList.size();
            }else {
                last = mTagStart + TAG_LIMIT;
            }
            showShortToast(getString(R.string.no_load_more));
        }
        List<String> tags = mHotTagList.subList(mTagStart, last);
        mTgHot.setTags(tags);//设置到控件TagGroup
        mTagStart += TAG_LIMIT;
    }

    /**
     * 书籍查询
     */
    private void searchBook() {
        String query = mEtInput.getText().toString().trim();
        if (!query.equals("")) {
            mRefreshLayout.setVisibility(View.VISIBLE);
            //显示正在加载
            mRefreshLayout.showLoading();
            mPresenter.searchBook(query);
            ViewUtils.toggleKeyboard(this);
        }
    }

}
