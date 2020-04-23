package com.sjl.bookmark.entity.zhuishu.table;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

import java.io.Serializable;

/**
 * 书的章节链接(作为下载的进度数据)
 * 同时作为网络章节和本地章节 (没有找到更好分离两者的办法)
 */
@Entity
public class BookChapter implements Serializable {
    private static final long serialVersionUID = 56423411313L;
    /**
     * {
     mixToc: {
     _id: "53e56ee3654b85b5c5ffcc0b",
     book: "53e56ee335f79bb626a496c9",
     chaptersCount1: 1604,
     chaptersUpdated: "2018-11-18T01:28:39.055Z",
     chapters: [
     {
     title: "楔子",
     link: "http://book.my716.com/getBooks.aspx?method=content&bookId=41584&chapterFile=U_41584_201808311216507194_6956_1.txt",
     unreadble: false
     },
     {
     title: "第1章 三鬼爷(上)",
     link: "http://book.my716.com/getBooks.aspx?method=content&bookId=41584&chapterFile=U_41584_201707140856524336_4585_2.txt",
     unreadble: false
     },
     */

    @Id
    private String id;//章节id

    private String link;

    private String title;

    //所属的下载任务
    private String taskName;

    private boolean unreadble;

    //所属的书籍
    @Index
    private String bookId;

    //本地书籍参数


    //在书籍文件中的起始位置
    private long start;

    //在书籍文件中的终止位置
    private long end;


    @Generated(hash = 1326037902)
    public BookChapter(String id, String link, String title, String taskName,
                       boolean unreadble, String bookId, long start, long end) {
        this.id = id;
        this.link = link;
        this.title = title;
        this.taskName = taskName;
        this.unreadble = unreadble;
        this.bookId = bookId;
        this.start = start;
        this.end = end;
    }


    @Generated(hash = 1481387400)
    public BookChapter() {
    }


    @Override
    public String toString() {
        return "BookChapter{" +
                "id='" + id + '\'' +
                ", link='" + link + '\'' +
                ", title='" + title + '\'' +
                ", taskName='" + taskName + '\'' +
                ", unreadble=" + unreadble +
                ", bookId='" + bookId + '\'' +
                ", start=" + start +
                ", end=" + end +
                '}';
    }


    public String getId() {
        return this.id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public String getLink() {
        return this.link;
    }


    public void setLink(String link) {
        this.link = link;
    }


    public String getTitle() {
        return this.title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public String getTaskName() {
        return this.taskName;
    }


    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }


    public boolean getUnreadble() {
        return this.unreadble;
    }


    public void setUnreadble(boolean unreadble) {
        this.unreadble = unreadble;
    }


    public String getBookId() {
        return this.bookId;
    }


    public void setBookId(String bookId) {
        this.bookId = bookId;
    }


    public long getStart() {
        return this.start;
    }


    public void setStart(long start) {
        this.start = start;
    }


    public long getEnd() {
        return this.end;
    }


    public void setEnd(long end) {
        this.end = end;
    }
}