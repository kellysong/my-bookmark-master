package com.sjl.bookmark.ui.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.TextUtils
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.sjl.bookmark.R
import com.sjl.bookmark.entity.table.Bookmark
import com.sjl.bookmark.ui.activity.BrowserActivity
import com.sjl.core.util.log.LogUtils
import java.util.regex.Pattern

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookmarkAdapter.java
 * @time 2018/2/7 15:59
 * @copyright(C) 2018 song
 */
class BookmarkAdapter(private val context: Context, private var bookmarkList: List<Bookmark>?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    /**
     * 上拉加载更多状态-默认为0
     */
    private var mLoadMoreStatus = 0
    fun getTitle(currentPosition: Int): String {
        val bookmark = bookmarkList!![currentPosition]
        return if (bookmark != null) bookmark.title else ""
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        //创建不同的 ViewHolder,根据viewtype来判断
        val view: View
        if (viewType == TYPE_HEADER) {
            view = LayoutInflater.from(parent.context).inflate(R.layout.item_bookmark_title, parent, false)
            return BookmarkTitleHolder(view)
        } else if (viewType == TYPE_ITEM) {
            view = LayoutInflater.from(parent.context).inflate(R.layout.item_bookmark, parent, false)
            val bookmarkHolder = BookmarkHolder(view)
            view.setOnClickListener {
                val position = bookmarkHolder.adapterPosition
                val intent = Intent(context, BrowserActivity::class.java)
                intent.putExtra(BrowserActivity.WEBVIEW_URL, bookmarkList!![position].href)
                intent.putExtra(BrowserActivity.WEBVIEW_TITLE, bookmarkList!![position].text)
                context.startActivity(intent)
            }
            return bookmarkHolder
        } else if (viewType == TYPE_FOOTER) {
            view = LayoutInflater.from(context).inflate(R.layout.item_foot, parent,
                    false)
            return FootViewHolder(view)
        }
        throw RuntimeException("非法viewType：$viewType")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //这里完成数据的绑定
        if (holder is FootViewHolder) {
            LogUtils.i("正在加载中...")
            val footViewHolder = holder
            when (mLoadMoreStatus) {
                LOADING_MORE -> {
                    if (footViewHolder.progressBar!!.visibility == View.GONE) {
                        footViewHolder.progressBar!!.visibility = View.VISIBLE
                    }
                    footViewHolder.mTvLoadText!!.setText(R.string.loading)
                }
                NO_LOAD_MORE -> {
                    footViewHolder.progressBar!!.visibility = View.GONE
                    footViewHolder.mTvLoadText!!.setText(R.string.no_load_more)
                }
                else -> {
                }
            }
            return
        }
        val bookmark = bookmarkList!![position]
        if (holder is BookmarkTitleHolder) {
            holder.tv_bookmark_title!!.text = bookmark.title
        } else if (holder is BookmarkHolder) {
            val itemHolder = holder
            itemHolder.tv_text!!.text = bookmark.text
            itemHolder.iv_icon!!.setImageBitmap(base64ToBitmap(bookmark.icon))
            val href = bookmark.href
            if (!TextUtils.isEmpty(href)) {
                itemHolder.tv_domain!!.text = getSubUtilSimple(href) + "/..."
            } else {
                itemHolder.tv_domain!!.text = "--"
            }
        }
    }

    override fun getItemCount(): Int {
        return if (bookmarkList != null && !bookmarkList!!.isEmpty()) bookmarkList!!.size + 1 else 1
    }

    override fun getItemViewType(position: Int): Int {
        if (position + 1 == itemCount) {
            return TYPE_FOOTER
        }
        val bookmark = bookmarkList!![position]
        return if (bookmark.type == 0) {
            TYPE_HEADER
        } else {
            TYPE_ITEM
        }
    }

    fun setLoadingState(loadingState: Int) {
        mLoadMoreStatus = loadingState
    }

    fun setData(bookmarks: List<Bookmark>?) {
        bookmarkList = bookmarks
        notifyDataSetChanged()
    }

    internal class BookmarkHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @JvmField
        @BindView(R.id.iv_icon)
        var iv_icon: ImageView? = null

        @JvmField
        @BindView(R.id.tv_text)
        var tv_text: TextView? = null

        @JvmField
        @BindView(R.id.tv_domain)
        var tv_domain: TextView? = null

        init {
            ButterKnife.bind(this, itemView)
        }
    }

    internal class BookmarkTitleHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @JvmField
        @BindView(R.id.tv_bookmark_title)
        var tv_bookmark_title: TextView? = null

        init {
            ButterKnife.bind(this, itemView)
        }
    }

    internal class FootViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        @JvmField
        @BindView(R.id.progressBar)
        var progressBar: ProgressBar? = null
        
        @JvmField
        @BindView(R.id.tv_loading_text)
        var mTvLoadText: TextView?= null

        init {
            ButterKnife.bind(this, view)
        }
    }

    /**
     * base64转Bitmap
     *
     * @param base64Data
     * @return
     */
    private fun base64ToBitmap(base64Data: String): Bitmap? {
        if (TextUtils.isEmpty(base64Data)) {
            return null
        }
        val bytes = Base64.decode(base64Data, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    private fun getSubUtilSimple(str: String): String {
        val m = REGEX_HTTP.matcher(str)
        while (m.find()) {
            return m.group(1)
        }
        return ""
    }

    companion object {
        const val TYPE_HEADER = 0 //代表标题
        const val TYPE_ITEM = 1 //代表项目item
        const val TYPE_FOOTER = 2 //加载更多条目
        private val REGEX_HTTP = Pattern.compile("//(.*?)/")

        /**
         * 正在加载中
         */
        const val LOADING_MORE = 0

        /**
         * 没有加载更多
         */
        const val NO_LOAD_MORE = 1
    }
}