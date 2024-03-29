package com.sjl.bookmark.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.sjl.bookmark.entity.table.Account;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "ACCOUNT".
*/
public class AccountDao extends AbstractDao<Account, Long> {

    public static final String TABLENAME = "ACCOUNT";

    /**
     * Properties of entity Account.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property AccountType = new Property(1, int.class, "accountType", false, "ACCOUNT_TYPE");
        public final static Property AccountState = new Property(2, int.class, "accountState", false, "ACCOUNT_STATE");
        public final static Property AccountTitle = new Property(3, String.class, "accountTitle", false, "ACCOUNT_TITLE");
        public final static Property Username = new Property(4, String.class, "username", false, "USERNAME");
        public final static Property Password = new Property(5, String.class, "password", false, "PASSWORD");
        public final static Property Email = new Property(6, String.class, "email", false, "EMAIL");
        public final static Property Phone = new Property(7, String.class, "phone", false, "PHONE");
        public final static Property Remark = new Property(8, String.class, "remark", false, "REMARK");
        public final static Property Date = new Property(9, java.util.Date.class, "date", false, "DATE");
    }


    public AccountDao(DaoConfig config) {
        super(config);
    }
    
    public AccountDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"ACCOUNT\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"ACCOUNT_TYPE\" INTEGER NOT NULL ," + // 1: accountType
                "\"ACCOUNT_STATE\" INTEGER NOT NULL ," + // 2: accountState
                "\"ACCOUNT_TITLE\" TEXT NOT NULL ," + // 3: accountTitle
                "\"USERNAME\" TEXT NOT NULL ," + // 4: username
                "\"PASSWORD\" TEXT NOT NULL ," + // 5: password
                "\"EMAIL\" TEXT," + // 6: email
                "\"PHONE\" TEXT," + // 7: phone
                "\"REMARK\" TEXT," + // 8: remark
                "\"DATE\" INTEGER NOT NULL );"); // 9: date
        // Add Indexes
        db.execSQL("CREATE INDEX " + constraint + "IDX_ACCOUNT_ACCOUNT_TYPE ON \"ACCOUNT\"" +
                " (\"ACCOUNT_TYPE\" ASC);");
        db.execSQL("CREATE INDEX " + constraint + "IDX_ACCOUNT_ACCOUNT_STATE ON \"ACCOUNT\"" +
                " (\"ACCOUNT_STATE\" ASC);");
        db.execSQL("CREATE INDEX " + constraint + "IDX_ACCOUNT_ACCOUNT_TITLE ON \"ACCOUNT\"" +
                " (\"ACCOUNT_TITLE\" ASC);");
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"ACCOUNT\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Account entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getAccountType());
        stmt.bindLong(3, entity.getAccountState());
        stmt.bindString(4, entity.getAccountTitle());
        stmt.bindString(5, entity.getUsername());
        stmt.bindString(6, entity.getPassword());
 
        String email = entity.getEmail();
        if (email != null) {
            stmt.bindString(7, email);
        }
 
        String phone = entity.getPhone();
        if (phone != null) {
            stmt.bindString(8, phone);
        }
 
        String remark = entity.getRemark();
        if (remark != null) {
            stmt.bindString(9, remark);
        }
        stmt.bindLong(10, entity.getDate().getTime());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Account entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getAccountType());
        stmt.bindLong(3, entity.getAccountState());
        stmt.bindString(4, entity.getAccountTitle());
        stmt.bindString(5, entity.getUsername());
        stmt.bindString(6, entity.getPassword());
 
        String email = entity.getEmail();
        if (email != null) {
            stmt.bindString(7, email);
        }
 
        String phone = entity.getPhone();
        if (phone != null) {
            stmt.bindString(8, phone);
        }
 
        String remark = entity.getRemark();
        if (remark != null) {
            stmt.bindString(9, remark);
        }
        stmt.bindLong(10, entity.getDate().getTime());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public Account readEntity(Cursor cursor, int offset) {
        Account entity = new Account( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getInt(offset + 1), // accountType
            cursor.getInt(offset + 2), // accountState
            cursor.getString(offset + 3), // accountTitle
            cursor.getString(offset + 4), // username
            cursor.getString(offset + 5), // password
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // email
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // phone
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // remark
            new java.util.Date(cursor.getLong(offset + 9)) // date
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Account entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setAccountType(cursor.getInt(offset + 1));
        entity.setAccountState(cursor.getInt(offset + 2));
        entity.setAccountTitle(cursor.getString(offset + 3));
        entity.setUsername(cursor.getString(offset + 4));
        entity.setPassword(cursor.getString(offset + 5));
        entity.setEmail(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setPhone(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setRemark(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setDate(new java.util.Date(cursor.getLong(offset + 9)));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Account entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Account entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Account entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
