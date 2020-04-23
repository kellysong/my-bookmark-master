package com.sjl.bookmark.entity.zhuishu;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookSummaryDto.java
 * @time 2019/7/30 16:16
 * @copyright(C) 2019 song
 */
public class BookSummaryDto {

    /**
     * _id : 5c6fd8cff7da7c543dc9998d
     * isCharge : false
     * name : 优质书源
     * lastChapter : 第913章 是南宫辰维
     * updated : 2019-07-29T15:07:05.140Z
     * source : zhuishuvip
     * link : http://vip.zhuishushenqi.com/toc/5c6fd8cff7da7c543dc9998d
     * starting : true
     * chaptersCount : 913
     * host : vip.zhuishushenqi.com
     */

    private String _id;
    private boolean isCharge;
    private String name;
    private String lastChapter;
    private String updated;
    private String source;
    private String link;
    private boolean starting;
    private int chaptersCount;
    private String host;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public boolean isIsCharge() {
        return isCharge;
    }

    public void setIsCharge(boolean isCharge) {
        this.isCharge = isCharge;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastChapter() {
        return lastChapter;
    }

    public void setLastChapter(String lastChapter) {
        this.lastChapter = lastChapter;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public boolean isStarting() {
        return starting;
    }

    public void setStarting(boolean starting) {
        this.starting = starting;
    }

    public int getChaptersCount() {
        return chaptersCount;
    }

    public void setChaptersCount(int chaptersCount) {
        this.chaptersCount = chaptersCount;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
