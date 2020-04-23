package com.sjl.bookmark.entity;

import java.io.Serializable;

/**
 * 快递查询信息
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ExpressSearchInfo.java
 * @time 2018/5/2 10:53
 * @copyright(C) 2018 song
 */
public class ExpressSearchInfo implements Serializable {
    private String name;
    private String logo;
    private String code;
    //快递单号
    private String post_id;
    private String is_check;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getIs_check() {
        return is_check;
    }

    public void setIs_check(String is_check) {
        this.is_check = is_check;
    }
}
