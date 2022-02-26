package com.sjl.bookmark.ui.fragment

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import com.sjl.bookmark.R
import com.sjl.bookmark.app.MyApplication
import com.sjl.bookmark.entity.zhihu.NewsCommentDto
import com.sjl.bookmark.ui.adapter.NewsCommentAdapter
import com.sjl.bookmark.ui.adapter.RecyclerViewDivider
import com.sjl.bookmark.ui.contract.NewsCommentContract
import com.sjl.bookmark.ui.presenter.NewsCommentPresenter
import com.sjl.bookmark.widget.danmu.DanMuControl
import com.sjl.bookmark.widget.danmu.DanMuInfo
import com.sjl.core.mvp.BaseFragment
import kotlinx.android.synthetic.main.news_comment_fragmnet.*
import master.flame.danmaku.controller.IDanmakuView
import java.util.*

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename NewsCommentFragment.java
 * @time 2018/12/24 11:07
 * @copyright(C) 2018 song
 */
class NewsCommentFragment : BaseFragment<NewsCommentPresenter>(), NewsCommentContract.View {

    private lateinit var mNewsCommentAdapter: NewsCommentAdapter
    private var position = 0

    private var danmuControl: DanMuControl? = null
    override fun getLayoutId(): Int {
        return R.layout.news_comment_fragmnet
    }

    override fun initView() {}
    override fun initListener() {}
    override fun initData() {
        val arguments = arguments
        position = arguments!!.getInt("position") //fragment索引
        val newsId = arguments.getString("newsId") //当前日报id
        //        LogUtils.i("position=" + position + ",newsId=" + newsId);
        mNewsCommentAdapter = NewsCommentAdapter(R.layout.news_comment_recycle_item, null)
        comment_recyclerview.adapter = mNewsCommentAdapter
        comment_recyclerview.addItemDecoration(
            RecyclerViewDivider(
                mActivity,
                LinearLayoutManager.VERTICAL
            )
        )
        comment_recyclerview.layoutManager = LinearLayoutManager(mActivity)
        if (position == 0) {
            danmuControl = DanMuControl(MyApplication.getContext(), sv_danmaku)
            mPresenter.loadShortComment(newsId)
        } else {
            mPresenter.loadLongComment(newsId)
        }
    }

    override fun onFirstUserVisible() {}
    override fun onUserVisible() {}
    override fun onUserInvisible() {}
    override fun showNewsComment(newsCommentDto: NewsCommentDto) {
        val comments = newsCommentDto.comments
        mNewsCommentAdapter.setNewData(comments)
        if (position == 0) {
            if (comments != null && comments.size > 0) {
                val danMuInfoList: MutableList<DanMuInfo> = ArrayList()
                var danMuInfo: DanMuInfo
                for (comment in comments) {
                    danMuInfo = DanMuInfo()
                    danMuInfo.avatarUrl = comment.avatar
                    danMuInfo.name = comment.author + ":"
                    danMuInfo.content = comment.content
                    danMuInfoList.add(danMuInfo)
                }
                danmuControl?.addDanmu(danMuInfoList)
            }
        }
    }

    override fun showError(errorMsg: String) {
        showLongToast(errorMsg)
    }

    override fun onResume() {
        super.onResume()
        sv_danmaku?.apply {
            if (isPrepared&& isPaused){
                resume()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        sv_danmaku?.apply {
            if (isPrepared){
                pause()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sv_danmaku?.release()
    }
}