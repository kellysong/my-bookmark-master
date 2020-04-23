package com.sjl.bookmark.app;

import android.os.Environment;

import com.sjl.core.util.file.FileUtils;

import java.io.File;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename AppConstant.java
 * @time 2018/2/18 21:48
 * @copyright(C) 2018 song
 */
public class AppConstant {
    public static final String DES_ENCRYPTKEY = "ab11223344556677";//8字节密钥

    public static final String ROOT_PATH = Environment.getExternalStorageDirectory() + File.separator;// sd路径

    /**
     * 头像sd路径
     */
    public static final String USER_HEAD_PATH = ROOT_PATH + MyApplication.getContext().getPackageName() + File.separator + "head";

    /**
     * 书签文件路径
     */
    public static final String BOOKMARK_PATH = ROOT_PATH + MyApplication.getContext().getPackageName() + File.separator + "bookmarkTemp";

    /**
     * 更新apk 路径
     */
    public static final String UPDATE_APK_PATH = ROOT_PATH + MyApplication.getContext().getPackageName() + File.separator + "update";

    /**
     * 小说书籍缓存路径
     */
    public static final String BOOK_CACHE_PATH = FileUtils.getCachePath() + File.separator + "book_cache" + File.separator;
    /**
     * 推荐书籍默认有效时间7天
     */
    public static final int RECOMMEND_BOOK_VALID_TIME = 7;



    public static final int ACCOUNT_REFRESH_EVENT_CODE = 1;

    public final static class SETTING {
        public static final int VIEW_MODE = 0;//查询
        public static final int CREATE_MODE = 1;//新增
        public static final String CREATE_LOCK_SUCCESS = "CREATE_LOCK_SUCCESS";

        public static final int CREATE_GESTURE = 1;
        public static final int UPDATE_GESTURE = 2;
        public static int CHANGE_PASS_WORD_SHOW = 3;

        public static final String OPEN_GESTURE = "OPEN_GESTURE";
        public static final String OPEN_PASS_WORD_SHOW = "OPEN_PASS_WORD_SHOW";
        /**
         * 自动备份收藏标志
         */
        public static final String AUTO_BACKUP_COLLECTION = "auto_backup_collection";
        public static final String AUTO_BACKUP_COLLECTION_TIME = "auto_backup_collection_time";

        /**
         * 小说字体转换类型
         */
        public static final String SHARED_READ_CONVERT_TYPE = "shared_read_convert_type";
        /**
         * 记录第一条新闻id,用于判断今天的日报是否最新获取的
         */
        public static final String FIRST_STORY_ID = "first_news_id";

        /**
         * 当前选择的皮肤主题
         */
        public static final String CURRENT_SELECT_SKIN = "current_select_skin";
    }

    /**
     * 订单号扫描请求码
     */
    public static final int REQUEST_CAPTURE = 0;

    /**
     * 快递公司请求码
     */
    public static final int REQUEST_COMPANY = 1;

    /**
     *
     *通用请求码
     */
    public static final int REQUEST_CODE= 100;
    /**
     * 通用结果码
     */
    public static final int RESULT_CODE= 200;


    /**
     * 页面传递参数标志
     */
    public interface Extras {
        String SEARCH_INFO = "search_info";
    }

    public interface RxBusFlag {
        /**
         * 刷新homeFragment
         */
        int FLAG_1 = 1;
        /**
         * 刷新头像
         */
        int FLAG_2 = 2;
        /**
         * 刷新书架
         */
        int FLAG_3 = 3;

    }
}
