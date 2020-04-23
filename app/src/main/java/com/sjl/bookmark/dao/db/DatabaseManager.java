package com.sjl.bookmark.dao.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.sjl.bookmark.dao.DaoMaster;
import com.sjl.bookmark.dao.DaoSession;

/**
 * 数据库连接管理
 * （由于SQLite是数据库级别锁，同一个数据库连接可以多线程操作，同时读、写、读写操作，SQLite底层做了同步处理；
 * 多个数据库连接，只可以多线程读操作，不可写或读写操作，否则会抛出锁表异常，故设成单例连接类）
 * GreenDao类作用：
 *  DaoMaster: 正如其名，这个类是数据库的统领，也是整个操作的入口。其管理着数据库， 数据库版本号和一些数据库相关的信息。DaoMaster可以用来生成下面将要说到DaoSession。在构造函数中需要对所有的Dao通过registerDaoClass()进行注册，以便DaoMaster对其进行管理。
         OpenHelper: 根据官方文档，GreenDAO中总共有四种OpenHelper，分别是 OpenHelper, DevOpenHelper，EncryptedOpenHelper 和EncryptedDevOpenHelper；前两个是用来操作未加密数据库，后两个是用来操作加密数据库；DevOpenHelper 和 EncryptedDevOpenHelper 会在升级的时候删除所有表并重建，所以只建议在开发时使用。
        OpenHelper的用来对数据库进行管理，如创建表，删除表，获取可读写数据库等。另外，OpenHelper还有对数据库创建，数据库升级 和 数据库开启等事件的监听，分别在onCreate(), onUpgrade(), onOpen()。
        DaoSession: 管理所有的XXXDao, DaoSession中也会有增删改查的方法， 其可以直接通过要插入实体的类型找到对应的XXXDao后再进行操作。当没有找到实体对应的Dao时，会抛出 org.greenrobot.greendao.DaoException: No DAO registered for class XXX 的错误。
        XXXDao： 对实体进行操作，有比DaoSession更丰富的操作，如loadAll, insertInTx.
 * @author Kelly
 * @version 1.0.0
 * @filename DatabaseManager.java
 * @time 2018/3/8 14:10
 * @copyright(C) 2018 song
 */
public class DatabaseManager {
    private static DatabaseManager databaseManager;
    private DaoSession daoSession;
    private SQLiteDatabase db;
    private static final String DB_NAME = "bookmark-db";
    /**
     * 不要在Presenter构造器中实例化数据库,因为Presenter实例化时，context可能为空;
     * 或者使用应用级别的上下文，即BaseApplication.getContext()
     * @param context 不能为空
     */
    private DatabaseManager(@NonNull Context context) {
        /**
         * 可以看到在onCreate方法中调用了createAllTable方法，顾名思义就是创建所有的数据表，而在onUpgrade方法中先是删除所有的数据表，然后再调用onCreate方法，也可以在onUpgrade方法中实现自定义的数据库升级操作。

         根据DevOpenHelper可以得到SQLiteDatabase对象，SQLiteDatabase是Android原生的数据库操作类，该类提供了大量操作数据库的方法，如果想在项目中调用Android原生的sql语句就可以用该类实现。
         */
        MySQLiteOpenHelper helper = new MySQLiteOpenHelper(context, DB_NAME, null);//初始化数据库
        db = helper.getWritableDatabase();//获取数据库
        /**
         * DaoMaster包含了DevOpenHelper，上述createAllTables(db, false)和dropAllTables(db, true)也是DaoMaster提供的，DaoMaster可以新建DaoSession，DaoSession管理所有的DAO文件，并提供相应的getter方法。
         */
        DaoMaster daoMaster = new DaoMaster(db);
        //初始化DaoMaster
        daoSession = daoMaster.newSession();//初始化DaoSession
    }

    public static DatabaseManager getInstance(Context context) {
        if (databaseManager == null) {
            synchronized (DatabaseManager.class) {
                if (databaseManager == null){
                    databaseManager = new DatabaseManager(context);
                }
            }
        }
        return databaseManager;
    }

    public synchronized DaoSession getDaoSession() {
        return daoSession;
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    public void shutdown() {
        if (daoSession != null) {
            daoSession.clear();
        }
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    public boolean checkDBStatus() {
        if (db != null && db.isOpen()) {
            return true;
        } else {
            return false;
        }
    }

}
