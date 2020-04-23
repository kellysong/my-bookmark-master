package com.sjl.bookmark.dao.impl;


import android.content.Context;
import android.text.TextUtils;

import com.sjl.bookmark.dao.AccountDao;
import com.sjl.bookmark.entity.table.Account;
import com.sjl.bookmark.dao.db.DatabaseManager;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename AccountService.java
 * @time 2018/3/8 14:23
 * @copyright(C) 2018 song
 */
public class AccountService {
    private static AccountService instance;
    private AccountDao accountDao;

    private AccountService(Context context) {
        this.accountDao =  DatabaseManager.getInstance(context).getDaoSession().getAccountDao();
    }


    public static AccountService getInstance(Context context) {
        if (instance == null) {
            instance = new AccountService(context);
        }
        return instance;
    }

    /**
     * 查询单个账号
     *
     * @param id
     * @return
     */
    public Account loadAccount(long id) {
        if (!TextUtils.isEmpty(id + "")) {
            return accountDao.load(id);
        }
        return null;
    }

    /**
     * 根据查询条件,返回数据列表
     *
     * @param where  条件
     * @param params 参数
     * @return 数据列表
     */
    public List<Account> queryAccount(String where, String... params) {
        return accountDao.queryRaw(where, params);
    }

    /**
     * 分压查询账号
     * @param accountTitle 账号所属网站
     * @param username 账号
     * @param pageOffset
     * @param pageSize
     * @return
     */
    public List<Account> queryAccountByPage(String accountTitle,String username,int pageOffset, int pageSize) {
        if (pageSize <= 0 || pageSize >=50) {
            pageSize = 12;
        }
        QueryBuilder<Account> builder = accountDao.queryBuilder();

        builder.whereOr(AccountDao.Properties.AccountTitle.like("%" + accountTitle + "%"),AccountDao.Properties.Username.like("%" + username + "%"));
        //表示从第nBaseRow行(基于0的索引)(包括该行)开始,取其后的nNumRecord  条记录
        List<Account> Accounts = builder.offset((pageOffset -1) * pageSize).limit(pageSize).orderAsc(AccountDao.Properties.Id).list();
        return Accounts;
    }


    /**
     * 根据实体插入（id不同时新增）或修改信息
     *
     * @param account
     * @return
     */
    public long saveAccount(Account account) {
        return accountDao.insertOrReplace(account);
    }



    /**
     * 根据id,删除数据
     *
     * @param id
     */
    public void deleteAccount(long id) {
        accountDao.deleteByKey(id);
    }

    /**
     * 根据实体删除信息
     *
     * @param account
     */
    public void deleteAccount(Account account) {
        accountDao.delete(account);
    }
}
