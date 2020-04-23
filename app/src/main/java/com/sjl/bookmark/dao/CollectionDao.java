package com.sjl.bookmark.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.sjl.bookmark.entity.table.Collection;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "COLLECTION".
*/
public class CollectionDao extends AbstractDao<Collection, Long> {

    public static final String TABLENAME = "COLLECTION";

    /**
     * Properties of entity Collection.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Title = new Property(1, String.class, "title", false, "TITLE");
        public final static Property Type = new Property(2, int.class, "type", false, "TYPE");
        public final static Property Href = new Property(3, String.class, "href", false, "HREF");
        public final static Property Date = new Property(4, java.util.Date.class, "date", false, "DATE");
        public final static Property Top = new Property(5, int.class, "top", false, "TOP");
        public final static Property Time = new Property(6, long.class, "time", false, "TIME");
    }


    public CollectionDao(DaoConfig config) {
        super(config);
    }
    
    public CollectionDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"COLLECTION\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"TITLE\" TEXT NOT NULL ," + // 1: title
                "\"TYPE\" INTEGER NOT NULL ," + // 2: type
                "\"HREF\" TEXT NOT NULL ," + // 3: href
                "\"DATE\" INTEGER NOT NULL ," + // 4: date
                "\"TOP\" INTEGER NOT NULL ," + // 5: top
                "\"TIME\" INTEGER NOT NULL );"); // 6: time
        // Add Indexes
        db.execSQL("CREATE INDEX " + constraint + "IDX_COLLECTION_TITLE ON \"COLLECTION\"" +
                " (\"TITLE\" ASC);");
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"COLLECTION\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Collection entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getTitle());
        stmt.bindLong(3, entity.getType());
        stmt.bindString(4, entity.getHref());
        stmt.bindLong(5, entity.getDate().getTime());
        stmt.bindLong(6, entity.getTop());
        stmt.bindLong(7, entity.getTime());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Collection entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getTitle());
        stmt.bindLong(3, entity.getType());
        stmt.bindString(4, entity.getHref());
        stmt.bindLong(5, entity.getDate().getTime());
        stmt.bindLong(6, entity.getTop());
        stmt.bindLong(7, entity.getTime());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public Collection readEntity(Cursor cursor, int offset) {
        Collection entity = new Collection( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // title
            cursor.getInt(offset + 2), // type
            cursor.getString(offset + 3), // href
            new java.util.Date(cursor.getLong(offset + 4)), // date
            cursor.getInt(offset + 5), // top
            cursor.getLong(offset + 6) // time
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Collection entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setTitle(cursor.getString(offset + 1));
        entity.setType(cursor.getInt(offset + 2));
        entity.setHref(cursor.getString(offset + 3));
        entity.setDate(new java.util.Date(cursor.getLong(offset + 4)));
        entity.setTop(cursor.getInt(offset + 5));
        entity.setTime(cursor.getLong(offset + 6));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Collection entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Collection entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Collection entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}