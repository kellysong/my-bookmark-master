package com.sjl.bookmark.dao.impl;

import android.content.Context;

import com.sjl.bookmark.dao.BrowseTrackDao;
import com.sjl.bookmark.dao.db.BaseDao;
import com.sjl.bookmark.entity.table.BrowseTrack;
import com.sjl.core.util.AppUtils;
import com.sjl.core.util.log.LogUtils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 浏览足迹DaoImpl
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BrowseTrackDaoImpl.java
 * @time 2018/12/23 16:19
 * @copyright(C) 2018 song
 */
public class BrowseTrackDaoImpl extends BaseDao<BrowseTrack> {
    public BrowseTrackDaoImpl(Context context) {
        super(context);
    }

    /**
     * 查询单条足迹
     *
     * @param type      0玩安卓，1知乎日报
     * @param articleId 文章id
     */
    public List<BrowseTrack> findBrowseTrackByType(int type, String articleId) {
        QueryBuilder<?> qb = daoSession.getDao(BrowseTrack.class).queryBuilder();
        List<BrowseTrack> list = (List<BrowseTrack>) qb.where(BrowseTrackDao.Properties.Type.eq(type), BrowseTrackDao.Properties.ArticleId.eq(articleId)).list();
        return list;

    }

    /**
     * 查询一个类别足迹
     *
     * @param type 0玩安卓，1知乎日报
     */
    public List<BrowseTrack> findBrowseTrackByType(int type) {
        QueryBuilder<?> qb = daoSession.getDao(BrowseTrack.class).queryBuilder();
        List<BrowseTrack> list = (List<BrowseTrack>) qb.where(BrowseTrackDao.Properties.Type.eq(type)).list();
        return list;

    }


    /**
     * 查询一个类别足迹，并转化map
     *
     * @param type 0玩安卓，1知乎日报
     * @return
     */
    public Map<String, Boolean> browseTrackToMap(int type) {
        Map<String, Boolean> result = new LinkedHashMap<String, Boolean>();
        List<BrowseTrack> browseTrackList = findBrowseTrackByType(type);
        if (browseTrackList != null && !browseTrackList.isEmpty()) {
            for (BrowseTrack bean : browseTrackList) {
                result.put(bean.getArticleId(), true);
            }
            return result;
        }
        return result;
    }

    /**
     * 删除所有足迹
     */
    public void deleteAllBrowseTrack() {
        deleteAll(BrowseTrack.class);
    }

    /**
     * 保存单条足迹
     *  @param type      0玩安卓，1知乎日报
     * @param articleId 文章id
     */
    public boolean saveBrowseTrackByType(int type, String articleId) {
        if (isExistBrowseTrack(type, articleId)) {
            LogUtils.i("已经存在该记录");
            return false;
        }
        BrowseTrack browseTrack = new BrowseTrack();
        browseTrack.setType(type);
        browseTrack.setArticleId(articleId);
        insert(browseTrack);

        return true;
    }

    /**
     * 校验是否存浏览足迹
     *
     * @param type      0玩安卓，1知乎日报
     * @param articleId 文章id
     * @return
     */
    public boolean isExistBrowseTrack(int type, String articleId) {

        List<BrowseTrack> list = findBrowseTrackByType(type, articleId);
        if (AppUtils.isEmpty(list)) {
            return false;
        } else {
            return true;
        }
    }
}
