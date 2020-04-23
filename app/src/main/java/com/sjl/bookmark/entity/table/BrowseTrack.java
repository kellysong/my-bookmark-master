package com.sjl.bookmark.entity.table;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

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
    @Generated(hash = 181846352)
    public BrowseTrack(Long id, @NotNull String articleId, int type) {
        this.id = id;
        this.articleId = articleId;
        this.type = type;
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

}
