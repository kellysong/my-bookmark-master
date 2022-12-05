package com.sjl.bookmark.dao.impl;

import android.content.Context;
import android.database.Cursor;

import com.sjl.bookmark.dao.BrowseTrackDao;
import com.sjl.bookmark.dao.db.BaseDao;
import com.sjl.bookmark.entity.table.BrowseTrack;
import com.sjl.bookmark.kotlin.util.StatisticsUtils;
import com.sjl.core.util.AppUtils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Date;
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
        List<BrowseTrack> list = (List<BrowseTrack>) qb.where(BrowseTrackDao.Properties.Type.eq(type)).orderDesc(BrowseTrackDao.Properties.CreateTime).list();
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
     *
     * @param type      0玩安卓，1知乎日报
     * @param articleId 文章id
     * @param href
     * @param text
     * @param category
     */
    public boolean saveBrowseTrackByType(int type, String articleId, String href, String text, String category) {
        if (isExistBrowseTrack(type, articleId)) {
//            LogUtils.i("已经存在该记录");
            return false;
        }
        BrowseTrack browseTrack = new BrowseTrack();
        browseTrack.setType(type);
        browseTrack.setArticleId(articleId);
        browseTrack.setHref(href);
        browseTrack.setText(text);
        browseTrack.setCategory(category);
        browseTrack.setCreateTime(new Date());
        insert(browseTrack);

        return true;
    }

    public boolean saveBrowseTrackByType(int type, String articleId,String text) {
        return saveBrowseTrackByType(type,articleId,null,text,null);
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

    /**
     * 查询一周的浏览数据
     * @return
     */
    public List<int[]>  findWeekData() {
        List<int[]> data = new ArrayList<>();
        String sql = "select COUNT(*) as dayCount,date(CREATE_TIME/1000, 'unixepoch', 'localtime') as countDate from BROWSE_TRACK" +
                " where type = 0  and  datetime(CREATE_TIME/1000, 'unixepoch', 'localtime')  between datetime('now','localtime','-7 days') and  datetime('now','localtime')" +
                " GROUP BY date(CREATE_TIME/1000, 'unixepoch', 'localtime')";

        Cursor cursor = daoSession.getDatabase().rawQuery(sql, null);
        try {
            //5,4,2,1
            Map<String,Integer> weekDate = new LinkedHashMap<>();
            while (cursor.moveToNext()) {
                int dayCount = cursor.getInt(cursor.getColumnIndex("dayCount"));
                String countDate = cursor.getString(cursor.getColumnIndex("countDate"));
                weekDate.put(countDate,dayCount);
            }
            String[] weekDateX = StatisticsUtils.INSTANCE.getWeekDateX("yyyy-MM-dd");
            int[] item = new int[weekDateX.length];
            for (int i = 0; i < weekDateX.length; i++) {
                String dateX = weekDateX[i];
                Integer integer = weekDate.get(dateX);
                if (integer == null){
                    item[i] = 0;
                }else {
                    item[i] = integer;
                }
            }
            data.add(item);
        } finally {
            cursor.close();
        }
       return data;
    }
}
