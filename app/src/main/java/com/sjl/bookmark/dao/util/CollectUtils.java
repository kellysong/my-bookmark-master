package com.sjl.bookmark.dao.util;

import android.content.Context;

import com.sjl.bookmark.dao.impl.CollectDaoImpl;
import com.sjl.bookmark.entity.table.Collection;

import java.util.Date;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename CollectUtils.java
 * @time 2018/3/26 15:33
 * @copyright(C) 2018 song
 */
public class CollectUtils {

    private CollectUtils(){

    }

    /**
     * 收藏网页
     * @param context 上下文
     * @param title 标题
     * @param url 链接
     * @return 0 收藏成功，1改收藏已经存在，-1收藏失败
     */
    public static int collectWebPage(Context context, String title, String url) {
        CollectDaoImpl collectService = new CollectDaoImpl(context);
        boolean result = collectService.isExistCollection(url);
        if (result){
            return 1;
        }
        result = collectService.saveCollection(new Collection(null, title, 0, url,new Date(),0,0));
        if (result){
            return 0;
        }else{
            return -1;
        }
    }
}
