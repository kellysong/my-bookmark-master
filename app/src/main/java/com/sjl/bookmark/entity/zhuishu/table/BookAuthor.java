package com.sjl.bookmark.entity.zhuishu.table;

/**
 *
 * 书籍作者
 */
public class BookAuthor {
    /**
     * _id : 553136ba70feaa764a096f6f
     * avatar : /avatar/26/eb/26ebf8ede76d7f52cd377960bd66383b
     * nickname : 九歌
     * activityAvatar :
     * type : normal
     * lv : 8
     * gender : female
     */

    private String _id;

    private String avatar;
    private String nickname;
    private String activityAvatar;
    private String type;
    private int lv;
    private String gender;


    public BookAuthor(String _id, String avatar, String nickname,
            String activityAvatar, String type, int lv, String gender) {
        this._id = _id;
        this.avatar = avatar;
        this.nickname = nickname;
        this.activityAvatar = activityAvatar;
        this.type = type;
        this.lv = lv;
        this.gender = gender;
    }


    public BookAuthor() {
    }


    @Override
    public String toString() {
        return "AuthorBean{" +
                "_id='" + _id + '\'' +
                ", avatar='" + avatar + '\'' +
                ", nickname='" + nickname + '\'' +
                ", activityAvatar='" + activityAvatar + '\'' +
                ", type='" + type + '\'' +
                ", lv=" + lv +
                ", gender='" + gender + '\'' +
                '}';
    }


    public String get_id() {
        return this._id;
    }


    public void set_id(String _id) {
        this._id = _id;
    }


    public String getAvatar() {
        return this.avatar;
    }


    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }


    public String getNickname() {
        return this.nickname;
    }


    public void setNickname(String nickname) {
        this.nickname = nickname;
    }


    public String getActivityAvatar() {
        return this.activityAvatar;
    }


    public void setActivityAvatar(String activityAvatar) {
        this.activityAvatar = activityAvatar;
    }


    public String getType() {
        return this.type;
    }


    public void setType(String type) {
        this.type = type;
    }


    public int getLv() {
        return this.lv;
    }


    public void setLv(int lv) {
        this.lv = lv;
    }


    public String getGender() {
        return this.gender;
    }


    public void setGender(String gender) {
        this.gender = gender;
    }
}