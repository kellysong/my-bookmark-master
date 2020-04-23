package com.sjl.bookmark.entity.zhuishu.table;

/**
 * BookHelpful
 */
public class BookHelpful {
    /**
     * total : 1
     * no : 5
     * yes : 6
     */
    private String _id;

    private int total;
    private int no;
    private int yes;
    public BookHelpful(String _id, int total, int no, int yes) {
        this._id = _id;
        this.total = total;
        this.no = no;
        this.yes = yes;
    }
    public BookHelpful() {
    }
    public String get_id() {
        return this._id;
    }
    public void set_id(String _id) {
        this._id = _id;
    }
    public int getTotal() {
        return this.total;
    }
    public void setTotal(int total) {
        this.total = total;
    }
    public int getNo() {
        return this.no;
    }
    public void setNo(int no) {
        this.no = no;
    }
    public int getYes() {
        return this.yes;
    }
    public void setYes(int yes) {
        this.yes = yes;
    }


}