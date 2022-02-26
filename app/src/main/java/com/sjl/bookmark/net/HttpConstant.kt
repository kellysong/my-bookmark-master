package com.sjl.bookmark.net;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename HttpConstant.java
 * @time 2018/3/21 9:17
 * @copyright(C) 2018 song
 */
public class HttpConstant {
    /**
     * 我的书签服务base url
     */
    public static final String MY_BOOKMARK_BASE_URL = "http://192.168.0.176:8080/";

    /**
     * WanAndroid
     */
    public static final String DEFAULT_BASE_URL = "https://www.wanandroid.com/";
    /**
     * 快递100
     */
    public static final String KUAIDI100_BASE_URL = "https://www.kuaidi100.com/";
    /**
     * 追书神器
     */
    public static final String ZHUISHU_BASE_URL = "http://api.zhuishushenqi.com";
    /**
     * 追书神器图片
     */
    public static final String ZHUISHU_IMG_BASE_URL = "http://statics.zhuishushenqi.com";


    /**
     * 知乎日报
     * https://news-at.zhihu.com/api/4/news/latest
     */
    public static final String ZHIHU_BASE_URL = "http://news-at.zhihu.com/";

    /**
     * 区分不同服务
     */
    public static final String DOMAIN_NAME = "Domain-Name";


    // start玩安卓================
    /**
     * 每页数量
     */
    public static final int PAGE_SIZE = 20;

    public static final int PAGE_SIZE_15 = 15;

    public static final String BANNER_KEY = "banner";
    public static final String ARTICLE_KEY = "article";

    /**
     * cid 分类的id，上述二级目录的id
     */
    public static final String CONTENT_CID_KEY = "cid";

    /**
     * 一级标题title
     */
    public static final String CONTENT_TITLE_KEY = "title";

    /**
     * 二级标题集合
     */
    public static final String CONTENT_CHILDREN_DATA_KEY = "childrenData";

    /**
     * 0打开单个类别,隐藏专题页面的搜索按钮，1打开专题
     */
    public static final String CONTENT_OPEN_FLAG = "openFlag";


    // end玩安卓================


    public interface LoadType {
        /**
         * 刷新成功
         */
        public static final int TYPE_REFRESH_SUCCESS = 1;
        /**
         * 刷新错误
         */
        public static final int TYPE_REFRESH_ERROR = 2;
        /**
         * 加载更多成功
         */
        public static final int TYPE_LOAD_MORE_SUCCESS = 3;
        /**
         * 加载更多失败
         */
        public static final int TYPE_LOAD_MORE_ERROR = 4;
    }


}
