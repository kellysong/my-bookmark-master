package com.sjl.bookmark.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sjl.bookmark.R;
import com.sjl.bookmark.dao.impl.DaoFactory;
import com.sjl.bookmark.entity.zhuishu.BookDetailDto;
import com.sjl.bookmark.entity.zhuishu.HotCommentDto;
import com.sjl.bookmark.entity.zhuishu.table.CollectBook;
import com.sjl.bookmark.entity.zhuishu.table.RecommendBook;
import com.sjl.bookmark.kotlin.language.I18nUtils;
import com.sjl.bookmark.net.HttpConstant;
import com.sjl.bookmark.ui.adapter.BookHotCommentAdapter;
import com.sjl.bookmark.ui.adapter.BookListAdapter;
import com.sjl.bookmark.ui.adapter.RecyclerViewDivider;
import com.sjl.bookmark.ui.contract.BookDetailContract;
import com.sjl.bookmark.ui.presenter.BookDetailPresenter;
import com.sjl.core.mvp.BaseActivity;
import com.sjl.core.net.RxBus;
import com.sjl.core.net.RxSchedulers;
import com.sjl.core.net.RxVoid;
import com.sjl.core.util.datetime.TimeUtils;
import com.sjl.core.util.log.LogUtils;
import com.sjl.core.widget.RefreshLayout;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;

import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import io.reactivex.functions.Consumer;

/**
 * 书籍详情
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookDetailActivity.java
 * @time 2018/12/2 21:07
 * @copyright(C) 2018 song
 */
public class BookDetailActivity extends BaseActivity<BookDetailPresenter> implements BookDetailContract.View {
    public static final String RESULT_IS_COLLECTED = "result_is_collected";

    public static final String EXTRA_BOOK_ID = "extra_book_id";

    @BindView(R.id.common_toolbar)
    Toolbar mToolBar;
    @BindView(R.id.refresh_layout)
    RefreshLayout mRefreshLayout;
    @BindView(R.id.nsv_content)
    NestedScrollView mNestedScrollView;
    @BindView(R.id.book_detail_iv_cover)
    ImageView mIvCover;
    @BindView(R.id.book_detail_tv_title)
    TextView mTvTitle;
    @BindView(R.id.book_detail_tv_author)
    TextView mTvAuthor;
    @BindView(R.id.book_detail_tv_type)
    TextView mTvType;
    @BindView(R.id.book_detail_tv_word_count)
    TextView mTvWordCount;
    @BindView(R.id.book_detail_tv_lately_update)
    TextView mTvLatelyUpdate;
    @BindView(R.id.book_list_tv_chase)
    TextView mTvChase;
    @BindView(R.id.book_detail_tv_read)
    TextView mTvRead;
    @BindView(R.id.book_detail_tv_follower_count)
    TextView mTvFollowerCount;
    @BindView(R.id.book_detail_tv_retention)
    TextView mTvRetention;
    @BindView(R.id.book_detail_tv_day_word_count)
    TextView mTvDayWordCount;
    @BindView(R.id.book_detail_tv_brief)
    TextView mTvBrief;
    @BindView(R.id.book_detail_tv_more_comment)
    TextView mTvMoreComment;
    @BindView(R.id.book_detail_rv_hot_comment)
    RecyclerView mRvHotComment;
    @BindView(R.id.book_list_tv_recommend_book_list)
    TextView mTvRecommendBookList;
    @BindView(R.id.book_detail_rv_recommend_book_list)
    RecyclerView mRvRecommendBookList;

    private BookHotCommentAdapter mHotCommentAdapter;
    private BookListAdapter mBookListAdapter;
    private CollectBook mCollBookBean;
    private ProgressDialog mProgressDialog;
    private String mBookId;
    private boolean isBriefOpen = false;
    private boolean isCollected = false;

    @Override
    protected int getLayoutId() {
        return R.layout.book_detail_activity;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {
        bindingToolbar(mToolBar, I18nUtils.getString(R.string.title_book_detail));
        //简介，可伸缩的TextView
        mTvBrief.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBriefOpen) {
                    mTvBrief.setMaxLines(4);
                    isBriefOpen = false;
                } else {
                    mTvBrief.setMaxLines(8);
                    isBriefOpen = true;
                }
            }
        });

        //追更和放弃
        mTvChase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCollected) {//已经收藏有，点击放弃
                    //从数据库删除
                    DaoFactory.getCollectBookDao().deleteCollBookInRx(mCollBookBean)
                            .compose(RxSchedulers.<RxVoid>applySingle())
                            .as(BookDetailActivity.this.<RxVoid>bindLifecycle())
                            .subscribe(new Consumer<RxVoid>() {
                                @Override
                                public void accept(RxVoid rxVoid) throws Exception {
                                    LogUtils.i("删除收藏成功");
                                    BaseActivity activity = getActivity(BookReadActivity.class);
                                    if (activity != null) {
                                        activity.finish();
                                    }
                                    RxBus.getInstance().post(false);//更新书架
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    LogUtils.e("删除收藏异常", throwable);
                                }
                            });

                    mTvChase.setText(getResources().getString(R.string.nb_book_detail_chase_update));

                    //修改背景
                    Drawable drawable = getResources().getDrawable(R.drawable.selector_btn_book_list);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        mTvChase.setBackground(drawable);
                    } else {
                        mTvChase.setBackgroundDrawable(drawable);
                    }
                    //设置图片
                    mTvChase.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(BookDetailActivity.this, R.mipmap.ic_book_list_add), null,
                            null, null);

                    isCollected = false;
                } else {//没有收藏，添加收藏
                    mPresenter.addToBookShelf(mCollBookBean);
                    mTvChase.setText(getResources().getString(R.string.nb_book_detail_give_up));

                    //修改背景
                    Drawable drawable = getResources().getDrawable(R.drawable.shape_common_gray_corner);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        mTvChase.setBackground(drawable);
                    } else {
                        mTvChase.setBackgroundDrawable(drawable);
                    }
                    //设置图片
                    /**
                     * setCompoundDrawables 设置图片的宽高是通过的画的drawable的宽高决定的，
                     所以，必须先使用Drawable.setBounds设置Drawable的宽高，图片才会显示,故采用setCompoundDrawablesWithIntrinsicBounds方便处理
                     */
                    mTvChase.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(BookDetailActivity.this, R.mipmap.ic_book_list_delete), null,
                            null, null);

                    isCollected = true;
                }
            }
        });
        //开始阅读
        mTvRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivityForResult(new Intent(BookDetailActivity.this, BookReadActivity.class)
//                        .putExtra(BookReadActivity.EXTRA_IS_COLLECTED, isCollected)
//                        .putExtra(BookReadActivity.EXTRA_COLL_BOOK, mCollBookBean), REQUEST_READ);

                startActivity(new Intent(BookDetailActivity.this, BookReadActivity.class)
                        .putExtra(BookReadActivity.EXTRA_IS_COLLECTED, isCollected)
                        .putExtra(BookReadActivity.EXTRA_COLL_BOOK, mCollBookBean));
            }
        });

        //更多评论
        mTvMoreComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(EXTRA_BOOK_ID, mBookId);
                openActivity(BookMoreCommentActivity.class, bundle);
            }
        });

    }

    @Override
    protected void initData() {
        mBookId = getIntent().getStringExtra(EXTRA_BOOK_ID);
        requestData();

    }

    private void requestData() {
        mRefreshLayout.showLoading();
        mPresenter.refreshBookDetail(mBookId);
    }

    /*
    * 复用Activity时的生命周期回调
    */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mBookId = getIntent().getStringExtra(EXTRA_BOOK_ID);
        if (TextUtils.isEmpty(mBookId)) {
            return;
        }
        mNestedScrollView.fling(0);//滑动到顶部
        mNestedScrollView.smoothScrollTo(0, 0);
        requestData();
    }


    public static void startActivity(Context context, String bookId) {
        Intent intent = new Intent(context, BookDetailActivity.class);
        intent.putExtra(EXTRA_BOOK_ID, bookId);
        context.startActivity(intent);
    }

    @Override
    public void finishRefresh(BookDetailDto.BookDetail bookDetail) {
        //封面
        Glide.with(this)
                .load(HttpConstant.ZHUISHU_IMG_BASE_URL + bookDetail.getCover())
                .placeholder(R.drawable.ic_book_loading)
                .error(R.mipmap.ic_load_error)
                .centerCrop()
                .into(mIvCover);

        //书名
        mTvTitle.setText(bookDetail.getTitle());
        //作者
        mTvAuthor.setText(bookDetail.getAuthor());
        //类型
        mTvType.setText("|" + bookDetail.getMajorCate());

        //总字数
        mTvWordCount.setText(getResources().getString(R.string.nb_book_word, bookDetail.getWordCount() / 10000));
        //更新时间
        mTvLatelyUpdate.setText(TimeUtils.dateConvert(bookDetail.getUpdated(), TimeUtils.DATE_FORMAT_7));
        //追书人数
        mTvFollowerCount.setText(bookDetail.getFollowerCount() + "");
        //存留率
        mTvRetention.setText(bookDetail.getRetentionRatio() + "%");
        //日更字数
        mTvDayWordCount.setText(bookDetail.getSerializeWordCount() + "");
        //简介
        mTvBrief.setText(bookDetail.getLongIntro());

        mCollBookBean = DaoFactory.getCollectBookDao().getCollectBook(bookDetail.get_id());

        //判断是否收藏
        if (mCollBookBean != null) {
            isCollected = true;

            mTvChase.setText(getResources().getString(R.string.nb_book_detail_give_up));
            //修改背景
            Drawable drawable = getResources().getDrawable(R.drawable.shape_common_gray_corner);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mTvChase.setBackground(drawable);
            } else {
                mTvChase.setBackgroundDrawable(drawable);
            }            //设置图片
            mTvChase.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.mipmap.ic_book_list_delete), null,
                    null, null);
            mTvRead.setText(R.string.nb_book_detail_continue_read);
        } else {
            isCollected = false;
            mTvChase.setText(getResources().getString(R.string.nb_book_detail_chase_update));
            //修改背景
            Drawable drawable = getResources().getDrawable(R.drawable.selector_btn_book_list);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mTvChase.setBackground(drawable);
            } else {
                mTvChase.setBackgroundDrawable(drawable);
            }            //设置图片
            mTvChase.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(BookDetailActivity.this, R.mipmap.ic_book_list_add), null,
                    null, null);
            mTvRead.setText(R.string.nb_book_detail_start_read);

            mCollBookBean = bookDetail.getCollBookBean();
        }
    }

    @Override
    public void finishHotComment(List<HotCommentDto.HotComment> hotCommentList) {
        if (hotCommentList.isEmpty()) {
            return;
        }
        mHotCommentAdapter = new BookHotCommentAdapter(this, R.layout.bookdetail_hot_comment_recycle_item, hotCommentList);
        mRvHotComment.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                //RecyclerView与外部ScrollView滑动冲突
                return false;
            }
        });
        mRvHotComment.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.VERTICAL));
        mRvHotComment.setNestedScrollingEnabled(false);//禁止RecyclerView嵌套滑动，防止NestedScrollView滑动不流畅

        mRvHotComment.setAdapter(mHotCommentAdapter);
    }

    @Override
    public void finishRecommendBookList(final List<RecommendBook> recommendBookList) {
        if (recommendBookList == null || recommendBookList.isEmpty()) {
            return;
        }
        //推荐书籍
        mBookListAdapter = new BookListAdapter(this, R.layout.bookdetail_like_recycle_item, recommendBookList);
        mRvRecommendBookList.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                //RecyclerView与外部ScrollView滑动冲突
                return false;
            }
        });
        mRvRecommendBookList.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.VERTICAL));
        mRvRecommendBookList.setAdapter(mBookListAdapter);
        mBookListAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                RecommendBook dataItem = mBookListAdapter.getItem(position);
                BookDetailActivity.startActivity(BookDetailActivity.this, dataItem.getRecommendId());
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
    }


    @Override
    public void waitToBookShelf() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setTitle(getString(R.string.book_detail_add_shelf_hint));
        }
        mProgressDialog.show();
    }

    @Override
    public void errorToBookShelf() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        showShortToast(getString(R.string.book_detail_add_shelf_hint2));
    }

    @Override
    public void succeedToBookShelf() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        showShortToast(getString(R.string.book_detail_add_shelf_hint3));
        //更新书架
        RxBus.getInstance().post(true);
    }

    @Override
    public void showError() {
        mRefreshLayout.showError();
    }

    @Override
    public void complete() {
        mRefreshLayout.showFinish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //如果进入阅读页面收藏了，页面结束的时候，就需要返回改变收藏按钮
//        if (requestCode == REQUEST_READ) {
//            if (data == null) {
//                return;
//            }
//
//            isCollected = data.getBooleanExtra(RESULT_IS_COLLECTED, false);
//
//            if (isCollected) {
//                mTvChase.setText(getResources().getString(R.string.nb_book_detail_give_up));
//                //修改背景
//                Drawable drawable = getResources().getDrawable(R.drawable.shape_common_gray_corner);
//                mTvChase.setBackground(drawable);
//                //设置图片
//                mTvChase.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.mipmap.ic_book_list_delete), null,
//                        null, null);
//                mTvRead.setText("继续阅读");
//            }
//        }
    }
}
