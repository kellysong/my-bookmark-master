package com.sjl.bookmark.ui.listener;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename OnSelectListener
 * @time 2022/12/2 17:25
 * @copyright(C) 2022 song
 */
public interface OnItemSelectListener<T> {
    void onSelect(int position, T item);
}
