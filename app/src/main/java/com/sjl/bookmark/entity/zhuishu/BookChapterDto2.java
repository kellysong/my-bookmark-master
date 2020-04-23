package com.sjl.bookmark.entity.zhuishu;

import java.util.List;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookChapterDto2.java
 * @time 2019/7/26 14:16
 * @copyright(C) 2019 song
 */
public class BookChapterDto2 {

    private String _id;
    private String name;
    private String updated;
    private String source;
    private String book;
    private String link;
    private String host;
    private String bookName;

    private List<ChaptersBean> chapters;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public List<ChaptersBean> getChapters() {
        return chapters;
    }

    public void setChapters(List<ChaptersBean> chapters) {
        this.chapters = chapters;
    }

    /**
     * title : 第1章 双倍还
     * link : http://vip.zhuishushenqi.com/chapter/5c6fd8cf769d7354ae266af9?cv=1550833871883
     * id : 5c6fd8cf769d7354ae266af9
     * time : 0
     * totalpage : 0
     * partsize : 0
     * currency : 10
     * order : 1
     * unreadble : false
     * isVip : false
     * chapterCover :
     */

    public static class ChaptersBean {
        private String title;
        private String link;
        private String id;
        private int time;
        private int totalpage;
        private int partsize;
        private int currency;
        private int order;
        private boolean unreadble;
        private boolean isVip;
        private String chapterCover;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }

        public int getTotalpage() {
            return totalpage;
        }

        public void setTotalpage(int totalpage) {
            this.totalpage = totalpage;
        }

        public int getPartsize() {
            return partsize;
        }

        public void setPartsize(int partsize) {
            this.partsize = partsize;
        }

        public int getCurrency() {
            return currency;
        }

        public void setCurrency(int currency) {
            this.currency = currency;
        }

        public int getOrder() {
            return order;
        }

        public void setOrder(int order) {
            this.order = order;
        }

        public boolean isUnreadble() {
            return unreadble;
        }

        public void setUnreadble(boolean unreadble) {
            this.unreadble = unreadble;
        }

        public boolean isIsVip() {
            return isVip;
        }

        public void setIsVip(boolean isVip) {
            this.isVip = isVip;
        }

        public String getChapterCover() {
            return chapterCover;
        }

        public void setChapterCover(String chapterCover) {
            this.chapterCover = chapterCover;
        }
    }
}
