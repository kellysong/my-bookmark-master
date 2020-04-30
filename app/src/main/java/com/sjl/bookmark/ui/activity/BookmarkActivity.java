package com.sjl.bookmark.ui.activity;

import android.content.Intent;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sjl.bookmark.R;
import com.sjl.bookmark.entity.table.Bookmark;
import com.sjl.bookmark.kotlin.language.I18nUtils;
import com.sjl.bookmark.ui.adapter.BookmarkAdapter;
import com.sjl.bookmark.ui.adapter.RecyclerViewDivider;
import com.sjl.bookmark.ui.contract.BookmarkContract;
import com.sjl.bookmark.ui.presenter.BookmarkPresenter;
import com.sjl.core.mvp.BaseActivity;
import com.sjl.core.util.log.LogUtils;

import java.util.List;

import butterknife.BindView;


/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookmarkActivity.java
 * @time 2018/1/29 16:13
 * @copyright(C) 2018 song
 */
public class BookmarkActivity extends BaseActivity<BookmarkPresenter> implements BookmarkContract.View {
    @BindView(R.id.common_toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayoutManager mLinearLayoutManager;
    private BookmarkAdapter mBookmarkAdapter;
    private MenuItem menuItem;
    private boolean menuItemVisible = true;

    @BindView(R.id.suspension_bar)
    LinearLayout mSuspensionBar;
    @BindView(R.id.tv_title)
    TextView mTitle;
    private int mCurrentPosition = 0;

    private int mSuspensionHeight;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bookmark;
    }

    @Override
    public void initView() {
        toolbar.setTitle(I18nUtils.getString(R.string.google_bookmark));
        setSupportActionBar(toolbar);
        //关键下面两句话，设置了回退按钮，及点击事件的效果
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * oncreate方法之后触发
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuItem = menu.getItem(0);
        if (!menuItemVisible) {
            toolbar.setTitle(I18nUtils.getString(R.string.title_search_result));
            menuItem.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify item_bookmark_title parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            startActivity(new Intent(this, BookmarkSearchActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void initListener() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.blueStatus);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {//下拉刷新
                mPresenter.pullRefreshDown();
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //LogUtils.i("StateChanged = " + newState);
                mSuspensionHeight = mSuspensionBar.getHeight();

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //下面是悬浮标题
                if (mBookmarkAdapter.getItemViewType(mCurrentPosition ) == BookmarkAdapter.TYPE_HEADER) {
                    View view = mLinearLayoutManager.findViewByPosition(mCurrentPosition);
                    if (view != null) {
                        if (view.getTop() <= mSuspensionHeight) {
                            mSuspensionBar.setY(-(mSuspensionHeight - view.getTop()));
                        } else {
                            mSuspensionBar.setY(0);
                        }
                    }
                }

                if (mCurrentPosition != mLinearLayoutManager.findFirstVisibleItemPosition()) {
                    mCurrentPosition = mLinearLayoutManager.findFirstVisibleItemPosition();
                    mSuspensionBar.setY(0);
                    updateSuspensionBar();
                }


                //下面是更多加载条
//                LogUtils.i("onScrolled");
                int lastVisibleItemPosition = mLinearLayoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition + 1 == mBookmarkAdapter.getItemCount()) {//上拉加载更多
                    boolean isRefreshing = swipeRefreshLayout.isRefreshing();
                    if (isRefreshing) {
                        mBookmarkAdapter.notifyItemRemoved(mBookmarkAdapter.getItemCount());
                        return;
                    }
                    mPresenter.pullRefreshUp();
                }


            }
        });
    }

    private void updateSuspensionBar() {
        mTitle.setText(mBookmarkAdapter.getTitle(mCurrentPosition));
    }


    @Override
    public void initData() {
        mPresenter.init(getIntent());


        List<Bookmark> bookmarks = mPresenter.initBookmarkList();
        mBookmarkAdapter = new BookmarkAdapter(this, bookmarks);
        mBookmarkAdapter.setLoadingState(mPresenter.getLoadState(bookmarks));

        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        //添加动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());


        //添加分割线
        // recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));//api25之后才有
        mRecyclerView.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.VERTICAL));


    /*    PinnedHeaderDecoration pinnedHeaderDecoration = new PinnedHeaderDecoration();
        //设置只有RecyclerItem.ITEM_HEADER的item显示标签
        pinnedHeaderDecoration.registerTypePinnedHeader(0, new PinnedHeaderDecoration.PinnedHeaderCreator() {
            @Override
            public boolean create(RecyclerView parent, int adapterPosition) {
                return true;
            }
        });


       mRecyclerView.addItemDecoration(pinnedHeaderDecoration);*/


        if (bookmarks.size() > 0){
            Bookmark bookmark = bookmarks.get(0);
            int type = bookmark.getType();
            if (type == 1){//修复没有悬浮标题数据时吗，遮挡条目问题
                bookmarks.add(0,new Bookmark(0,bookmark.getTitle()));//追加一条悬浮标题数据
            }
            mSuspensionBar.setVisibility(View.VISIBLE);//修复没有数据时显示悬浮条目问题
            updateSuspensionBar();
        }else {
            mSuspensionBar.setVisibility(View.GONE);
        }
        mRecyclerView.setAdapter(mBookmarkAdapter);
    }


    @Override
    public void showBookmarkData(List<Bookmark> bookmarks, int loadingState) {//上拉加载更多
        LogUtils.i("当前加载状态：" + loadingState);
        mBookmarkAdapter.setLoadingState(loadingState);//注意此处
        mBookmarkAdapter.setData(bookmarks);
        swipeRefreshLayout.setRefreshing(false);
        //加载更多的效果可以通过item_foot.xml自定义，滑动到最后一项时显示该item并执行加载更多，当加载数据完毕时需要将该item移除掉
        mBookmarkAdapter.notifyItemRemoved(mBookmarkAdapter.getItemCount());
    }

    @Override
    public void setItemMenuVisible(boolean visible) {
        this.menuItemVisible = visible;
    }


}
