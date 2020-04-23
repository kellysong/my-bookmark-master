package com.sjl.bookmark.entity.zhihu;

import java.util.ArrayList;
import java.util.List;

/**
 *日报详情
 */
public class NewsDetailDto {
    /**
     * body : xxxxxxxx
     * image_source : 武藤 杰洛特 / 知乎
     * title : 杨超越平行世界变形记
     * image : https://pic4.zhimg.com/v2-665f075f56fe67c19155cf55c68b9757.jpg
     * share_url : http://daily.zhihu.com/story/9704013
     * js : []
     * ga_prefix : 121916
     * images : ["https://pic1.zhimg.com/v2-fab6234a27a428055b337b56d9aeb46c.jpg"]
     * type : 0
     * id : 9704013
     * css : ["http://news-at.zhihu.com/css/news_qa.auto.css?v=4b3e3"]
     */

    private String body;
    private String contentBody;

    private String image_source;
    private String title;
    private String image;
    private String share_url;
    private String ga_prefix;
    private int type;
    private int id;
    private List<String> js;
    private List<String> images;
    private List<String> css;
    private ArrayList<Recommender> recommenders;

    public String getContentBody() {
        return contentBody;
    }

    public void setContentBody(String contentBody) {
        this.contentBody = contentBody;
    }

    public static class Recommender {

        private String avatar;

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getImage_source() {
        return image_source;
    }

    public void setImage_source(String image_source) {
        this.image_source = image_source;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getShare_url() {
        return share_url;
    }

    public void setShare_url(String share_url) {
        this.share_url = share_url;
    }

    public String getGa_prefix() {
        return ga_prefix;
    }

    public void setGa_prefix(String ga_prefix) {
        this.ga_prefix = ga_prefix;
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

    public List<String> getJs() {
        return js;
    }

    public void setJs(List<String> js) {
        this.js = js;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<String> getCss() {
        return css;
    }

    public void setCss(List<String> css) {
        this.css = css;
    }

    public ArrayList<Recommender> getRecommenders() {
        return recommenders;
    }

    public void setRecommenders(ArrayList<Recommender> recommenders) {
        this.recommenders = recommenders;
    }
}
