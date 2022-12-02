package com.sjl.bookmark.widget.popupview;


import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.AttachPopupView;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.sjl.bookmark.R;
import com.sjl.bookmark.dao.util.BookmarkParse;
import com.sjl.bookmark.entity.BookmarkMenu;
import com.sjl.bookmark.ui.listener.OnItemSelectListener;

import java.util.List;


/**
 * Bookmark菜单
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookmarkMenuPopupView
 * @time 2022/12/2 16:25
 * @copyright(C) 2022 song
 */
public class BookmarkMenuPopupView {

    private AttachPopupView attachPopupView;

    private OnItemSelectListener<BookmarkMenu> onItemSelectListener;

    public BookmarkMenuPopupView(Activity activity, View v) {
        initPopupView(activity, v);
    }

    private void initPopupView(Activity activity, View v) {
        Context context;
        if (activity != null) {
            context = activity;
        } else {
            throw new RuntimeException("activity和fragment不能为空");
        }
        List<BookmarkMenu> bookmarkMenus = BookmarkParse.Companion.listBookmarkMenu(context);
        String[] strings = new String[bookmarkMenus.size()];
        for (int i = 0; i < strings.length; i++) {
            strings[i] = bookmarkMenus.get(i).getName();
        }


        attachPopupView = new XPopup.Builder(context)
                .hasShadowBg(false)
                .offsetY(-context.getResources().getDimensionPixelOffset(R.dimen.dp_10))
                .atView(v)
                .asAttachList(strings,
                        null,
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                if (onItemSelectListener == null){
                                    return;
                                }
                                onItemSelectListener.onSelect(position, bookmarkMenus.get(position));
                            }
                        }, 0, 0, Gravity.LEFT);

    }


    public void show() {
        attachPopupView.show();
    }

    public void setOnSelectListener(OnItemSelectListener<BookmarkMenu> onItemSelectListener) {
        this.onItemSelectListener = onItemSelectListener;
    }
}
