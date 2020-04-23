package com.sjl.bookmark.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Android知识体系
 */
public class Category {

    private int id;
    private String name;
    private int courseId;
    private int parentChapterId;
    private int order;
    private int visible;
    private List<ChildrenBean> children;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getParentChapterId() {
        return parentChapterId;
    }

    public void setParentChapterId(int parentChapterId) {
        this.parentChapterId = parentChapterId;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getVisible() {
        return visible;
    }

    public void setVisible(int visible) {
        this.visible = visible;
    }

    public List<ChildrenBean> getChildren() {
        return children;
    }

    public void setChildren(List<ChildrenBean> children) {
        this.children = children;
    }


    public static class ChildrenBean implements Parcelable {
        /**
         * id : 60
         * name : Android Studio相关
         * courseId : 13
         * parentChapterId : 150
         * order : 1000
         * visible : 1
         * children : []
         */

        private int id;
        private String name;
        private int courseId;
        private int parentChapterId;
        private int order;
        private int visible;
        private List<ChildrenBean> children;

        public ChildrenBean() {

        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getCourseId() {
            return courseId;
        }

        public void setCourseId(int courseId) {
            this.courseId = courseId;
        }

        public int getParentChapterId() {
            return parentChapterId;
        }

        public void setParentChapterId(int parentChapterId) {
            this.parentChapterId = parentChapterId;
        }

        public int getOrder() {
            return order;
        }

        public void setOrder(int order) {
            this.order = order;
        }

        public int getVisible() {
            return visible;
        }

        public void setVisible(int visible) {
            this.visible = visible;
        }

        public List<?> getChildren() {
            return children;
        }

        public void setChildren(List<ChildrenBean> children) {
            this.children = children;
        }


        public ChildrenBean(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeString(name);
            dest.writeInt(courseId);
            dest.writeInt(parentChapterId);
            dest.writeInt(order);
            dest.writeInt(visible);
            dest.writeList(children);
        }

        /**
         * 1. 实现Parcelable接口
         2. 添加实体属性
         3. 覆写writeToParcel(Parcel dest, int flags)方法，指定写入Parcel类的数据。
         4. 创建Parcelable.Creator静态对象，有两个方法createFromParcel(Parcel in)与newArray(int size)，前者指定如何从Parcel中读取出数据对象，后者创建一个数组。
         5. 覆写describeContents方法，默认返回0。
         */
        public static final Parcelable.Creator<ChildrenBean> CREATOR = new Parcelable.Creator<ChildrenBean>() {

            @Override
            public ChildrenBean createFromParcel(Parcel source) {

                ChildrenBean model = new ChildrenBean();
                model.id = source.readInt();
                model.name = source.readString();
                model.courseId = source.readInt();
                model.parentChapterId = source.readInt();
                model.order = source.readInt();
                model.visible = source.readInt();
                // 必须实例化
                model.children = new ArrayList<ChildrenBean>();
                source.readList(model.children, getClass().getClassLoader());
                return model;
            }

            @Override
            public ChildrenBean[] newArray(int size) {
                return new ChildrenBean[size];
            }
        };
    }
}
