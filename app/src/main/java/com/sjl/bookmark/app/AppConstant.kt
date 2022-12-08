package com.sjl.bookmark.app

import android.os.Environment
import com.sjl.core.util.file.FileUtils
import java.io.File

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename AppConstant.java
 * @time 2018/2/18 21:48
 * @copyright(C) 2018 song
 */
object AppConstant {
    const val DES_ENCRYPTKEY = "ab11223344556677" //8字节密钥
    val ROOT_PATH = Environment.getExternalStorageDirectory().toString() + File.separator // sd路径
    /**
     * 启动页文件路径
     */
    val SPLASH_PATH =
        ROOT_PATH + MyApplication.getContext().packageName + File.separator + "splash"

    /**
     * 头像sd路径
     */
    val USER_HEAD_PATH =
        ROOT_PATH + MyApplication.getContext().packageName + File.separator + "head"

    /**
     * 书签文件路径
     */
    val BOOKMARK_PATH =
        ROOT_PATH + MyApplication.getContext().packageName + File.separator + "bookmarkTemp"


    /**
     * 更新apk 路径
     */
    @JvmField
    val UPDATE_APK_PATH =
        ROOT_PATH + MyApplication.getContext().packageName + File.separator + "update"

    /**
     * 小说书籍缓存路径
     */
    @JvmField
    val BOOK_CACHE_PATH = FileUtils.getCachePath() + File.separator + "book_cache" + File.separator

    /**
     * 图片
     */
    val BOOKMARK_DCIM = Environment.DIRECTORY_DCIM+File.separator+"bookmark"

    /**
     * 下载
     */
    val BOOKMARK_DOWNLOADS = Environment.DIRECTORY_DOWNLOADS+File.separator+"bookmark"

    /**
     * 推荐书籍默认有效时间7天
     */
    const val RECOMMEND_BOOK_VALID_TIME = 7
    const val ACCOUNT_REFRESH_EVENT_CODE = 1

    /**
     * 订单号扫描请求码
     */
    const val REQUEST_CAPTURE = 0

    /**
     * 快递公司请求码
     */
    const val REQUEST_COMPANY = 1

    /**
     *
     * 通用请求码
     */
    const val REQUEST_CODE = 100

    /**
     * 通用结果码
     */
    const val RESULT_CODE = 200

    const val SETTING_PWD = "123123"


    object SETTING {
        const val VIEW_MODE = 0 //查询
        const val CREATE_MODE = 1 //新增
        const val CREATE_LOCK_SUCCESS = "CREATE_LOCK_SUCCESS"
        const val CREATE_GESTURE = 1
        const val UPDATE_GESTURE = 2
        var CHANGE_PASS_WORD_SHOW = 3
        const val OPEN_GESTURE = "OPEN_GESTURE"
        const val OPEN_PASS_WORD_SHOW = "OPEN_PASS_WORD_SHOW"

        /**
         * 自动备份收藏标志
         */
        const val AUTO_BACKUP_COLLECTION = "auto_backup_collection"
        const val AUTO_BACKUP_COLLECTION_TIME = "auto_backup_collection_time"

        /**
         * 小说字体转换类型
         */
        const val SHARED_READ_CONVERT_TYPE = "shared_read_convert_type"

        /**
         * 记录第一条新闻id,用于判断今天的日报是否最新获取的
         */
        const val FIRST_STORY_ID = "first_news_id"

        /**
         * 当前选择的皮肤主题
         */
        const val CURRENT_SELECT_SKIN = "current_select_skin"

        /**
         * 暗黑模式
         */
        const val DARK_THEME = "dark_theme"

        /**
         * 登录日期
         */
        const val LOGIN_DATE = "login_date"

        /**
         * 书签服务base url
         */
        const val BOOKMARK_BASE_URL = "bookmark_base_url"

        /**
         * 哀悼日
         */
        const val MOURNING_DAYS = "mourning_days"
    }

    /**
     * 页面传递参数标志
     */
    interface Extras {
        companion object {
            const val SEARCH_INFO = "search_info"
        }
    }

    interface SignStatus {
        companion object {
            const val NOT_SINGED = 0
            const val SIGNED = 1
        }
    }

    interface RxBusFlag {
        companion object {
            /**
             * 刷新homeFragment
             */
            const val FLAG_1 = 1

            /**
             * 刷新头像
             */
            const val FLAG_2 = 2

            /**
             * 刷新书架
             */
            const val FLAG_3 = 3
        }
    }
}