package com.sjl.bookmark.widget;


public interface DragGridListener {
    /**
     * 重新排列数据
     *
     * @param oldPosition
     * @param newPosition
     */
    void reorderItems(int oldPosition, int newPosition);


    /**
     * 设置某个item隐藏
     *
     * @param hidePosition
     */
    void setHideItem(int hidePosition);


    /**
     * 删除某个item
     *
     * @param deletePosition
     */
    void removeItem(int deletePosition);

    /**
     * 设置点击打开后的item移动到第一位置
     *
     * @param openPosition
     */
    void setItemToFirst(int openPosition);

    /**
     * 完成拖拽动画
     */
    void finishDragGrid();

    /**
     * 显示删除按钮
     */
    void showDeleteButton();
}
