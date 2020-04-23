package com.sjl.bookmark.dao.impl;

import android.content.Context;
import android.database.Cursor;

import com.sjl.bookmark.app.AppConstant;
import com.sjl.bookmark.dao.CollectBookDao;
import com.sjl.bookmark.dao.db.BaseDao;
import com.sjl.bookmark.entity.zhuishu.table.BookChapter;
import com.sjl.bookmark.entity.zhuishu.table.CollectBook;
import com.sjl.core.util.file.FileUtils;
import com.sjl.core.net.RxVoid;
import com.sjl.core.util.log.LogUtils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

/**
 * 书架推荐图书
 *
 * @author Kelly
 * @version 1.0.0
 * @filename CollectBookDaoImpl.java
 * @time 2018/12/1 19:21
 * @copyright(C) 2018 song
 */
public class CollectBookDaoImpl extends BaseDao<CollectBook> {
    private BookChapterDaoImpl bookChapterService;

    public CollectBookDaoImpl(Context context) {
        super(context);
        this.bookChapterService = new BookChapterDaoImpl(context);
    }

    /**
     * 保存推荐的图书
     *
     * @param collBookBeans
     */
    public void saveCollectBookBeans(List<CollectBook> collBookBeans) {
        int maxSortId = collBookBeans.size();
        for (CollectBook bean : collBookBeans) {
            bean.setBookSortId(maxSortId);//为每一本书设置一个在书架排序id
            maxSortId--;
            if (bean.getBookChapters() != null) {
                //存储BookChapter(需要修改，如果存在id相同的则无视)
                bookChapterService.saveBookChapters(bean.getBookChapters());
            }
        }
        LogUtils.i("书架空空如也，在线获取推荐的图书================");
        for (CollectBook bean : collBookBeans) {
            LogUtils.i(bean.get_id() + "," + bean.getTitle() + "," + bean.getBookSortId());
        }
        batchInsert(collBookBeans);//存在直接更新，不存在更新数据
    }

    /**
     * 存储已收藏书籍
     *
     * @param bean
     */
    public void saveCollBookWithAsync(final CollectBook bean) {
        int maxSortId = getMaxSortId() <= 0 ? 1 : getMaxSortId() + 1;//获取最大排序id加1
        bean.setBookSortId(maxSortId);
        //启动异步存储
        daoSession.startAsyncSession()
                .runInTx(new Runnable() {
                    @Override
                    public void run() {
                        if (bean.getBookChapters() != null) {
                            // 存储BookChapterBean
                            List<BookChapter> bookChapters = bean.getBookChapters();
                            bookChapterService.saveBookChapters(bookChapters);
                        }
                        //存储CollectBook (确保先后顺序，否则出错)
                        daoSession.insertOrReplace(bean);
                    }
                });
    }

    /**
     * 获取图书的最大排序id
     *
     * @return
     */
    private int getMaxSortId() {
        String sql = "SELECT  max(BOOK_SORT_ID) as maxvalue FROM COLLECT_BOOK";
        Cursor cursor = null;
        int count = -1;
        try {
            cursor = manager.getDb().rawQuery(sql, null);
            if (cursor == null || !cursor.moveToFirst()) {
                return count;
            }
            count = cursor.getInt(0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return count;
    }

    /**
     * 查询书籍转换为map
     *
     * @return
     */
    public Map<String, CollectBook> collectBookListToMap() {
        List<CollectBook> localData = findAll();
        if (localData != null && !localData.isEmpty()) {
            Map<String, CollectBook> result = new LinkedHashMap<String, CollectBook>();
            for (CollectBook bean : localData) {
                result.put(bean.get_id(), bean);
            }
            return result;
        }
        return null;
    }

    /**
     * 降序查询所有书籍,BookSortId最大说明是最新添加的书籍
     *
     * @return
     */
    public List<CollectBook> findAll() {
//        daoSession.getCollectionDao().detachAll();
        QueryBuilder<CollectBook> builder = (QueryBuilder<CollectBook>) daoSession.getDao(CollectBook.class).queryBuilder();
        builder.orderDesc(CollectBookDao.Properties.BookSortId);//降序
        return queryWithParams(builder);
    }

    /**
     * 重新设置顺序
     *
     * @param bookList 返回新的顺序
     */
    public synchronized void resetCollectBookSortId(List<CollectBook> bookList) {
        LogUtils.i("resetCollectBookSortId后");
        int i = bookList.size();
        for (CollectBook bean : bookList) {
            bean.setBookSortId(i);
//            LogUtils.i(bean.get_id() + "," + bean.getTitle() + "," + bean.getBookSortId());
            i--;
        }
        batchUpdate(bookList,CollectBook.class);
    }

    /**
     * 删除单本书
     *
     * @param collectBook
     */
    public void deleteCollectBook(CollectBook collectBook) {
        delete(collectBook);
    }


    /**
     * 保存单本书
     *
     * @param collectBook
     */
    public void saveCollectBook(CollectBook collectBook) {
        createOrUpdate(collectBook);
    }

    /**
     * 批量保存书
     *
     * @param collectBook
     */
    public void saveCollectBooks(List<CollectBook> collectBook) {
        batchInsert(collectBook);
    }

    /**
     * 删除在线图书相关数据
     *
     * @param collectBook
     * @return
     */
    public Single<RxVoid> deleteCollBookInRx(final CollectBook collectBook) {
        return Single.create(new SingleOnSubscribe<RxVoid>() {
            @Override
            public void subscribe(SingleEmitter<RxVoid> e) throws Exception {
                //查看文本中是否存在删除的数据
                deleteBookFile(collectBook.get_id());
                //删除下载任务
//                deleteDownloadTask(bean.get_id());
                //删除目录
                bookChapterService.deleteBookChapter(collectBook.get_id());
                //删除CollBook
                deleteCollectBook(collectBook);
                //删除阅读记录
                DaoFactory.getBookRecordDao().deleteBookRecord(collectBook.get_id());
                //删除推荐书籍记录
                DaoFactory.getRecommendBookDao().deleteRecommendBookByBookId(collectBook.get_id());
                e.onSuccess(new RxVoid());
            }
        });
    }

    /**
     * 删除所有书籍
     *
     * @param all
     * @return
     */
    public Single<RxVoid> deleteAllCollectBookInRx(final List<CollectBook> all) {
        return Single.create(new SingleOnSubscribe<RxVoid>() {
            @Override
            public void subscribe(SingleEmitter<RxVoid> e) throws Exception {
                for (CollectBook collectBook : all) {
                    if (collectBook.isLocal()) {//不删除本地文件
                        bookChapterService.deleteBookChapter(collectBook.get_id());
                        deleteCollectBook(collectBook);
                        DaoFactory.getBookRecordDao().deleteBookRecord(collectBook.get_id());
                    } else {
                        //查看文本中是否存在删除的数据
                        deleteBookFile(collectBook.get_id());
                        //删除下载任务
                        //deleteDownloadTask(bean.get_id());
                        //删除章节目录
                        bookChapterService.deleteBookChapter(collectBook.get_id());
                        //删除CollBook
                        deleteCollectBook(collectBook);
                        //删除阅读记录
                        DaoFactory.getBookRecordDao().deleteBookRecord(collectBook.get_id());
                        //删除推荐书籍记录
                        DaoFactory.getRecommendBookDao().deleteRecommendBookByBookId(collectBook.get_id());

                    }
                }

                e.onSuccess(new RxVoid());
            }
        });
    }

    /**
     * 删除书籍文件
     *
     * @param bookId
     */
    public void deleteBookFile(String bookId) {
        FileUtils.deleteFile(AppConstant.BOOK_CACHE_PATH + bookId);
    }

    /**
     * 查询书籍信息
     *
     * @param bookId
     * @return
     */
    public CollectBook getCollectBook(String bookId) {
        CollectBook bean = (CollectBook) daoSession.getDao(CollectBook.class).queryBuilder()
                .where(CollectBookDao.Properties._id.eq(bookId))
                .unique();
        return bean;
    }


}
