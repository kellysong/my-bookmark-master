package com.sjl.bookmark.entity.zhihu;

import java.util.ArrayList;

/**
 * 新闻
 * 转换后列表
 *
 * @author Kelly
 * @version 1.0.0
 * @filename NewsList.java
 * @time 2018/12/19 10:02
 * @copyright(C) 2018 song
 */
public class NewsList {
    private int itemType;
    //轮播图
    public ArrayList<TopStory> top_stories;
    //今日热闻
    private String today;
    //日期
    private String date;
    //列表
    private String image;
    private int type;
    private int id;
    private String ga_prefix;
    private String title;


    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

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

    public String getToday() {
        return today;
    }

    public void setToday(String today) {
        this.today = today;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<TopStory> getTop_stories() {
        return top_stories;
    }

    public void setTop_stories(ArrayList<TopStory> top_stories) {
        this.top_stories = top_stories;
    }
}
