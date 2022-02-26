package com.sjl.bookmark.api

import com.sjl.bookmark.entity.dto.ResponseDto
import com.sjl.bookmark.entity.table.Collection
import com.sjl.core.entity.dto.UpdateInfoDto
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * 书签api服务接口
 *
 *  @Path:所有在网址中的参数(URL的问号前面),如:
 * http://192.168.1.1/api/Accounts/{accountId}
 *
 * @Query:URL问号后面的参数,如:
 * http://192.168.1.1/api/Comments?access_token={access_token}
 *
 * @QueryMap:相当于多个@Query
 *
 *
 * @Field:用于POST请求,提交单个数据
 *
 *
 * @FieldMap:以map形式提交多个Field(Retrofit2.0之后添加)
 *
 *
 * @Body:相当于多个@Field,以对象的形式提交
 * @author Kelly
 * @version 1.0.0
 * @filename MyBookmarkService.java
 * @time 2018/5/16 11:06
 * @copyright(C) 2018 song
 */
interface MyBookmarkService {
    /**
     * 下载书签文件
     * http://192.168.3.92:8080/my-bookmark/bookmark/downloadBookmarkFile.htmls
     */
    @Headers("Domain-Name:my-bookmark")
    @Streaming
    @GET("my-bookmark/bookmark/downloadBookmarkFile.htmls")
    fun downloadBookmarkFile(): Observable<ResponseBody>

    /**
     * 同步我的收藏到服务器端
     * http://192.168.3.92:8080/my-bookmark/bookmark/syncCollection.htmls
     *
     * @param myCollection
     * @return
     */
    @Headers("Domain-Name:my-bookmark")
    @POST("my-bookmark/bookmark/syncCollection.htmls")
    @FormUrlEncoded
    fun syncCollection(@Field("myCollection") myCollection: String?): Observable<ResponseDto<Any>>

    /**
     * 查询所有书签
     * http://192.168.3.92:8080/my-bookmark/bookmark/syncCollection.htmls
     *
     * @return
     */
    @Headers("Domain-Name:my-bookmark")
    @GET("my-bookmark/bookmark/findAllCollection.htmls")
    fun findAllCollection(): Observable<ResponseDto<List<Collection>>>

    /**
     * 检查更新
     * http://192.168.3.92:8080/my-bookmark/appUpdate/checkUpdate.htmls
     * @param appId 应用id
     * @param versionCode 应用版本号
     * @return
     */
    @Headers("Domain-Name:my-bookmark")
    @GET("my-bookmark/appUpdate/checkUpdate.htmls")
    fun checkUpdate(
        @Query("appId") appId: String?,
        @Query("versionCode") versionCode: Int
    ): Observable<ResponseDto<UpdateInfoDto>>

    /**
     * 下载Apk文件
     * http://192.168.3.92:8080/my-bookmark/appUpdate/downloadApkFile.htmls
     */
    @Headers("Domain-Name:my-bookmark")
    @Streaming
    @GET("my-bookmark/appUpdate/downloadApkFile.htmls")
    fun downloadApkFile(): Observable<ResponseBody>
}