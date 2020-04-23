package com.sjl.bookmark.entity.zhihu;

/**
 *顶部轮播图
 */
public class TopStory {

    /**
     * image : https://pic3.zhimg.com/v2-7909fe0f1bfb5546c13599670b11ac5a.jpg
     * type : 0
     * id : 9703969
     * ga_prefix : 121816
     * title : 想付多少钱随便，这是慈善吗？商家可不这么想
     */

    private String image;
    private int type;
    private int id;
    private String ga_prefix;
    private String title;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

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
}
