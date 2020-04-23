package com.sjl.bookmark.dao.impl;

import android.content.Context;
import android.text.TextUtils;

import com.sjl.bookmark.dao.BookmarkDao;
import com.sjl.bookmark.dao.db.DatabaseManager;
import com.sjl.bookmark.entity.table.Bookmark;
import com.sjl.core.util.log.LogUtils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * 书签服务
 */
public class BookmarkService {
    private static BookmarkService instance;
    private BookmarkDao bookmarkDao;

    private BookmarkService(Context context) {
        this.bookmarkDao =  DatabaseManager.getInstance(context).getDaoSession().getBookmarkDao();
    }


    public static BookmarkService getInstance(Context context) {
        if (instance == null) {
            instance = new BookmarkService(context);
        }
        return instance;
    }

    /**
     * 查询单个书签
     *
     * @param id
     * @return
     */
    public Bookmark loadBookmark(long id) {
        if (!TextUtils.isEmpty(id + "")) {
            return bookmarkDao.load(id);
        }
        return null;
    }

    /**
     * 查询所有书签
     *
     * @return
     */
    public List<Bookmark> loadAllBookmark() {
        return bookmarkDao.loadAll();
    }

    /**
     * 生成按id倒排序的列表
     *
     * @return
     */
    public List<Bookmark> loadAllBookmarkByOrder() {
        return bookmarkDao.queryBuilder().orderDesc(BookmarkDao.Properties.Id).list();
    }

    /**
     * 根据查询条件,返回数据列表
     *
     * @param where  条件
     * @param params 参数
     * @return 数据列表
     */
    public List<Bookmark> queryBookmark(String where, String... params) {
        return bookmarkDao.queryRaw(where, params);
    }

    /**
     * 分页查询书签
     * @param title 书签标题
     * @param text 书签连接文本
     * @param pageOffset 编译
     * @param pageSize 每页大小
     * @return
     */
    public List<Bookmark> queryBookmarkByPage(String title,String text,int pageOffset, int pageSize) {
        if (pageSize <= 0 || pageSize >=50) {
            pageSize = 12;
        }
        QueryBuilder<Bookmark> builder = bookmarkDao.queryBuilder();
//        WhereCondition.StringCondition condition = new WhereCondition.StringCondition("1=1");
//        builder.where(condition);
        builder.whereOr(BookmarkDao.Properties.Title.like("%" + title + "%"),BookmarkDao.Properties.Text.like("%" + text + "%"));
        //表示从第nBaseRow行(基于0的索引)(包括该行)开始,取其后的nNumRecord  条记录
        List<Bookmark> bookmarks = builder.offset((pageOffset -1) * pageSize).limit(pageSize).orderAsc(BookmarkDao.Properties.Id).list();
        return bookmarks;
    }


    /**
     * 根据实体插入（id不同时新增）或修改信息
     *
     * @param bookmark
     * @return
     */
    public long saveBookmark(Bookmark bookmark) {
        return bookmarkDao.insertOrReplace(bookmark);
    }


    /**
     * 批量插入或修改书签信息
     *
     * @param bookmarks 用户信息列表
     */
    public void saveBookmarkLists(final List<Bookmark> bookmarks) {
        if (bookmarks == null || bookmarks.isEmpty()) {
            return;
        }
        bookmarkDao.getSession().runInTx(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < bookmarks.size(); i++) {
                        Bookmark bookmark = bookmarks.get(i);
                        bookmarkDao.insertOrReplace(bookmark);
                    }
                } catch (Exception e) {
                    LogUtils.e("批量插入或修改书签信息异常", e);
                }

            }
        });

    }

    /**
     * 删除所有数据
     */
    public void deleteAllBookmark() {
        bookmarkDao.deleteAll();
    }

    /**
     * 根据id,删除数据
     *
     * @param id
     */
    public void deleteBookmark(long id) {
        bookmarkDao.deleteByKey(id);
    }

    /**
     * 根据实体删除信息
     *
     * @param bookmark
     */
    public void deleteBookmark(Bookmark bookmark) {
        bookmarkDao.delete(bookmark);
    }
}
