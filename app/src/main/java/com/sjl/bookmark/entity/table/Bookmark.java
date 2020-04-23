package com.sjl.bookmark.entity.table;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;

import java.io.Serializable;
import java.util.Date;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit.

/**
 * Entity mapped to table "BOOKMARK".
 */
@Entity
public class Bookmark implements Serializable{

    //File–>Settings–>Editor–>Inspections–>Java–>Serialization issues–>Serializable class without ‘serialVersionUID’ 勾选中该选项即可。

    private static final long serialVersionUID = -1577992649320057216L;
    @Id(autoincrement = true)
    private Long id;
    private int type; //0标题，1条目

    @NotNull
    @Index
    private String title;
    private String href;
    private String icon;

    @Index
    private String text;//链接文本

    @NotNull
    private java.util.Date date; //插入数据库时间

    @Generated(hash = 1206029275)
    public Bookmark() {
    }

    public Bookmark(Long id) {
        this.id = id;
    }

    @Generated(hash = 703156870)
    public Bookmark(Long id, int type, @NotNull String title, String href, String icon, String text, @NotNull java.util.Date date) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.href = href;
        this.icon = icon;
        this.text = text;
        this.date = date;
    }

    public Bookmark(int type, String title) {
        this.type = type;
        this.title = title;
        this.date = new Date();
    }

    public Bookmark(String title, String href, String icon, String text) {
        this.type = 1;
        this.title = title;
        this.href = href;
        this.icon = icon;
        this.text = text;
        this.date = new Date();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @NotNull
    public String getTitle() {
        return title;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setTitle(@NotNull String title) {
        this.title = title;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @NotNull
    public java.util.Date getDate() {
        return date;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setDate(@NotNull java.util.Date date) {
        this.date = date;
    }

}