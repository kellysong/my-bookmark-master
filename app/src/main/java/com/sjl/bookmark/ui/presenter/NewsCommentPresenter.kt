package com.sjl.bookmark.ui.presenter

import com.sjl.bookmark.R
import com.sjl.bookmark.api.ZhiHuApiService
import com.sjl.bookmark.entity.zhihu.NewsCommentDto
import com.sjl.bookmark.ui.contract.NewsCommentContract
import com.sjl.core.net.RetrofitHelper
import com.sjl.core.net.RxSchedulers
import com.sjl.core.util.log.LogUtils
import io.reactivex.functions.Consumer

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename NewsCommentPresenter.java
 * @time 2018/12/24 15:44
 * @copyright(C) 2018 song
 */
class NewsCommentPresenter : NewsCommentContract.Presenter() {
    override fun loadLongComment(id: String) {
        val apiService: ZhiHuApiService = RetrofitHelper.getInstance().getApiService(
            ZhiHuApiService::class.java
        )
        apiService.getLongComment(id).compose(RxSchedulers.applySchedulers<NewsCommentDto>())
            .`as`(bindLifecycle()).subscribe(object : Consumer<NewsCommentDto> {
                @Throws(Exception::class)
                override fun accept(newsCommentDto: NewsCommentDto) {
                    mView.showNewsComment(newsCommentDto)
                }
            }, object : Consumer<Throwable> {
                @Throws(Exception::class)
                override fun accept(throwable: Throwable?) {
                    LogUtils.e("获取长评论异常,id:" + id, throwable)
                    mView.showError(mContext.getString(R.string.comment_get_failed))
                }
            })
    }

    override fun loadShortComment(id: String) {
        val apiService: ZhiHuApiService = RetrofitHelper.getInstance().getApiService(
            ZhiHuApiService::class.java
        )
        apiService.getShortComment(id).compose(RxSchedulers.applySchedulers<NewsCommentDto>())
            .`as`(bindLifecycle()).subscribe(object : Consumer<NewsCommentDto> {
                @Throws(Exception::class)
                override fun accept(newsCommentDto: NewsCommentDto) {
                    mView.showNewsComment(newsCommentDto)
                }
            }, object : Consumer<Throwable> {
                @Throws(Exception::class)
                override fun accept(throwable: Throwable?) {
                    LogUtils.e("获取短评论异常,id:" + id, throwable)
                    mView.showError(mContext.getString(R.string.comment_get_failed))
                }
            })
    }
}