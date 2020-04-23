package com.sjl.bookmark.ui.presenter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import com.sjl.bookmark.api.ZhiHuApiService;
import com.sjl.bookmark.entity.zhihu.NewsDetailDto;
import com.sjl.bookmark.entity.zhihu.NewsExtraDto;
import com.sjl.bookmark.ui.contract.NewsDetailContract;
import com.sjl.core.net.RetrofitHelper;
import com.sjl.core.net.RxSchedulers;
import com.sjl.core.util.log.LogUtils;
import com.sjl.core.util.ToastUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename NewsDetailPresenter.java
 * @time 2018/12/21 11:33
 * @copyright(C) 2018 song
 */
public class NewsDetailPresenter extends NewsDetailContract.Presenter {
    @Override
    public void shareNews(final String content, String imgUrl) {

        if (true) {//暂时不支持图文分享
            share(content, null);
            return;
        }
        //分享网络图片
        Observable
                .just(imgUrl)
                .map(new Function<String, Uri>() {
                    @Override
                    public Uri apply(String imageUrl) throws Exception {
                        URL url = new URL(imageUrl);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setConnectTimeout(6000); //超时设置
                        connection.setDoInput(true);
                        connection.setUseCaches(false); //设置不使用缓存
                        InputStream inputStream = connection.getInputStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        Uri imageUri = Uri.parse(MediaStore.Images.Media.insertImage(mContext.getContentResolver(), bitmap, null, null));
                        inputStream.close();
                        return imageUri;
                    }
                }).compose(RxSchedulers.<Uri>applySchedulers()).as(this.<Uri>bindLifecycle()).subscribe(new Consumer<Uri>() {
            @Override
            public void accept(Uri uri) throws Exception {
                share(content, uri);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ToastUtils.showShort(mContext, "分享失败");
            }
        });
    }

    /**
     * 分享
     *
     * @param content
     * @param uri
     */
    private void share(String content, Uri uri) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        if (uri != null) {
            //uri 是图片的地址
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.setType("image/*");
            //当用户选择短信时使用sms_body取得文字
            shareIntent.putExtra("sms_body", content);
        } else {
            shareIntent.setType("text/plain");
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT, content);
        mContext.startActivity(shareIntent);
    }


    @Override
    public void loadNewsExtra(String id) {
        ZhiHuApiService apiService = RetrofitHelper.getInstance().getApiService(ZhiHuApiService.class);
        apiService.getStoryExtra(id).compose(RxSchedulers.<NewsExtraDto>applySchedulers())
                .as(this.<NewsExtraDto>bindLifecycle()).subscribe(new Consumer<NewsExtraDto>() {
            @Override
            public void accept(NewsExtraDto newsExtra) throws Exception {
                mView.showNewsExtra(newsExtra);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                LogUtils.e("获取新闻额外信息异常", throwable);
                mView.showError("获取新闻额外信息失败");
            }
        });

    }

    @Override
    public void loadNewsDetail(String id) {
        ZhiHuApiService apiService = RetrofitHelper.getInstance().getApiService(ZhiHuApiService.class);
        apiService.getNewsDetail(id).flatMap(new Function<NewsDetailDto, ObservableSource<NewsDetailDto>>() {
            @Override
            public ObservableSource<NewsDetailDto> apply(NewsDetailDto newsDetail) throws Exception {
                if (newsDetail == null) {
                    return Observable.error(new Exception("获取新闻详情为空"));
                }
                String body = newsDetail.getBody();
                body = body.substring(body.indexOf("<div class=\"question\">"));//去掉头部图片
                final String webContent = "<!DOCTYPE html>" +
                        "<html>" +
                        "<head><meta charset=\"UTF-8\"><link rel=\"stylesheet\" href=\"news_qa.auto.css\"></head>" +
                        "<body>" + body + "</body>" +
                        "</html>";
                newsDetail.setContentBody(webContent);
                return Observable.just(newsDetail);
            }
        }).compose(RxSchedulers.<NewsDetailDto>applySchedulers())
                .as(this.<NewsDetailDto>bindLifecycle()).subscribe(new Consumer<NewsDetailDto>() {
            @Override
            public void accept(NewsDetailDto newsDetail) throws Exception {
                mView.showNewsDetail(newsDetail);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                LogUtils.e("获取新闻详情异常", throwable);
                mView.showError("获取新闻详情失败");
            }
        });
    }
}
