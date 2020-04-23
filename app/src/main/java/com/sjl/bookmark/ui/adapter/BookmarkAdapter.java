package com.sjl.bookmark.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sjl.bookmark.R;
import com.sjl.bookmark.entity.table.Bookmark;
import com.sjl.bookmark.ui.activity.BrowserActivity;
import com.sjl.core.util.log.LogUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookmarkAdapter.java
 * @time 2018/2/7 15:59
 * @copyright(C) 2018 song
 */
public class BookmarkAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Bookmark> bookmarkList;
    public static final int TYPE_HEADER = 0;  //代表标题
    public static final int TYPE_ITEM = 1;    //代表项目item
    public static final int TYPE_FOOTER = 2;//加载更多条目
    private static final Pattern REGEX_HTTP = Pattern.compile("//(.*?)/");


    /**
     * 正在加载中
     */
    public static final int LOADING_MORE = 0;
    /**
     * 没有加载更多
     */
    public static final int NO_LOAD_MORE = 1;

    /**
     * 上拉加载更多状态-默认为0
     */
    private int mLoadMoreStatus = 0;

    public BookmarkAdapter(Context context, List<Bookmark> bookmarkList) {
        this.context = context;
        this.bookmarkList = bookmarkList;
    }

    public String getTitle(int currentPosition) {
        Bookmark bookmark = bookmarkList.get(currentPosition);
        return bookmark != null ? bookmark.getTitle() : "";
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        //创建不同的 ViewHolder,根据viewtype来判断
        View view;
        if (viewType == TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bookmark_title, parent, false);
            return new BookmarkTitleHolder(view);
        } else if (viewType == TYPE_ITEM) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bookmark, parent, false);
            final BookmarkHolder bookmarkHolder = new BookmarkHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = bookmarkHolder.getAdapterPosition();
                    Intent intent = new Intent(context, BrowserActivity.class);
                    intent.putExtra(BrowserActivity.WEBVIEW_URL, bookmarkList.get(position).getHref());
                    intent.putExtra(BrowserActivity.WEBVIEW_TITLE, bookmarkList.get(position).getText());
                    context.startActivity(intent);

                }
            });
            return bookmarkHolder;
        } else if (viewType == TYPE_FOOTER) {
            view = LayoutInflater.from(context).inflate(R.layout.item_foot, parent,
                    false);
            return new FootViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //这里完成数据的绑定
        if (holder instanceof FootViewHolder) {
            LogUtils.i("正在加载中...");
            FootViewHolder footViewHolder = (FootViewHolder) holder;
            switch (mLoadMoreStatus) {
                case LOADING_MORE://加载更多
                    if (footViewHolder.progressBar.getVisibility() == View.GONE) {
                        footViewHolder.progressBar.setVisibility(View.VISIBLE);
                    }
                    footViewHolder.mTvLoadText.setText(R.string.loading);
                    break;
                case NO_LOAD_MORE://没有数据了
                    footViewHolder.progressBar.setVisibility(View.GONE);
                    footViewHolder.mTvLoadText.setText(R.string.no_load_more);
                    break;
                default:
                    break;
            }
            return;
        }
        Bookmark bookmark = bookmarkList.get(position);
        if (holder instanceof BookmarkTitleHolder) {
            BookmarkTitleHolder titleHolder = (BookmarkTitleHolder) holder;
            titleHolder.tv_bookmark_title.setText(bookmark.getTitle());
        } else if (holder instanceof BookmarkHolder) {
            BookmarkHolder itemHolder = (BookmarkHolder) holder;
            itemHolder.tv_text.setText(bookmark.getText());
            itemHolder.iv_icon.setImageBitmap(base64ToBitmap(bookmark.getIcon()));
            String href = bookmark.getHref();
            if (!TextUtils.isEmpty(href)) {
                itemHolder.tv_domain.setText(getSubUtilSimple(href) + "/...");
            } else {
                itemHolder.tv_domain.setText("--");
            }
        }

    }


    @Override
    public int getItemCount() {
        return (bookmarkList != null && !bookmarkList.isEmpty()) ? bookmarkList.size() + 1 : 1;
    }


    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        }
        Bookmark bookmark = bookmarkList.get(position);
        if (bookmark.getType() == 0) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    public void setLoadingState(int loadingState) {
        this.mLoadMoreStatus = loadingState;
    }

    public void setData(List<Bookmark> bookmarks) {
        this.bookmarkList = bookmarks;
        notifyDataSetChanged();
    }


    static class BookmarkHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_icon)
        ImageView iv_icon;

        @BindView(R.id.tv_text)
        TextView tv_text;

        @BindView(R.id.tv_domain)
        TextView tv_domain;


        public BookmarkHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class BookmarkTitleHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_bookmark_title)
        TextView tv_bookmark_title;


        public BookmarkTitleHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class FootViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.progressBar)
        ProgressBar progressBar;
        @BindView(R.id.tv_loading_text)
        TextView mTvLoadText;

        public FootViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    /**
     * base64转Bitmap
     *
     * @param base64Data
     * @return
     */
    private Bitmap base64ToBitmap(String base64Data) {
        if (TextUtils.isEmpty(base64Data)) {
            return null;
        }
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private String getSubUtilSimple(String str) {
        Matcher m = REGEX_HTTP.matcher(str);
        while (m.find()) {
            return m.group(1);
        }
        return "";
    }
}
