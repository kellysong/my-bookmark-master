package com.sjl.bookmark.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.sjl.bookmark.entity.zhuishu.table.CollectBook;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "COLLECT_BOOK".
*/
public class CollectBookDao extends AbstractDao<CollectBook, String> {

    public static final String TABLENAME = "COLLECT_BOOK";

    /**
     * Properties of entity CollectBook.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property _id = new Property(0, String.class, "_id", true, "_ID");
        public final static Property Title = new Property(1, String.class, "title", false, "TITLE");
        public final static Property Author = new Property(2, String.class, "author", false, "AUTHOR");
        public final static Property ShortIntro = new Property(3, String.class, "shortIntro", false, "SHORT_INTRO");
        public final static Property Cover = new Property(4, String.class, "cover", false, "COVER");
        public final static Property HasCp = new Property(5, boolean.class, "hasCp", false, "HAS_CP");
        public final static Property LatelyFollower = new Property(6, int.class, "latelyFollower", false, "LATELY_FOLLOWER");
        public final static Property RetentionRatio = new Property(7, double.class, "retentionRatio", false, "RETENTION_RATIO");
        public final static Property Updated = new Property(8, String.class, "updated", false, "UPDATED");
        public final static Property LastRead = new Property(9, String.class, "lastRead", false, "LAST_READ");
        public final static Property ChaptersCount = new Property(10, int.class, "chaptersCount", false, "CHAPTERS_COUNT");
        public final static Property LastChapter = new Property(11, String.class, "lastChapter", false, "LAST_CHAPTER");
        public final static Property IsUpdate = new Property(12, boolean.class, "isUpdate", false, "IS_UPDATE");
        public final static Property IsLocal = new Property(13, boolean.class, "isLocal", false, "IS_LOCAL");
        public final static Property BookSortId = new Property(14, int.class, "bookSortId", false, "BOOK_SORT_ID");
    }

    private DaoSession daoSession;


    public CollectBookDao(DaoConfig config) {
        super(config);
    }
    
    public CollectBookDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"COLLECT_BOOK\" (" + //
                "\"_ID\" TEXT PRIMARY KEY NOT NULL ," + // 0: _id
                "\"TITLE\" TEXT," + // 1: title
                "\"AUTHOR\" TEXT," + // 2: author
                "\"SHORT_INTRO\" TEXT," + // 3: shortIntro
                "\"COVER\" TEXT," + // 4: cover
                "\"HAS_CP\" INTEGER NOT NULL ," + // 5: hasCp
                "\"LATELY_FOLLOWER\" INTEGER NOT NULL ," + // 6: latelyFollower
                "\"RETENTION_RATIO\" REAL NOT NULL ," + // 7: retentionRatio
                "\"UPDATED\" TEXT," + // 8: updated
                "\"LAST_READ\" TEXT," + // 9: lastRead
                "\"CHAPTERS_COUNT\" INTEGER NOT NULL ," + // 10: chaptersCount
                "\"LAST_CHAPTER\" TEXT," + // 11: lastChapter
                "\"IS_UPDATE\" INTEGER NOT NULL ," + // 12: isUpdate
                "\"IS_LOCAL\" INTEGER NOT NULL ," + // 13: isLocal
                "\"BOOK_SORT_ID\" INTEGER NOT NULL );"); // 14: bookSortId
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"COLLECT_BOOK\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, CollectBook entity) {
        stmt.clearBindings();
 
        String _id = entity.get_id();
        if (_id != null) {
            stmt.bindString(1, _id);
        }
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(2, title);
        }
 
        String author = entity.getAuthor();
        if (author != null) {
            stmt.bindString(3, author);
        }
 
        String shortIntro = entity.getShortIntro();
        if (shortIntro != null) {
            stmt.bindString(4, shortIntro);
        }
 
        String cover = entity.getCover();
        if (cover != null) {
            stmt.bindString(5, cover);
        }
        stmt.bindLong(6, entity.getHasCp() ? 1L: 0L);
        stmt.bindLong(7, entity.getLatelyFollower());
        stmt.bindDouble(8, entity.getRetentionRatio());
 
        String updated = entity.getUpdated();
        if (updated != null) {
            stmt.bindString(9, updated);
        }
 
        String lastRead = entity.getLastRead();
        if (lastRead != null) {
            stmt.bindString(10, lastRead);
        }
        stmt.bindLong(11, entity.getChaptersCount());
 
        String lastChapter = entity.getLastChapter();
        if (lastChapter != null) {
            stmt.bindString(12, lastChapter);
        }
        stmt.bindLong(13, entity.getIsUpdate() ? 1L: 0L);
        stmt.bindLong(14, entity.getIsLocal() ? 1L: 0L);
        stmt.bindLong(15, entity.getBookSortId());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, CollectBook entity) {
        stmt.clearBindings();
 
        String _id = entity.get_id();
        if (_id != null) {
            stmt.bindString(1, _id);
        }
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(2, title);
        }
 
        String author = entity.getAuthor();
        if (author != null) {
            stmt.bindString(3, author);
        }
 
        String shortIntro = entity.getShortIntro();
        if (shortIntro != null) {
            stmt.bindString(4, shortIntro);
        }
 
        String cover = entity.getCover();
        if (cover != null) {
            stmt.bindString(5, cover);
        }
        stmt.bindLong(6, entity.getHasCp() ? 1L: 0L);
        stmt.bindLong(7, entity.getLatelyFollower());
        stmt.bindDouble(8, entity.getRetentionRatio());
 
        String updated = entity.getUpdated();
        if (updated != null) {
            stmt.bindString(9, updated);
        }
 
        String lastRead = entity.getLastRead();
        if (lastRead != null) {
            stmt.bindString(10, lastRead);
        }
        stmt.bindLong(11, entity.getChaptersCount());
 
        String lastChapter = entity.getLastChapter();
        if (lastChapter != null) {
            stmt.bindString(12, lastChapter);
        }
        stmt.bindLong(13, entity.getIsUpdate() ? 1L: 0L);
        stmt.bindLong(14, entity.getIsLocal() ? 1L: 0L);
        stmt.bindLong(15, entity.getBookSortId());
    }

    @Override
    protected final void attachEntity(CollectBook entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    @Override
    public CollectBook readEntity(Cursor cursor, int offset) {
        CollectBook entity = new CollectBook( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // _id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // title
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // author
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // shortIntro
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // cover
            cursor.getShort(offset + 5) != 0, // hasCp
            cursor.getInt(offset + 6), // latelyFollower
            cursor.getDouble(offset + 7), // retentionRatio
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // updated
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // lastRead
            cursor.getInt(offset + 10), // chaptersCount
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // lastChapter
            cursor.getShort(offset + 12) != 0, // isUpdate
            cursor.getShort(offset + 13) != 0, // isLocal
            cursor.getInt(offset + 14) // bookSortId
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, CollectBook entity, int offset) {
        entity.set_id(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setTitle(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setAuthor(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setShortIntro(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setCover(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setHasCp(cursor.getShort(offset + 5) != 0);
        entity.setLatelyFollower(cursor.getInt(offset + 6));
        entity.setRetentionRatio(cursor.getDouble(offset + 7));
        entity.setUpdated(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setLastRead(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setChaptersCount(cursor.getInt(offset + 10));
        entity.setLastChapter(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setIsUpdate(cursor.getShort(offset + 12) != 0);
        entity.setIsLocal(cursor.getShort(offset + 13) != 0);
        entity.setBookSortId(cursor.getInt(offset + 14));
     }
    
    @Override
    protected final String updateKeyAfterInsert(CollectBook entity, long rowId) {
        return entity.get_id();
    }
    
    @Override
    public String getKey(CollectBook entity) {
        if(entity != null) {
            return entity.get_id();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(CollectBook entity) {
        return entity.get_id() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
