package com.sjl.bookmark.ui.presenter

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import com.sjl.bookmark.R
import com.sjl.bookmark.api.ZhiHuApiService
import com.sjl.bookmark.entity.zhihu.NewsDetailDto
import com.sjl.bookmark.entity.zhihu.NewsExtraDto
import com.sjl.bookmark.ui.contract.NewsDetailContract
import com.sjl.core.net.RetrofitHelper
import com.sjl.core.net.RxSchedulers
import com.sjl.core.util.log.LogUtils
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename NewsDetailPresenter.java
 * @time 2018/12/21 11:33
 * @copyright(C) 2018 song
 */
class NewsDetailPresenter : NewsDetailContract.Presenter() {
    override fun shareNews(content: String, imgUrl: String) {
        if (true) { //暂时不支持图文分享
            share(content, null)
            return
        }
        //分享网络图片
        Observable
            .just(imgUrl)
            .map(object : Function<String, Uri> {
                @Throws(Exception::class)
                override fun apply(imageUrl: String): Uri {
                    val url: URL = URL(imageUrl)
                    val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                    connection.connectTimeout = 6000 //超时设置
                    connection.doInput = true
                    connection.useCaches = false //设置不使用缓存
                    val inputStream: InputStream = connection.inputStream
                    val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
                    val imageUri: Uri = Uri.parse(
                        MediaStore.Images.Media.insertImage(
                            mContext.contentResolver,
                            bitmap,
                            null,
                            null
                        )
                    )
                    inputStream.close()
                    return imageUri
                }
            }).compose(RxSchedulers.applySchedulers()).`as`(bindLifecycle())
            .subscribe(object : Consumer<Uri> {
                @Throws(Exception::class)
                override fun accept(uri: Uri) {
                    share(content, uri)
                }
            }, object : Consumer<Throwable> {
                @Throws(Exception::class)
                override fun accept(throwable: Throwable?) {
//                ToastUtils.showShort(mContext, mContext.getString(R.string.ssdk_oks_share_failed));
                }
            })
    }

    /**
     * 分享
     *
     * @param content
     * @param uri
     */
    private fun share(content: String, uri: Uri?) {
        val shareIntent: Intent = Intent(Intent.ACTION_SEND)
        if (uri != null) {
            //uri 是图片的地址
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            shareIntent.type = "image/*"
            //当用户选择短信时使用sms_body取得文字
            shareIntent.putExtra("sms_body", content)
        } else {
            shareIntent.type = "text/plain"
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT, content)
        mContext.startActivity(shareIntent)
    }

    override fun loadNewsExtra(id: String?) {
        val apiService: ZhiHuApiService = RetrofitHelper.getInstance().getApiService(
            ZhiHuApiService::class.java
        )
        apiService.getStoryExtra(id).compose(RxSchedulers.applySchedulers<NewsExtraDto>())
            .`as`(bindLifecycle()).subscribe(object : Consumer<NewsExtraDto> {
                @Throws(Exception::class)
                override fun accept(newsExtra: NewsExtraDto) {
                    mView.showNewsExtra(newsExtra)
                }
            }, object : Consumer<Throwable?> {
                @Throws(Exception::class)
                override fun accept(throwable: Throwable?) {
                    LogUtils.e("获取新闻额外信息异常", throwable)
                    mView.showError(mContext.getString(R.string.news_other_get_failed))
                }
            })
    }

    override fun loadNewsDetail(id: String?) {
        val apiService: ZhiHuApiService = RetrofitHelper.getInstance().getApiService(
            ZhiHuApiService::class.java
        )
        apiService.getNewsDetail(id)
            .flatMap(object : Function<NewsDetailDto, ObservableSource<NewsDetailDto>> {
                @Throws(Exception::class)
                override fun apply(newsDetail: NewsDetailDto): ObservableSource<NewsDetailDto> {
                    if (newsDetail == null) {
                        return Observable.error(Exception(mContext.getString(R.string.news_details_empty)))
                    }
                    var body: String = newsDetail.body
                    body = body.substring(body.indexOf("<div class=\"question\">")) //去掉头部图片
                    val webContent: String = ("<!DOCTYPE html>" +
                            "<html>" +
                            "<head><meta charset=\"UTF-8\"><link rel=\"stylesheet\" href=\"news_qa.auto.css\"></head>" +
                            "<body>" + body + "</body>" +
                            "</html>")
                    newsDetail.contentBody = webContent
                    return Observable.just(newsDetail)
                }
            }).compose(RxSchedulers.applySchedulers())
            .`as`(bindLifecycle()).subscribe(object : Consumer<NewsDetailDto> {
                @Throws(Exception::class)
                override fun accept(newsDetail: NewsDetailDto) {
                    mView.showNewsDetail(newsDetail)
                }
            }, object : Consumer<Throwable?> {
                @Throws(Exception::class)
                override fun accept(throwable: Throwable?) {
                    LogUtils.e("获取新闻详情异常", throwable)
                    mView.showError(mContext.getString(R.string.news_detail_get_failed))
                }
            })
    }
}