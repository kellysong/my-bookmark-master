package com.sjl.bookmark.dao.impl;

import android.content.Context;

import com.sjl.bookmark.dao.RecommendBookDao;
import com.sjl.bookmark.dao.db.BaseDao;
import com.sjl.bookmark.entity.zhuishu.table.RecommendBook;
import com.sjl.core.util.log.LogUtils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * 查询缓存的推荐书籍(本地txt书籍无推荐书籍)，防止爬虫过慢影响体验
 *
 * @author Kelly
 * @version 1.0.0
 * @filename RecommendBookDaoImpl.java
 * @time 2018/12/11 15:39
 * @copyright(C) 2018 song
 */
public class RecommendBookDaoImpl extends BaseDao<RecommendBook> {
    public RecommendBookDaoImpl(Context context) {
        super(context);
    }

    /**
     * 查询bookId下推荐的书籍
     *
     * @param bookId
     * @return
     */
    public List<RecommendBook> queryRecommendBookByBookId(String bookId) {
        QueryBuilder<RecommendBook> queryBuilder = daoSession.getRecommendBookDao()
                .queryBuilder()
                .where(RecommendBookDao.Properties.BookId.eq(bookId));
        return queryWithParams(queryBuilder);
    }

    /**
     * 查询bookId下推荐的书籍
     *
     * @param recommendBookList
     * @return
     */
    public void saveRecommendBookByBookId(List<RecommendBook> recommendBookList) {
        boolean batchInsert = batchInsert(recommendBookList);
        LogUtils.i("saveRecommendBookByBookId batchInsert:" + batchInsert);
    }


    /**
     * 删除bookId下推荐的书籍
     *
     * @param bookId
     * @return
     */
    public void deleteRecommendBookByBookId(String bookId) {
        List<RecommendBook> recommendBooks = queryRecommendBookByBookId(bookId);
        batchDelete(recommendBooks, RecommendBook.class);
    }
}
