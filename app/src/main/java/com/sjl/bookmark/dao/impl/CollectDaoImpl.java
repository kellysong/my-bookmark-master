package com.sjl.bookmark.dao.impl;

import android.content.Context;
import android.text.TextUtils;

import com.sjl.bookmark.dao.CollectionDao;
import com.sjl.bookmark.dao.db.BaseDao;
import com.sjl.bookmark.entity.table.Collection;
import com.sjl.core.util.AppUtils;
import com.sjl.core.util.log.LogUtils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename CollectDaoImpl.java
 * @time 2018/3/25 16:21
 * @copyright(C) 2018 song
 */
public class CollectDaoImpl extends BaseDao<Collection> {

    public CollectDaoImpl(Context context) {
        super(context);
    }

    /**
     * 更新收藏
     *
     * @param collection
     */
    public void updateCollection(Collection collection) {
        update(collection);
    }


    /**
     * 新增收藏
     *
     * @param collection
     */
    public boolean saveCollection(Collection collection) {
        boolean result = insert(collection);
        LogUtils.i(result == false ? "添加收藏失败" : "添加收藏成功");
        return result;
    }

    /**
     * 校验是否存在收藏
     *
     * @param url
     * @return
     */
    public boolean isExistCollection(String url) {
        QueryBuilder<?> qb = daoSession.getDao(Collection.class).queryBuilder();
        List<Collection> list = (List<Collection>) qb.where(CollectionDao.Properties.Href.eq(url)).list();
        if (AppUtils.isEmpty(list)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 分页查询收藏
     *
     * @param title
     * @param pageOffset
     * @param pageSize
     * @return
     */
    public List<Collection> queryCollectByPage(String title, int pageOffset, int pageSize) {
        if (pageSize <= 0 || pageSize >= 50) {
            pageSize = 12;
        }
        QueryBuilder<?> builder = daoSession.getDao(Collection.class).queryBuilder();
        if (!TextUtils.isEmpty(title)) {
            builder.where(CollectionDao.Properties.Title.like("%" + title + "%"));
        }
        builder.orderDesc(CollectionDao.Properties.Top).orderDesc(CollectionDao.Properties.Time).orderDesc(CollectionDao.Properties.Date);
        List<Collection> collections = queryWithParamsByPage((QueryBuilder<Collection>) builder, pageOffset, pageSize);
        return collections;
    }

    /**
     * 删除收藏
     *
     * @param collection
     */
    public void deleteCollection(Collection collection) {
        delete(collection);
    }

    /**
     * 查询所有收藏
     *
     * @return
     */
    public List<Collection> findAllCollection() {
        List<Collection> collections = queryAll(Collection.class);
        return collections;
    }

    /**
     * 批量保存收藏
     *
     * @param collection
     */
    public boolean batchSaveCollection(List<Collection> collection) {
        boolean result = batchInsert(collection);
        LogUtils.i(result == false ? "批量保存收藏失败" : "批量保存收藏成功");
        return result;
    }
}
