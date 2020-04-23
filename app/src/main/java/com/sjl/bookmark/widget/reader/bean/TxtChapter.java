package com.sjl.bookmark.widget.reader.bean;

/**
 * 文章文本
 */
public class TxtChapter {

    //章节所属的小说(网络)
    public  String bookId;
    //章节的链接(网络)
    public String link;

    //章节名(共用)
    public String title;

    //章节内容在文章中的起始位置(本地)
    public long start;
    //章节内容在文章中的终止位置(本地)
    public long end;

    @Override
    public String toString() {
        return "TxtChapter{" +
                "title='" + title + '\'' +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
