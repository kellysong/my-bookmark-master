package com.sjl.bookmark.entity.zhuishu.table;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename RecommendBook.java
 * @time 2018/12/11 15:30
 * @copyright(C) 2018 song
 */
@Entity
public class RecommendBook {
    @Id(autoincrement = true)
    private Long id;
    private String recommendId;//推荐书籍id
    private String title;
    private String author;
    private String cover;//封面，完整路径
    private String updateTime;//更新时间，默认7天更新一次

    //所属书籍
    private String bookId;

    @Generated(hash = 1147755175)
    public RecommendBook(Long id, String recommendId, String title, String author,
            String cover, String updateTime, String bookId) {
        this.id = id;
        this.recommendId = recommendId;
        this.title = title;
        this.author = author;
        this.cover = cover;
        this.updateTime = updateTime;
        this.bookId = bookId;
    }

    @Generated(hash = 279365346)
    public RecommendBook() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCover() {
        return this.cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getBookId() {
        return this.bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getRecommendId() {
        return this.recommendId;
    }

    public void setRecommendId(String recommendId) {
        this.recommendId = recommendId;
    }
}
