package com.sjl.bookmark.entity.zhihu;

import java.util.ArrayList;


public class NewsDto {
    public String date;
    private String jsonString;
    private Long createdTime;

    public ArrayList<TopStory> top_stories;

    public ArrayList<Story> stories;

    public NewsDto(String date, String jsonString, Long createdTime) {
        this.date = date;
        this.jsonString = jsonString;
        this.createdTime = createdTime;
    }
    public NewsDto() {
    }
    public String getDate() {
        return this.date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getJsonString() {
        return this.jsonString;
    }
    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }
    public Long getCreatedTime() {
        return this.createdTime;
    }
    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }



    public ArrayList<Story> getStories() {
        return stories;
    }

    public void setStories(ArrayList<Story> stories) {
        this.stories = stories;
    }

    public ArrayList<TopStory> getTop_stories() {
        return top_stories;
    }

    public void setTop_stories(ArrayList<TopStory> top_stories) {
        this.top_stories = top_stories;
    }
}
