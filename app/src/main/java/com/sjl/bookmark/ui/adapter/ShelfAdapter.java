package com.sjl.bookmark.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sjl.bookmark.R;
import com.sjl.bookmark.dao.impl.CollectBookDaoImpl;
import com.sjl.bookmark.entity.zhuishu.table.CollectBook;
import com.sjl.bookmark.net.HttpConstant;
import com.sjl.bookmark.widget.DragGridListener;
import com.sjl.bookmark.widget.DragGridView;
import com.sjl.core.util.log.LogUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ShelfAdapter extends BaseAdapter implements DragGridListener {
    private Context mContext;
    private List<CollectBook> bookList;
    private static LayoutInflater inflater = null;
    private int mHidePosition = -1;
    private CollectBookDaoImpl collectBookService = null;
    private OnDeleteItemListener itemDeleteListener = null;
    private DragGridView dragGridView;

    public ShelfAdapter(Context context, List<CollectBook> CollBookBeans) {
        this.mContext = context;
        this.bookList = CollBookBeans;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.collectBookService = new CollectBookDaoImpl(context);
    }

    @Override
    public int getCount() {
        //背景书架的draw需要用到item的高度
        if (bookList == null || bookList.size() == 0) {
            return 9;
        } else {
            return bookList.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return bookList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View contentView, ViewGroup arg2) {
//        LogUtils.i("getView1: position = " + position + ", childCount = " + arg2.getChildCount());
        final ViewHolder viewHolder;
        if (contentView == null) {
            contentView = inflater.inflate(R.layout.shelfitem, arg2, false);
            viewHolder = new ViewHolder(contentView);
            contentView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) contentView.getTag();
        }
        if (((DragGridView) arg2).isOnMeasure) {
            return contentView;
        }
//        LogUtils.w("getView2: position = " + position + ", childCount = " + arg2.getChildCount() + ",bookList:" + bookList.size());

        if (bookList != null && bookList.size() > position) {
            //DragGridView  解决复用问题
            if (position == mHidePosition) {
                contentView.setVisibility(View.INVISIBLE);
            } else {
                contentView.setVisibility(View.VISIBLE);
            }
            viewHolder.deleteItem_IB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeItem(position);
                }
            });
            if (dragGridView.isShowDeleteButton()) {
                viewHolder.deleteItem_IB.setVisibility(View.VISIBLE);
            } else {
                viewHolder.deleteItem_IB.setVisibility(View.INVISIBLE);
            }
            CollectBook collBookBean = bookList.get(position);
            viewHolder.name.setVisibility(View.VISIBLE);
            String fileName = collBookBean.getTitle();
            viewHolder.name.setText(fileName);
            ImageView bookCover = viewHolder.bookCover;
            if (!collBookBean.isLocal() && collBookBean.getCover() != null) {
                Glide.with(mContext).load(HttpConstant.ZHUISHU_IMG_BASE_URL + collBookBean.getCover()).into(bookCover);
            } else {//必须设置否则错乱
                viewHolder.bookCover.setImageResource(R.mipmap.cover_type_txt);
            }
        }else{//背景隐藏
            contentView.setVisibility(View.INVISIBLE);
        }


        return contentView;
    }

    public void setDragGridView(DragGridView dragGridView) {
        this.dragGridView = dragGridView;
    }


    static class ViewHolder {
        @BindView(R.id.ib_close)
        ImageButton deleteItem_IB;
        @BindView(R.id.tv_name)
        TextView name;

        @BindView(R.id.iv_cover)
        ImageView bookCover;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public void setItems(List<CollectBook> collectBooks) {
        this.bookList = collectBooks;//不知道为啥清空添加会在activity返回时导致数据不见
        notifyDataSetChanged();
    }

    public List<CollectBook> getBookList() {
        return bookList;
    }

    /**
     * Drag移动时item交换数据,并在数据库中更新交换后的位置数据(有问题还不知道原因，暂时采用一次性设置书籍在书架的位置，简单明了)
     *
     * @param oldPosition
     * @param newPosition
     */
    @Override
    public void reorderItems(int oldPosition, int newPosition) {
        LogUtils.i("reorderItems");
        CollectBook oldCollectBook = bookList.get(oldPosition);
//        List<CollectBook> books = collectBookService.findAll();
//
//        CollectBook newCollectBook = books.get(newPosition);
//        int tempId = newCollectBook.getBookSortId();
//        LogUtils.i("oldPosition is" + oldPosition + ",sortId is" + oldCollectBook.getBookSortId());
//        LogUtils.i("newPosition is" + newPosition + ",sortId is" + + tempId);

        if (oldPosition < newPosition) {//0,1
            for (int i = oldPosition; i < newPosition; i++) {
                Collections.swap(bookList, i, i + 1);
            }
        } else if (oldPosition > newPosition) {
            for (int i = oldPosition; i > newPosition; i--) {
                Collections.swap(bookList, i, i - 1);
            }
        }
        bookList.set(newPosition, oldCollectBook);

    }

    /**
     * 隐藏item
     *
     * @param hidePosition
     */
    @Override
    public void setHideItem(int hidePosition) {
        this.mHidePosition = hidePosition;
        LogUtils.w("hidePosition:" + hidePosition);
        notifyDataSetChanged();
    }

    /**
     * 删除书籍，点击删除按钮回调
     *
     * @param deletePosition
     */
    @Override
    public void removeItem(int deletePosition) {
        LogUtils.i("deletePosition:" + deletePosition);

        if (itemDeleteListener != null) {
            itemDeleteListener.item(deletePosition);
        }
    }


    /**
     * Book打开后位置移动到第一位
     *
     * @param openPosition
     */
    @Override
    public void setItemToFirst(int openPosition) {
        LogUtils.i("setItemToFirst:" + openPosition);

//        CollectBook oldCollectBook = bookList.get(openPosition);
        LogUtils.i("set item adapter " + openPosition);
        if (openPosition == 0) {
            return;
        }
        List<CollectBook> tempCollectBookList = new ArrayList<CollectBook>();
        tempCollectBookList.addAll(getBookList());
        CollectBook firstCollectBook = tempCollectBookList.get(0);
        CollectBook clickCollectBook = tempCollectBookList.remove(openPosition);//被点击的书籍
        LogUtils.i("first:" + firstCollectBook.getBookSortId() + ",click:" + clickCollectBook.getBookSortId());
        //和第一本书籍交换排序id
        //first:1,click:6
        int tempId = clickCollectBook.getBookSortId();
        clickCollectBook.setBookSortId(firstCollectBook.getBookSortId());
        firstCollectBook.setBookSortId(tempId);
        tempCollectBookList.add(0, clickCollectBook);
        setItems(tempCollectBookList);//刷新列表
        collectBookService.resetCollectBookSortId(tempCollectBookList);
//        List<CollectBook> all = collectBookService.findAll();
//        //打印是否交换成功
//        for (int j = 0; j < all.size(); j++) {
//            LogUtils.i(all.get(j).getBookSortId()+":" + all.get(j).getTitle());
//        }

    }

    @Override
    public void finishDragGrid() {
        collectBookService.resetCollectBookSortId(bookList);
    }

    @Override
    public void showDeleteButton() {
        LogUtils.i("showDeleteButton");
        notifyDataSetChanged();
    }


    public interface OnDeleteItemListener {
        void item(int deletePosition);
    }

    /**
     * 设置删除按钮监听
     *
     * @param itemDeleteListener
     */
    public void setItemDeleteListener(OnDeleteItemListener itemDeleteListener) {
        this.itemDeleteListener = itemDeleteListener;
    }
}
