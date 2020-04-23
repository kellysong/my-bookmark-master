package com.sjl.bookmark.entity.zhuishu.table;

import android.os.Parcel;
import android.os.Parcelable;

import com.sjl.bookmark.dao.BookChapterDao;
import com.sjl.bookmark.dao.CollectBookDao;
import com.sjl.bookmark.dao.DaoSession;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

/**
 * 收藏的书籍
 */
@Entity
public class CollectBook implements Parcelable {


    /**
     * _id : 53663ae356bdc93e49004474
     * title : 逍遥派
     * author : 白马出淤泥
     * shortIntro : 金庸武侠中有不少的神秘高手，书中或提起名字，或不曾提起，总之他们要么留下了绝世秘笈，要么就名震武林。 独孤九剑的创始者，独孤求败，他真的只创出九剑吗？ 残本葵花...
     * cover : /cover/149273897447137
     * hasCp : true
     * latelyFollower : 60213
     * retentionRatio : 22.87
     * updated : 2017-05-07T18:24:34.720Z
     * <p>
     * chaptersCount : 1660
     * lastChapter : 第1659章 朱长老
     */
    @Id
    private String _id; // 本地书籍中，path 的 md5 值作为本地书籍的 id
    private String title;//书名
    private String author;
    private String shortIntro;
    private String cover; //网络图书封面url, 在本地书籍txt中，该字段作为本地文件的路径
    private boolean hasCp;
    private int latelyFollower;
    private double retentionRatio;
    //最新更新日期
    private String updated;
    //最新阅读日期
    private String lastRead;
    private int chaptersCount;
    //最后章节
    private String lastChapter;
    //是否更新或未阅读
    private boolean isUpdate = true;
    //是否是本地文件
    private boolean isLocal = false;

    @ToMany(referencedJoinProperty = "bookId")
    private List<BookChapter> bookChapterList;
    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /**
     * Used for active entity operations.
     */
    @Generated(hash = 1185895962)
    private transient CollectBookDao myDao;

    /**
     * 排序id,判断在书架的位置
     */
    private int bookSortId;

    public CollectBook() {
    }


    public int getBookSortId() {
        return bookSortId;
    }

    public void setBookSortId(int bookSortId) {
        this.bookSortId = bookSortId;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getShortIntro() {
        return shortIntro;
    }

    public void setShortIntro(String shortIntro) {
        this.shortIntro = shortIntro;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public boolean isHasCp() {
        return hasCp;
    }

    public void setHasCp(boolean hasCp) {
        this.hasCp = hasCp;
    }

    public int getLatelyFollower() {
        return latelyFollower;
    }

    public void setLatelyFollower(int latelyFollower) {
        this.latelyFollower = latelyFollower;
    }

    public double getRetentionRatio() {
        return retentionRatio;
    }

    public void setRetentionRatio(double retentionRatio) {
        this.retentionRatio = retentionRatio;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public int getChaptersCount() {
        return chaptersCount;
    }

    public void setChaptersCount(int chaptersCount) {
        this.chaptersCount = chaptersCount;
    }

    public String getLastChapter() {
        return lastChapter;
    }

    public void setLastChapter(String lastChapter) {
        this.lastChapter = lastChapter;
    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setUpdate(boolean update) {
        isUpdate = update;
    }

    public boolean getHasCp() {
        return this.hasCp;
    }

    public boolean getIsUpdate() {
        return this.isUpdate;
    }

    public void setIsUpdate(boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }

    public String getLastRead() {
        return lastRead;
    }

    public void setLastRead(String lastRead) {
        this.lastRead = lastRead;
    }

    public void setBookChapters(List<BookChapter> beans) {
        bookChapterList = beans;
        for (BookChapter bean : bookChapterList) {
            bean.setBookId(get_id());
        }
    }

    public boolean getIsLocal() {
        return this.isLocal;
    }

    public void setIsLocal(boolean isLocal) {
        this.isLocal = isLocal;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this._id);
        dest.writeString(this.title);
        dest.writeString(this.author);
        dest.writeString(this.shortIntro);
        dest.writeString(this.cover);
        dest.writeByte(this.hasCp ? (byte) 1 : (byte) 0);
        dest.writeInt(this.latelyFollower);
        dest.writeDouble(this.retentionRatio);
        dest.writeString(this.updated);
        dest.writeString(this.lastRead);
        dest.writeInt(this.chaptersCount);
        dest.writeString(this.lastChapter);
        dest.writeByte(this.isUpdate ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isLocal ? (byte) 1 : (byte) 0);
        dest.writeInt(this.bookSortId);
    }

    protected CollectBook(Parcel in) {
        this._id = in.readString();
        this.title = in.readString();
        this.author = in.readString();
        this.shortIntro = in.readString();
        this.cover = in.readString();
        this.hasCp = in.readByte() != 0;
        this.latelyFollower = in.readInt();
        this.retentionRatio = in.readDouble();
        this.updated = in.readString();
        this.lastRead = in.readString();
        this.chaptersCount = in.readInt();
        this.lastChapter = in.readString();
        this.isUpdate = in.readByte() != 0;
        this.isLocal = in.readByte() != 0;
        this.bookSortId = in.readInt();
    }


    @Generated(hash = 1891627594)
    public CollectBook(String _id, String title, String author, String shortIntro, String cover,
                       boolean hasCp, int latelyFollower, double retentionRatio, String updated, String lastRead,
                       int chaptersCount, String lastChapter, boolean isUpdate, boolean isLocal, int bookSortId) {
        this._id = _id;
        this.title = title;
        this.author = author;
        this.shortIntro = shortIntro;
        this.cover = cover;
        this.hasCp = hasCp;
        this.latelyFollower = latelyFollower;
        this.retentionRatio = retentionRatio;
        this.updated = updated;
        this.lastRead = lastRead;
        this.chaptersCount = chaptersCount;
        this.lastChapter = lastChapter;
        this.isUpdate = isUpdate;
        this.isLocal = isLocal;
        this.bookSortId = bookSortId;
    }

    public static final Creator<CollectBook> CREATOR = new Creator<CollectBook>() {
        @Override
        public CollectBook createFromParcel(Parcel source) {
            return new CollectBook(source);
        }

        @Override
        public CollectBook[] newArray(int size) {
            return new CollectBook[size];
        }
    };

    @Override
    public String toString() {
        return "CollectBook{" +
                "_id='" + _id + '\'' +
                ", title='" + title + '\'' +
                ", bookSortId=" + bookSortId +
                '}';
    }

    //下面是自动生成的代码


    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 2111891666)
    public List<BookChapter> getBookChapterList() {
        if (bookChapterList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            BookChapterDao targetDao = daoSession.getBookChapterDao();
            List<BookChapter> bookChapterListNew = targetDao._queryCollectBook_BookChapterList(_id);
            synchronized (this) {
                if (bookChapterList == null) {
                    bookChapterList = bookChapterListNew;
                }
            }
        }
        return bookChapterList;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 1077762221)
    public synchronized void resetBookChapterList() {
        bookChapterList = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 934270904)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getCollectBookDao() : null;
    }

    //下面是自定义
    public List<BookChapter> getBookChapters() {
        if (daoSession == null) {
            return bookChapterList;
        } else {
            return getBookChapterList();
        }
    }
}