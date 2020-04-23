package com.sjl.bookmark.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.sjl.bookmark.R;
import com.sjl.bookmark.entity.zhihu.NewsCommentDto;
import com.sjl.bookmark.ui.adapter.NewsCommentAdapter;
import com.sjl.bookmark.ui.adapter.RecyclerViewDivider;
import com.sjl.bookmark.ui.contract.NewsCommentContract;
import com.sjl.bookmark.ui.presenter.NewsCommentPresenter;
import com.sjl.bookmark.widget.danmu.DanMuControl;
import com.sjl.bookmark.widget.danmu.DanMuInfo;
import com.sjl.core.mvp.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import master.flame.danmaku.controller.IDanmakuView;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename NewsCommentFragment.java
 * @time 2018/12/24 11:07
 * @copyright(C) 2018 song
 */
public class NewsCommentFragment extends BaseFragment<NewsCommentPresenter> implements NewsCommentContract.View {
    @BindView(R.id.comment_recyclerview)
    RecyclerView mCommentRecyclerView;
    private NewsCommentAdapter mNewsCommentAdapter;
    private int position;

    @BindView(R.id.sv_danmaku)
    IDanmakuView mDanmakuView;

    private DanMuControl danmuControl;


    @Override
    protected int getLayoutId() {
        return R.layout.news_comment_fragmnet;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {
        Bundle arguments = getArguments();
        position = arguments.getInt("position");//fragment索引
        String newsId = arguments.getString("newsId");//当前日报id
//        LogUtils.i("position=" + position + ",newsId=" + newsId);
        mNewsCommentAdapter = new NewsCommentAdapter(R.layout.news_comment_recycle_item, null);
        mCommentRecyclerView.setAdapter(mNewsCommentAdapter);
        mCommentRecyclerView.addItemDecoration(new RecyclerViewDivider(mActivity, LinearLayoutManager.VERTICAL));
        mCommentRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        if (position == 0) {
            danmuControl = new DanMuControl(mActivity, mDanmakuView);
            mPresenter.loadShortComment(newsId);
        } else {
            mPresenter.loadLongComment(newsId);
        }

    }

    @Override
    protected void onFirstUserVisible() {

    }

    @Override
    protected void onUserVisible() {

    }

    @Override
    protected void onUserInvisible() {

    }

    @Override
    public void showNewsComment(NewsCommentDto newsCommentDto) {
        ArrayList<NewsCommentDto.Comment> comments = newsCommentDto.getComments();
        mNewsCommentAdapter.setNewData(comments);
        if (position == 0) {
            if (comments != null && comments.size() > 0) {
                List<DanMuInfo> danMuInfoList = new ArrayList<>();
                DanMuInfo danMuInfo;
                for (NewsCommentDto.Comment comment : comments) {
                    danMuInfo = new DanMuInfo();
                    danMuInfo.avatarUrl = comment.getAvatar();
                    danMuInfo.name = comment.getAuthor()+":";
                    danMuInfo.content = comment.getContent();
                    danMuInfoList.add(danMuInfo);
                }
                danmuControl.addDanmu(danMuInfoList);
            }
        }
    }

    @Override
    public void showError(String errorMsg) {
        showLongToast(errorMsg);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
            mDanmakuView.resume();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mDanmakuView != null && mDanmakuView.isPrepared()) {
            mDanmakuView.pause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mDanmakuView != null) {
            mDanmakuView.release();
            mDanmakuView = null;
        }
    }
}
