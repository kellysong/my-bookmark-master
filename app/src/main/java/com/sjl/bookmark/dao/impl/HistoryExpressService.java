package com.sjl.bookmark.dao.impl;

import android.content.Context;

import com.sjl.bookmark.dao.HistoryExpressDao;
import com.sjl.bookmark.dao.db.BaseDao;
import com.sjl.bookmark.entity.table.HistoryExpress;
import com.sjl.core.util.AppUtils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename HistoryExpressService.java
 * @time 2018/4/26 15:06
 * @copyright(C) 2018 song
 */
public class HistoryExpressService extends BaseDao<HistoryExpress> {

    public HistoryExpressService(Context context) {
        super(context);
    }

    /**
     * 获取未验收的快递
     *
     * @return
     */
    public List<HistoryExpress> getUnCheckList() {
        List<HistoryExpress> unCheckList = queryByParams(HistoryExpress.class, "where CHECK_STATUS = ? order by SIGN_TIME desc", new String[]{"0"});
        return unCheckList;
    }

    /**
     * 查询所有历史快递
     * @return
     */
    public List<HistoryExpress> queryHistoryExpresses() {
//        List<HistoryExpress> historyExpresses = queryAll(HistoryExpress.class);
        QueryBuilder<?> qb = daoSession.getDao(HistoryExpress.class).queryBuilder();
        //未签收在前面，已签收在后面，已经签收的按照时间降序
        List<HistoryExpress> historyExpresses = (List<HistoryExpress>) qb.orderDesc(HistoryExpressDao.Properties.CheckStatus).orderDesc(HistoryExpressDao.Properties.SignTime).list();
        return  historyExpresses;
    }

    /**
     * 校验本地是否存在该快递信息
     *
     * @param postId
     * @return true存在，false不存在
     */
    public boolean isExistHistoryExpress(String postId) {
        HistoryExpress list = queryHistoryExpress(postId);
        if (list != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 查询单个快递信息
     *
     * @param postId
     * @return
     */
    public HistoryExpress queryHistoryExpress(String postId) {
        QueryBuilder<?> qb = daoSession.getDao(HistoryExpress.class).queryBuilder();
        List<HistoryExpress> list = (List<HistoryExpress>) qb.where(HistoryExpressDao.Properties.PostId.eq(postId)).list();
        if (!AppUtils.isEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }

    /**
     * 更新或创建本地快递信息
     * @param history
     */
    public void createOrUpdateHistoryExpress(HistoryExpress history) {
        createOrUpdate(history);
    }

    /**
     * 更新跨地信息
     * @param history
     */
    public void updateHistoryExpress(HistoryExpress history) {
        update(history);
    }


    /**
     * 删除本地缓存的快递信息
     * @param history
     */
    public void deleteHistoryExpress(HistoryExpress history) {
        delete(history);
    }
}
