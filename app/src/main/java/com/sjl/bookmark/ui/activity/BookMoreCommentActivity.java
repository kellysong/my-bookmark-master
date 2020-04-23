package com.sjl.bookmark.ui.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.sjl.bookmark.R;
import com.sjl.bookmark.api.ZhuiShuShenQiApi;
import com.sjl.bookmark.entity.zhuishu.HotCommentDto;
import com.sjl.bookmark.kotlin.language.I18nUtils;
import com.sjl.bookmark.ui.adapter.BookHotCommentAdapter;
import com.sjl.bookmark.ui.adapter.RecyclerViewDivider;
import com.sjl.core.mvp.BaseActivity;
import com.sjl.core.net.RetrofitHelper;
import com.sjl.core.net.RxSchedulers;
import com.sjl.core.util.log.LogUtils;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.zhy.adapter.recyclerview.wrapper.LoadMoreWrapper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.functions.Consumer;


/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookMoreCommentActivity.java
 * @time 2018/12/9 13:57
 * @copyright(C) 2018 song
 */
public class BookMoreCommentActivity extends BaseActivity {
    @BindView(R.id.common_toolbar)
    Toolbar mToolBar;
    @BindView(R.id.rvHomeArticles)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    private String mBookId;
    private int mPage = 1;
    private BookHotCommentAdapter mHotCommentAdapter;
    private LoadMoreWrapper mLoadMoreWrapper;

    private List<HotCommentDto.HotComment> datas;
    private boolean pullDownFlag = true;
    @Override
    protected int getLayoutId() {
        return R.layout.bookmore_comment_activity;
    }

    @Override
    protected void initView() {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.blueStatus);
    }

    @Override
    protected void initListener() {
        bindingToolbar(mToolBar, I18nUtils.getString(R.string.title_book_comment));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage = 1;
                pullDownFlag = true;
                requestData();
            }
        });
    }

    @Override
    protected void initData() {
        datas = new ArrayList<>();
        mBookId = getIntent().getStringExtra(BookDetailActivity.EXTRA_BOOK_ID);
        mRecyclerView.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.VERTICAL));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));//必须设置否则数据不显示
        mHotCommentAdapter = new BookHotCommentAdapter(this, R.layout.bookdetail_hot_comment_recycle_item, datas);
        mLoadMoreWrapper = new LoadMoreWrapper(mHotCommentAdapter);
        mLoadMoreWrapper.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {//自动触发一次
                pullDownFlag = false;
                requestData();
            }
        });

        mRecyclerView.setAdapter(mLoadMoreWrapper);
    }

    private void requestData() {
        ZhuiShuShenQiApi zhuiShuShenQiApi = RetrofitHelper.getInstance().getApiService(ZhuiShuShenQiApi.class);
        mPage=20;//目前接口不支持分页，暂时取前面20条评论
        zhuiShuShenQiApi.getMoreComment2(mBookId, mPage)
                .compose(RxSchedulers.<HotCommentDto>applySingle())
//                .as(BookMoreCommentActivity.this.<HotCommentDto>bindLifecycle())//TODO:不知道为啥报错
                .as(AutoDispose.<HotCommentDto>autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new Consumer<HotCommentDto>() {
                    @Override
                    public void accept(HotCommentDto hotCommentDto) throws Exception {
                        if (hotCommentDto.ok) {
                            List<HotCommentDto.HotComment> reviews = hotCommentDto.getReviews();
                            if (reviews != null && reviews.size() > 0) {
                                if (pullDownFlag){//下拉
                                    addData(true,reviews);
                                }else{
                                    //局部刷新
                                    addData(false,reviews);
                                }
                                mLoadMoreWrapper.loadMoreEnd();//结束加载

//                                mLoadMoreWrapper.loadMoreCompleted();
//                                mPage++;
                            }else{
                                mLoadMoreWrapper.loadMoreEnd();
                            }
                        } else {
                            mLoadMoreWrapper.loadMoreEnd();
                        }
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("书评获取异常",throwable);
                        mSwipeRefreshLayout.setRefreshing(false);
                        mLoadMoreWrapper.loadMoreEnd();

                    }
                });
    }

    public void addData(boolean isFirstPage, List<HotCommentDto.HotComment> cells) {
        if(isFirstPage){
            datas.clear();
            datas.addAll(datas.size(), cells);
            mLoadMoreWrapper.notifyDataSetChanged();
        }else {
            datas.addAll(datas.size(), cells);
            mLoadMoreWrapper.notifyItemRangeChanged(datas.size(), datas.size() + cells.size());
        }
    }

}
