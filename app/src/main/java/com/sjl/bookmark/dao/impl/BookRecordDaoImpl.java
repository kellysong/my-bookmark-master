package com.sjl.bookmark.dao.impl;

import android.content.Context;

import com.sjl.bookmark.dao.db.BaseDao;
import com.sjl.bookmark.dao.BookRecordDao;
import com.sjl.bookmark.entity.zhuishu.table.BookRecord;

/**
 * 阅读记录dao服务
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookRecordDaoImpl.java
 * @time 2018/12/3 16:20
 * @copyright(C) 2018 song
 */
public class BookRecordDaoImpl extends BaseDao<BookRecord> {

    public BookRecordDaoImpl(Context context) {
        super(context);
    }


    public void deleteBookRecord(String id) {
        daoSession.getDao(BookRecord.class)
                .queryBuilder()
                .where(BookRecordDao.Properties.BookId.eq(id))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities();//将查询到的符合条件的删除
    }

    public void saveBookRecord(BookRecord bookRecord) {
        createOrUpdate(bookRecord);
    }

    /**
     * 获取书籍阅读记录
     * @param bookId
     * @return
     */
    public BookRecord getBookRecord(String bookId) {
        return (BookRecord) daoSession.getDao(BookRecord.class)
                .queryBuilder()
                .where(BookRecordDao.Properties.BookId.eq(bookId))
                .unique();
    }
}
