package com.sjl.bookmark.entity.table;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;
import java.util.Date;

/**
 * 文章浏览足迹表
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BrowseTrackDaoImpl.java
 * @time 2018/12/23 16:12
 * @copyright(C) 2018 song
 */
@Entity
public class BrowseTrack {
    @Id(autoincrement = true)
    private Long id;
    @NotNull
    private String articleId;//文章id,可能相同
    @NotNull
    @Index
    private int type; //0玩安卓，1知乎日报
    /**
     * 链接
     */
    private String href;

    /**
     * 链接文本(标题)
     */
    @Index
    private String text;
    /**
     * 分类
     */
    private String category;
    /**
     * 创建时间
     */
    private java.util.Date createTime;


    @Generated(hash = 887519129)
    public BrowseTrack(Long id, @NotNull String articleId, int type, String href,
            String text, String category, java.util.Date createTime) {
        this.id = id;
        this.articleId = articleId;
        this.type = type;
        this.href = href;
        this.text = text;
        this.category = category;
        this.createTime = createTime;
    }
    @Generated(hash = 761130463)
    public BrowseTrack() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getArticleId() {
        return this.articleId;
    }
    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public String getHref() {
        return this.href;
    }
    public void setHref(String href) {
        this.href = href;
    }
    public String getText() {
        return this.text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public String getCategory() {
        return this.category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public java.util.Date getCreateTime() {
        return this.createTime;
    }
    public void setCreateTime(java.util.Date createTime) {
        this.createTime = createTime;
    }

}
