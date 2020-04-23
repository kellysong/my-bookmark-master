package com.sjl.bookmark.dao.impl;

import android.content.Context;

import com.sjl.bookmark.dao.db.BaseDao;
import com.sjl.bookmark.dao.BookChapterDao;
import com.sjl.bookmark.entity.zhuishu.table.BookChapter;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookChapterDaoImpl.java
 * @time 2018/12/1 20:14
 * @copyright(C) 2018 song
 */
public class BookChapterDaoImpl extends BaseDao<BookChapter> {
    public BookChapterDaoImpl(Context context) {
        super(context);
    }


    /**
     * 保存每一本书的章节
     * @param bookChapters
     */
    public void saveBookChapters(List<BookChapter> bookChapters) {
        batchInsert(bookChapters);
    }

    /**
     * 删除书籍下的所有章节
     * @param bookId
     */
    public void deleteBookChapter(String bookId){
        daoSession.getDao(BookChapter.class)
                .queryBuilder()
                .where(BookChapterDao.Properties.BookId.eq(bookId))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities();
    }

    /**
     * 异步保存BookChapter
     * @param bookChapterList
     */
    public void saveBookChaptersWithAsync(List<BookChapter> bookChapterList) {
        batchInsertAsync(bookChapterList);
    }

    /**
     * 获取书籍章节列表
     * @param bookId
     * @return
     */
    public Single<List<BookChapter>> getBookChaptersInRx(final String bookId){
        return Single.create(new SingleOnSubscribe<List<BookChapter>>() {
            @Override
            public void subscribe(SingleEmitter<List<BookChapter>> e) throws Exception {
                List<BookChapter> beans = daoSession
                        .getBookChapterDao()
                        .queryBuilder()
                        .where(BookChapterDao.Properties.BookId.eq(bookId))
                        .list();
                e.onSuccess(beans);
            }
        });
    }
}
