package com.sjl.bookmark.entity;

import java.io.Serializable;

/**
 * 用户信息
 *
 * @author Kelly
 * @version 1.0.0
 * @filename UserInfo.java
 * @time 2018/11/29 10:03
 * @copyright(C) 2018 song
 */
public class UserInfo implements Serializable {
    private static final long serialVersionUID = -1589422710885196299L;

    private String avatar;//头像
    private String name;//昵称
    private String sex;//性别
    private String phone;//电话
    private String personality;//个性签名

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPersonality() {
        return personality;
    }

    public void setPersonality(String personality) {
        this.personality = personality;
    }
}
