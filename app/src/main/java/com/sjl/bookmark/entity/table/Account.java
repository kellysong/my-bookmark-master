package com.sjl.bookmark.entity.table;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * 账户实体
 *
 * @author Kelly
 * @version 1.0.0
 * @filename Account.java
 * @time 2018/3/8 13:46
 * @copyright(C) 2018 song
 */
@Entity
public class Account {
    @Id(autoincrement = true)
    private Long id;

    @NotNull
    @Index
    private int accountType; //0安全，1娱乐，2社会，3开发，4其它
    @NotNull
    @Index
    private int accountState; //0在用，1闲置，2作废

    @NotNull
    @Index
    private String accountTitle;
    @NotNull
    private String username;
    @NotNull
    private String password;

    private String email;
    private String phone;
    private String remark;

    @NotNull
    private java.util.Date date; //插入数据库时间

    @Generated(hash = 340318624)
    public Account(Long id, int accountType, int accountState,
            @NotNull String accountTitle, @NotNull String username,
            @NotNull String password, String email, String phone, String remark,
            @NotNull java.util.Date date) {
        this.id = id;
        this.accountType = accountType;
        this.accountState = accountState;
        this.accountTitle = accountTitle;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.remark = remark;
        this.date = date;
    }

    @Generated(hash = 882125521)
    public Account() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getAccountType() {
        return this.accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public int getAccountState() {
        return this.accountState;
    }

    public void setAccountState(int accountState) {
        this.accountState = accountState;
    }

    public String getAccountTitle() {
        return this.accountTitle;
    }

    public void setAccountTitle(String accountTitle) {
        this.accountTitle = accountTitle;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public java.util.Date getDate() {
        return this.date;
    }

    public void setDate(java.util.Date date) {
        this.date = date;
    }


}
