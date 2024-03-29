package com.sjl.bookmark.entity.zhuishu;

import com.sjl.bookmark.entity.zhuishu.table.BookChapter;

import java.util.List;

/**
 * 书籍章节传输dto
 *  持有BookChapter
 * @author Kelly
 * @version 1.0.0
 * @filename BookChapterDto.java
 * @time 2018/12/1 19:40
 * @copyright(C) 2018 song
 */
public class BookChapterDto extends BaseZhuiShu {

    /**
     * mixToc : {"_id":"572072a2e3ee1dcc0accdb9a","book":"57206c3539a913ad65d35c7b","chaptersCount1":288,"chaptersUpdated":"2017-05-09T10:02:34.705Z","chapters":[{"title":"第一章 他叫白小纯","link":"http://read.qidian.com/chapter/rJgN8tJ_cVdRGoWu-UQg7Q2/6jr-buLIUJSaGfXRMrUjdw2","unreadble":false}
     */

    private MixTocBean mixToc;

    public void setMixToc(MixTocBean mixToc) {
        this.mixToc = mixToc;
    }

    public MixTocBean getMixToc() {
        return mixToc;
    }

    public static class MixTocBean {
        /**
         * _id : 572072a2e3ee1dcc0accdb9a
         * book : 57206c3539a913ad65d35c7b
         * chaptersCount1 : 288
         * chaptersUpdated : 2017-05-09T10:02:34.705Z
         * chapters : [{"title":"第一章 他叫白小纯","link":"http://read.qidian.com/chapter/rJgN8tJ_cVdRGoWu-UQg7Q2/6jr-buLIUJSaGfXRMrUjdw2","unreadble":false},{"title":"第二章 火灶房","link":"http://read.qidian.com/chapter/rJgN8tJ_cVdRGoWu-UQg7Q2/wty0QJyYhjm2uJcMpdsVgA2","unreadble":false}
         */

        private String _id;
        private String book;
        private int chaptersCount1;
        private String chaptersUpdated;
        private String updated;
        private List<BookChapter> chapters;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getBook() {
            return book;
        }

        public void setBook(String book) {
            this.book = book;
        }

        public int getChaptersCount1() {
            return chaptersCount1;
        }

        public void setChaptersCount1(int chaptersCount1) {
            this.chaptersCount1 = chaptersCount1;
        }

        public String getChaptersUpdated() {
            return chaptersUpdated;
        }

        public void setChaptersUpdated(String chaptersUpdated) {
            this.chaptersUpdated = chaptersUpdated;
        }

        public String getUpdated() {
            return updated;
        }

        public void setUpdated(String updated) {
            this.updated = updated;
        }

        public List<BookChapter> getChapters() {
            return chapters;
        }

        public void setChapters(List<BookChapter> chapters) {
            this.chapters = chapters;
        }
    }
}
