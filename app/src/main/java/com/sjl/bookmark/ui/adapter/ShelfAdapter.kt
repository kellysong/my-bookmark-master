package com.sjl.bookmark.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.sjl.bookmark.R
import com.sjl.bookmark.dao.impl.CollectBookDaoImpl
import com.sjl.bookmark.entity.zhuishu.table.CollectBook
import com.sjl.bookmark.net.HttpConstant
import com.sjl.bookmark.widget.DragGridListener
import com.sjl.bookmark.widget.DragGridView
import com.sjl.core.util.log.LogUtils
import java.util.*

class ShelfAdapter(private val mContext: Context, var bookList: ArrayList<CollectBook>) : BaseAdapter(), DragGridListener {
    private var mHidePosition = -1
    private var collectBookService: CollectBookDaoImpl = CollectBookDaoImpl(mContext)
    private var itemDeleteListener: OnDeleteItemListener? = null
    private var dragGridView: DragGridView? = null
    private var inflater: LayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    override fun getCount(): Int {
        //背景书架的draw需要用到item的高度
        return if (bookList == null || bookList.size == 0) {
            9
        } else {
            bookList.size
        }
    }

    override fun getItem(position: Int): Any {
        return bookList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, tcontentView: View?, arg2: ViewGroup): View? {
//        LogUtils.i("getView1: position = " + position + ", childCount = " + arg2.getChildCount());
        var contentView = tcontentView
        val viewHolder: ViewHolder
        if (contentView == null) {
            contentView = inflater.inflate(R.layout.shelfitem, arg2, false)
            viewHolder = ViewHolder(contentView)
            contentView.tag = viewHolder
        } else {
            viewHolder = contentView.tag as ViewHolder
        }
        if ((arg2 as DragGridView).isOnMeasure) {
            return contentView
        }
        //        LogUtils.w("getView2: position = " + position + ", childCount = " + arg2.getChildCount() + ",bookList:" + bookList.size());
        if (bookList != null && bookList.size > position) {
            //DragGridView  解决复用问题
            if (position == mHidePosition) {
                contentView?.visibility = View.INVISIBLE
            } else {
                contentView?.visibility = View.VISIBLE
            }
            viewHolder.deleteItem_IB!!.setOnClickListener { removeItem(position) }
            if (dragGridView!!.isShowDeleteButton) {
                viewHolder.deleteItem_IB!!.visibility = View.VISIBLE
            } else {
                viewHolder.deleteItem_IB!!.visibility = View.INVISIBLE
            }
            val collBookBean = bookList[position]
            viewHolder.name!!.visibility = View.VISIBLE
            val fileName = collBookBean.title
            viewHolder.name!!.text = fileName
            val bookCover = viewHolder.bookCover
            if (!collBookBean.isLocal() && collBookBean.cover != null) {
                Glide.with(mContext).load(HttpConstant.ZHUISHU_IMG_BASE_URL + collBookBean.cover).into(bookCover!!)
            } else { //必须设置否则错乱
                viewHolder.bookCover!!.setImageResource(R.mipmap.cover_type_txt)
            }
        } else { //背景隐藏
            contentView?.visibility = View.INVISIBLE
        }
        return contentView
    }

    fun setDragGridView(dragGridView: DragGridView?) {
        this.dragGridView = dragGridView
    }

    internal class ViewHolder(view: View?) {
        @JvmField
        @BindView(R.id.ib_close)
        var deleteItem_IB: ImageButton? = null

        @JvmField
        @BindView(R.id.tv_name)
        var name: TextView? = null

        @JvmField
        @BindView(R.id.iv_cover)
        var bookCover: ImageView? = null

        init {
            ButterKnife.bind(this, view!!)
        }
    }

    fun setItems(collectBooks: ArrayList<CollectBook>) {
        bookList = collectBooks //不知道为啥清空添加会在activity返回时导致数据不见
        notifyDataSetChanged()
    }

    fun getBookList(): List<CollectBook> {
        return bookList
    }

    /**
     * Drag移动时item交换数据,并在数据库中更新交换后的位置数据(有问题还不知道原因，暂时采用一次性设置书籍在书架的位置，简单明了)
     *
     * @param oldPosition
     * @param newPosition
     */
    override fun reorderItems(oldPosition: Int, newPosition: Int) {
        LogUtils.i("reorderItems")
        val oldCollectBook = bookList[oldPosition]
        //        List<CollectBook> books = collectBookService.findAll();
//
//        CollectBook newCollectBook = books.get(newPosition);
//        int tempId = newCollectBook.getBookSortId();
//        LogUtils.i("oldPosition is" + oldPosition + ",sortId is" + oldCollectBook.getBookSortId());
//        LogUtils.i("newPosition is" + newPosition + ",sortId is" + + tempId);
        if (oldPosition < newPosition) { //0,1
            for (i in oldPosition until newPosition) {
                Collections.swap(bookList, i, i + 1)
            }
        } else if (oldPosition > newPosition) {
            for (i in oldPosition downTo newPosition + 1) {
                Collections.swap(bookList, i, i - 1)
            }
        }
        bookList[newPosition] = oldCollectBook
    }

    /**
     * 隐藏item
     *
     * @param hidePosition
     */
    override fun setHideItem(hidePosition: Int) {
        mHidePosition = hidePosition
        LogUtils.w("hidePosition:$hidePosition")
        notifyDataSetChanged()
    }

    /**
     * 删除书籍，点击删除按钮回调
     *
     * @param deletePosition
     */
    override fun removeItem(deletePosition: Int) {
        LogUtils.i("deletePosition:$deletePosition")
        if (itemDeleteListener != null) {
            itemDeleteListener!!.item(deletePosition)
        }
    }

    /**
     * Book打开后位置移动到第一位
     *
     * @param openPosition
     */
    override fun setItemToFirst(openPosition: Int) {
        LogUtils.i("setItemToFirst:$openPosition")

//        CollectBook oldCollectBook = bookList.get(openPosition);
        LogUtils.i("set item adapter $openPosition")
        if (openPosition == 0) {
            return
        }
        val tempCollectBookList: ArrayList<CollectBook> = ArrayList()
        tempCollectBookList.addAll(getBookList())
        val firstCollectBook = tempCollectBookList[0]
        val clickCollectBook = tempCollectBookList.removeAt(openPosition) //被点击的书籍
        LogUtils.i("first:" + firstCollectBook.bookSortId + ",click:" + clickCollectBook.bookSortId)
        //和第一本书籍交换排序id
        //first:1,click:6
        val tempId = clickCollectBook.bookSortId
        clickCollectBook.bookSortId = firstCollectBook.bookSortId
        firstCollectBook.bookSortId = tempId
        tempCollectBookList.add(0, clickCollectBook)
        setItems(tempCollectBookList) //刷新列表
        collectBookService.resetCollectBookSortId(tempCollectBookList)
        //        List<CollectBook> all = collectBookService.findAll();
//        //打印是否交换成功
//        for (int j = 0; j < all.size(); j++) {
//            LogUtils.i(all.get(j).getBookSortId()+":" + all.get(j).getTitle());
//        }
    }

    override fun finishDragGrid() {
        collectBookService.resetCollectBookSortId(bookList)
    }

    override fun showDeleteButton() {
        LogUtils.i("showDeleteButton")
        notifyDataSetChanged()
    }

    interface OnDeleteItemListener {
        fun item(deletePosition: Int)
    }

    /**
     * 设置删除按钮监听
     *
     * @param itemDeleteListener
     */
    fun setItemDeleteListener(itemDeleteListener: OnDeleteItemListener?) {
        this.itemDeleteListener = itemDeleteListener
    }


}