package com.sjl.bookmark.dao.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.github.yuweiguocn.library.greendao.MigrationHelper;
import com.sjl.bookmark.dao.AccountDao;
import com.sjl.bookmark.dao.BookChapterDao;
import com.sjl.bookmark.dao.BookRecordDao;
import com.sjl.bookmark.dao.BookmarkDao;
import com.sjl.bookmark.dao.BrowseTrackDao;
import com.sjl.bookmark.dao.CollectBookDao;
import com.sjl.bookmark.dao.CollectionDao;
import com.sjl.bookmark.dao.DaoMaster;
import com.sjl.bookmark.dao.HistoryExpressDao;
import com.sjl.bookmark.dao.RecommendBookDao;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.database.Database;

/**
 * 数据库升级帮助类
 *
 * @author Kelly
 * @version 1.0.0
 * @filename MySQLiteOpenHelper.java
 * @time 2018/12/7 8:48
 * @copyright(C) 2018 song
 */
public class MySQLiteOpenHelper extends DaoMaster.OpenHelper {
    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        /**
         * 要升级的数据库表DAO
         */
        Class<? extends AbstractDao<?, ?>>[] classes = new Class[]{
                AccountDao.class, BookmarkDao.class, CollectionDao.class, HistoryExpressDao.class
                , CollectBookDao.class, BookChapterDao.class, BookRecordDao.class,
                RecommendBookDao.class, BrowseTrackDao.class
        };

        MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {

            @Override
            public void onCreateAllTables(Database db, boolean ifNotExists) {
                DaoMaster.createAllTables(db, ifNotExists);
            }

            @Override
            public void onDropAllTables(Database db, boolean ifExists) {
                DaoMaster.dropAllTables(db, ifExists);
            }
        }, classes);
    }
}
