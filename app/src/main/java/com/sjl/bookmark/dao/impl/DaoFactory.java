package com.sjl.bookmark.dao.impl;

import androidx.annotation.NonNull;

import com.sjl.bookmark.app.MyApplication;

/**
 * dao工厂
 *
 * @author Kelly
 * @version 1.0.0
 * @filename DaoFactory.java
 * @time 2018/12/5 8:52
 * @copyright(C) 2018 song
 */
public class DaoFactory {

    private DaoFactory() {

    }


    /**
     * 本地收藏
     *
     * @return
     */
    @NonNull
    public static CollectDaoImpl getCollectDao() {
        return new CollectDaoImpl(MyApplication.getContext());

    }

    /**
     * 书籍收藏
     *
     * @return
     */
    @NonNull
    public static CollectBookDaoImpl getCollectBookDao() {
        return new CollectBookDaoImpl(MyApplication.getContext());

    }

    /**
     * 书籍章节
     *
     * @return
     */
    @NonNull
    public static BookChapterDaoImpl getBookChapterDao() {
        return new BookChapterDaoImpl(MyApplication.getContext());
    }


    /**
     * 书籍阅读记录
     *
     * @return
     */
    @NonNull
    public static BookRecordDaoImpl getBookRecordDao() {
        return new BookRecordDaoImpl(MyApplication.getContext());
    }


    /**
     * 推荐书籍
     *
     * @return
     */
    @NonNull
    public static RecommendBookDaoImpl getRecommendBookDao() {
        return new RecommendBookDaoImpl(MyApplication.getContext());
    }

    /**
     * 文章浏览足迹
     *
     * @return
     */
    @NonNull
    public static BrowseTrackDaoImpl getBrowseTrackDao() {
        return new BrowseTrackDaoImpl(MyApplication.getContext());

    }
}
