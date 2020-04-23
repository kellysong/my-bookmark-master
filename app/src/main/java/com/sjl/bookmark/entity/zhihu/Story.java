package com.sjl.bookmark.entity.zhihu;

import java.util.List;

/**
 * 今日热闻
 */
public class Story {


    /**
     * images : ["https://pic1.zhimg.com/v2-4111872431a6c60bc8f1621fdb675bfc.jpg"]
     * type : 0
     * id : 9703969
     * ga_prefix : 121816
     * title : 想付多少钱随便，这是慈善吗？商家可不这么想
     */

    private int type;
    private int id;
    private String ga_prefix;
    private String title;
    private List<String> images;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGa_prefix() {
        return ga_prefix;
    }

    public void setGa_prefix(String ga_prefix) {
        this.ga_prefix = ga_prefix;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
