package com.sjl.bookmark.entity.table;

import androidx.annotation.NonNull;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;
import java.util.Calendar;

/**
 * 收藏表
 *
 * @author Kelly
 * @version 1.0.0
 * @filename Collection.java
 * @time 2018/3/25 15:55
 * @copyright(C) 2018 song
 */
@Entity
public class Collection implements Serializable,Comparable<Collection> {

    private static final long serialVersionUID = -2538181885012822427L;

    @Id(autoincrement = true)
    private Long id;
    @NotNull
    @Index
    private String title;//标题

    @NotNull
    private int type;//0网页，1笔记，2其它

    @NotNull
    private String href;//链接,当本地笔记时，改字段为笔记内容

    @NotNull
    private java.util.Date date; //插入数据库时间

    /**
     * 是否置顶,1置顶，0不置顶
     */
    public int top;
    /**
     * 置顶时间
     **/
    public long time;

    @Transient
    private boolean selectItem;//该字段不参与成数据库表的列

    @Generated(hash = 1827648621)
    public Collection(Long id, @NotNull String title, int type,
            @NotNull String href, @NotNull java.util.Date date, int top,
            long time) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.href = href;
        this.date = date;
        this.top = top;
        this.time = time;
    }

    @Generated(hash = 1149123052)
    public Collection() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public java.util.Date getDate() {
        return this.date;
    }

    public void setDate(java.util.Date date) {
        this.date = date;
    }

    public boolean isSelectItem() {
        return selectItem;
    }

    public void setSelectItem(boolean selectItem) {
        this.selectItem = selectItem;
    }


    /**
     * 根据时间对比
     * */
    public static int compareToTime(long lhs, long rhs) {
        Calendar cLhs = Calendar.getInstance();
        Calendar cRhs = Calendar.getInstance();
        cLhs.setTimeInMillis(lhs);
        cRhs.setTimeInMillis(rhs);
        return cLhs.compareTo(cRhs);
    }

    @Override
    public int compareTo(@NonNull Collection another) {

        /**置顶判断 ArrayAdapter是按照升序从上到下排序的，就是默认的自然排序
         * 如果是相等的情况下返回0，包括都置顶或者都不置顶，返回0的情况下要再做判断，拿它们置顶时间进行判断
         * 如果是不相等的情况下，otherSession是置顶的，则当前session是非置顶的，应该在otherSession下面，所以返回1
         * 同样，session是置顶的，则当前otherSession是非置顶的，应该在otherSession上面，所以返回-1
         * */
        int result = 0 - (top - another.getTop());
        if (result == 0) {
            result = 0 -compareToTime(time, another.getTime());
            if (result == 0){//默认按照日期升序排序
                return 0 -date.compareTo(another.getDate());
            }
        }
        return result;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Collection that = (Collection) o;

        if (type != that.type) return false;
        if (top != that.top) return false;
        if (time != that.time) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (href != null ? !href.equals(that.href) : that.href != null) return false;
        return date != null ? date.equals(that.date) : that.date == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + type;
        result = 31 * result + (href != null ? href.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + top;
        result = 31 * result + (int) (time ^ (time >>> 32));
        return result;
    }
}
