package com.sjl.bookmark.dao.db;

import android.content.Context;

import com.sjl.bookmark.dao.DaoSession;
import com.sjl.core.util.log.LogUtils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BaseDao.java
 * @time 2018/3/25 16:08
 * @copyright(C) 2018 song
 */
public class BaseDao<T> {
    protected DatabaseManager manager;
    /**
     * 用来获取dao
     */
    protected DaoSession daoSession;

    public BaseDao(Context context) {
        manager = DatabaseManager.getInstance(context);
        daoSession = manager.getDaoSession();
    }

    /**************************数据库插入操作***********************/
    /**
     * 插入单个对象
     *
     * @param object
     * @return
     */
    public boolean insert(T object) {
        boolean flag = false;
        try {
            flag = manager.getDaoSession().insert(object) != -1 ? true : false;
        } catch (Exception e) {
            LogUtils.e(e.toString(), e);
        }
        return flag;
    }

    /**
     * 插入多个对象，并开启新的线程
     *
     * @param objects
     * @return
     */
    public boolean batchInsert(final List<T> objects) {
        boolean flag;
        if (null == objects || objects.isEmpty()) {
            return false;
        }
        try {
            manager.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (T object : objects) {
                        manager.getDaoSession().insertOrReplace(object);
                    }
                }
            });
            flag = true;
        } catch (Exception e) {
            LogUtils.e(e.toString(), e);
            flag = false;
        }
        return flag;
    }

    /**
     * 异步插入多个对象，并开启新的线程
     *
     * @param objects
     * @return
     */
    public void batchInsertAsync(final List<T> objects) {
        if (null == objects || objects.isEmpty()) {
            return;
        }
        try {
            manager.getDaoSession().startAsyncSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    daoSession.getDao(getEntityClass()).insertOrReplaceInTx(objects);

                }
            });
        } catch (Exception e) {
            LogUtils.e(e.toString(), e);
        }
    }


    /**************************数据库更新操作***********************/
    /**
     * 以对象形式进行数据修改
     * 其中必须要知道对象的主键ID
     *
     * @param object
     * @return
     */
    public void update(T object) {

        if (null == object) {
            return;
        }
        try {
            manager.getDaoSession().update(object);
        } catch (Exception e) {
            LogUtils.e(e.toString(), e);
        }
    }

    /**
     * 以对象形式进行数据修改
     * 其中更新必须要知道对象的主键ID
     *
     * @param object
     * @return
     */
    public void createOrUpdate(T object) {
        if (null == object) {
            return;
        }
        try {
            manager.getDaoSession().insertOrReplace(object);
        } catch (Exception e) {
            LogUtils.e("createOrUpdate:" + e.toString(), e);
        }
    }

    /**
     * 批量更新数据
     *
     * @param objects
     * @return
     */
    public void batchUpdate(final List<T> objects, Class clss) {
        if (null == objects || objects.isEmpty()) {
            return;
        }
        try {

            daoSession.getDao(clss).updateInTx(objects);
        } catch (Exception e) {
            LogUtils.e(e.toString(), e);
        }
    }


    /**************************数据库删除操作***********************/
    /**
     * 删除某个数据库表
     *
     * @param clss
     * @return
     */
    public boolean deleteAll(Class clss) {
        boolean flag;
        try {
            manager.getDaoSession().deleteAll(clss);
            flag = true;
        } catch (Exception e) {
            LogUtils.e(e.toString(), e);
            flag = false;
        }
        return flag;
    }

    /**
     * 删除某个对象
     *
     * @param object
     * @return
     */
    public void delete(T object) {
        try {
            daoSession.delete(object);
        } catch (Exception e) {
            LogUtils.e("删除单个数据异常", e);
        }
    }

    /**
     * 批量删除数据
     *
     * @param objects
     * @return
     */
    public boolean batchDelete(final List<T> objects, Class clss) {
        boolean flag;
        if (null == objects || objects.isEmpty()) {
            return false;
        }
        try {
            daoSession.getDao(clss).deleteInTx(objects);
            flag = true;
        } catch (Exception e) {
            LogUtils.e("批量删除数据异常", e);
            flag = false;
        }
        return flag;
    }

    /**
     * 异步批量删除数据
     *
     * @param objects
     * @return
     */
    public void batchDeleteAsync(final List<T> objects) {
        if (null == objects || objects.isEmpty()) {
            return;
        }
        try {
            manager.getDaoSession().startAsyncSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    daoSession.getDao(getEntityClass()).deleteInTx(objects);

                }
            });
        } catch (Exception e) {
            LogUtils.e("异步批量删除数据异常", e);
        }
    }

    /**************************数据库查询操作***********************/


    /**
     * 根据主键ID来查询
     *
     * @param id
     * @return
     */
    public T queryById(long id, Class object) {
        return (T) daoSession.getDao(object).loadByRowId(id);
    }

    /**
     * 查询某条件下的对象
     *
     * @param object
     * @return
     */
    public List<T> queryByParams(Class object, String where, String... params) {
        List<T> objects = null;
        try {
            objects = daoSession.getDao(object).queryRaw(where, params);
        } catch (Exception e) {
            LogUtils.e(e.toString(), e);
        }
        return objects;
    }

    /**
     * 分页查询
     *
     * @param queryParams 查询参数
     * @param pageOffset
     * @param pageSize
     * @return
     */
    public List<T> queryWithParamsByPage(QueryBuilder<T> queryParams, int pageOffset, int pageSize) {
        List<T> objects = null;
        try {
            queryParams.offset((pageOffset - 1) * pageSize).limit(pageSize);
            objects = queryParams.list();
        } catch (Exception e) {
            LogUtils.e(e.toString(), e);
        }
        return objects;
    }

    /**
     * 查询
     *
     * @param queryParams 查询参数
     * @return
     */
    public List<T> queryWithParams(QueryBuilder<T> queryParams) {
        List<T> objects = null;
        try {
            objects = queryParams.list();
        } catch (Exception e) {
            LogUtils.e(e.toString(), e);
        }
        return objects;
    }

    /**
     * 查询所有对象
     *
     * @param object
     * @return
     */
    public List<T> queryAll(Class object) {
        List<T> objects = null;
        try {
            objects = (List<T>) daoSession.getDao(object).loadAll();
        } catch (Exception e) {
            LogUtils.e(e.toString(), e);
        }
        return objects;
    }

    protected Class<T> entityClass;

    protected Class getEntityClass() {
        if (entityClass == null) {
            // clazz.getGenericSuperclass();
            //得到泛型父类 ParameterizedType是参数化类型
            //getActualTypeArguments获取参数化类型的数组，可能有多个
            entityClass = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass())
                    .getActualTypeArguments()[0];
        }
        return entityClass;
    }

}
