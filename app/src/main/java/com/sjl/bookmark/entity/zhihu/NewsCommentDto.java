package com.sjl.bookmark.entity.zhihu;

import java.util.ArrayList;

/**
 * 日报评论dto
 *
 * @author Kelly
 * @version 1.0.0
 * @filename NewsCommentDto.java
 * @time 2018/12/24 14:52
 * @copyright(C) 2018 song
 */
public class NewsCommentDto {

    private ArrayList<Comment> comments;

    public static class Comment {

        private String author;
        private int id;
        private String content;
        private int likes;
        private int time;
        private String avatar;

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getLikes() {
            return likes;
        }

        public void setLikes(int likes) {
            this.likes = likes;
        }

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }
}
