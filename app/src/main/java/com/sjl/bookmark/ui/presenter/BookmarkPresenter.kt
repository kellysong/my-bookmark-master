package com.sjl.bookmark.ui.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.sjl.bookmark.dao.impl.BookmarkService;
import com.sjl.bookmark.entity.table.Bookmark;
import com.sjl.bookmark.ui.activity.BookmarkSearchActivity;
import com.sjl.bookmark.ui.adapter.BookmarkAdapter;
import com.sjl.bookmark.ui.contract.BookmarkContract;
import com.sjl.core.util.log.LogUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookmarkPresenter.java
 * @time 2018/1/29 15:44
 * @copyright(C) 2018 song
 */
public class BookmarkPresenter extends BookmarkContract.Presenter {
    private Handler handler;
    private List<Bookmark> bookmarkList;
    private boolean isHasMore = true;//false没有更多数据，true还有
    private boolean isLock;
    private int currentPage = 1;
    private static final int PAGE_SIZE = 20;
    private String keyWord = "";

    @Override
    public void init(Intent intent) {
        handler = new Handler();
        bookmarkList = new ArrayList<>();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            keyWord = bundle.getString(BookmarkSearchActivity.KEY_SEARCH_TEXT);
            if (!TextUtils.isEmpty(keyWord)) {
                mView.setItemMenuVisible(false);
            }
        }
    }


    /**
     * 初始化数据
     */
    @Override
    public List<Bookmark> initBookmarkList() {
        LogUtils.i("当前页码：currentPage=" + currentPage + ",搜索字符：" + keyWord);
        List<Bookmark> bookmarks = BookmarkService.getInstance(mContext).queryBookmarkByPage(keyWord, keyWord, currentPage, PAGE_SIZE);
        bookmarkList.addAll(bookmarks);
        return bookmarks;
    }

    /**
     * 上拉加载
     */
    @Override
    public void pullRefreshUp() {
        LogUtils.i("滚动加锁标志：isLock=" + isLock);
        synchronized (this) {
            if (!isLock) {//锁定防止多次滚动
                isLock = true;
                if (isHasMore) {
                    currentPage++;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            List<Bookmark> bookmarks = initBookmarkList();
                            mView.showBookmarkData(bookmarkList, getLoadState(bookmarks));
                            isLock = false;
                        }
                    }, 500);
                }
            }
        }

    }

    /**
     * 下拉刷新
     */
    @Override
    public void pullRefreshDown() {
        currentPage = 1;
        isLock = false;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                bookmarkList.clear();
                List<Bookmark> bookmarks = initBookmarkList();
                if (mView != null){
                    mView.showBookmarkData(bookmarkList, getLoadState(bookmarks));
                }
            }
        }, 1000);
    }

    /**
     * @param bookmarks 查询返回集合
     * @return
     */
    public int getLoadState(List<Bookmark> bookmarks) {
        if (bookmarks != null && bookmarks.size() > 0) {
            if (bookmarks.size() < PAGE_SIZE) {
                LogUtils.i("没有更多数据了");
                isHasMore = false;
                return BookmarkAdapter.NO_LOAD_MORE;
            } else {
                isHasMore = true;
                return BookmarkAdapter.LOADING_MORE;
            }
        } else {
            isHasMore = false;
            return BookmarkAdapter.NO_LOAD_MORE;
        }
    }

}
