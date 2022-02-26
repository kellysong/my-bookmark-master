package com.sjl.bookmark.ui.presenter;

import com.sjl.bookmark.R;
import com.sjl.bookmark.api.ZhiHuApiService;
import com.sjl.bookmark.entity.zhihu.NewsCommentDto;
import com.sjl.bookmark.ui.contract.NewsCommentContract;
import com.sjl.core.net.RetrofitHelper;
import com.sjl.core.net.RxSchedulers;
import com.sjl.core.util.log.LogUtils;

import io.reactivex.functions.Consumer;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename NewsCommentPresenter.java
 * @time 2018/12/24 15:44
 * @copyright(C) 2018 song
 */
public class NewsCommentPresenter extends NewsCommentContract.Presenter {
    @Override
    public void loadLongComment(final String id) {
        ZhiHuApiService apiService = RetrofitHelper.getInstance().getApiService(ZhiHuApiService.class);
        apiService.getLongComment(id).compose(RxSchedulers.<NewsCommentDto>applySchedulers())
                .as(this.<NewsCommentDto>bindLifecycle()).subscribe(new Consumer<NewsCommentDto>() {
            @Override
            public void accept(NewsCommentDto newsCommentDto) throws Exception {
                mView.showNewsComment(newsCommentDto);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                LogUtils.e("获取长评论异常,id:" + id, throwable);
                mView.showError(mContext.getString(R.string.comment_get_failed));
            }
        });
    }

    @Override
    public void loadShortComment(final String id) {
        ZhiHuApiService apiService = RetrofitHelper.getInstance().getApiService(ZhiHuApiService.class);
        apiService.getShortComment(id).compose(RxSchedulers.<NewsCommentDto>applySchedulers())
                .as(this.<NewsCommentDto>bindLifecycle()).subscribe(new Consumer<NewsCommentDto>() {
            @Override
            public void accept(NewsCommentDto newsCommentDto) throws Exception {
                mView.showNewsComment(newsCommentDto);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                LogUtils.e("获取短评论异常,id:" + id, throwable);
                mView.showError(mContext.getString(R.string.comment_get_failed));
            }
        });
    }
}
