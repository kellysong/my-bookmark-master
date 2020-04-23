package com.sjl.bookmark.api;

import com.sjl.bookmark.entity.dto.ResponseDto;
import com.sjl.bookmark.entity.table.Collection;
import com.sjl.core.entity.dto.UpdateInfoDto;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

/**
 * 书签api服务接口
 * <p> @Path:所有在网址中的参数(URL的问号前面),如:
    http://192.168.1.1/api/Accounts/{accountId}</p>
 <p>@Query:URL问号后面的参数,如:
    http://192.168.1.1/api/Comments?access_token={access_token}</p>
 <p>@QueryMap:相当于多个@Query</p>

 <p>@Field:用于POST请求,提交单个数据</p>

 <p>@FieldMap:以map形式提交多个Field(Retrofit2.0之后添加)</p>

 <p>@Body:相当于多个@Field,以对象的形式提交</p>
 * @author Kelly
 * @version 1.0.0
 * @filename MyBookmarkService.java
 * @time 2018/5/16 11:06
 * @copyright(C) 2018 song
 */
public interface MyBookmarkService {

    /**
     * 下载书签文件
     * http://192.168.3.92:8080/my-bookmark/bookmark/downloadBookmarkFile.htmls
     */
    @Headers({"Domain-Name:my-bookmark"})
    @Streaming
    @GET("my-bookmark/bookmark/downloadBookmarkFile.htmls")
    Observable<ResponseBody> downloadBookmarkFile();

    /**
     * 同步我的收藏到服务器端
     * http://192.168.3.92:8080/my-bookmark/bookmark/syncCollection.htmls
     *
     * @param myCollection
     * @return
     */
    @Headers({"Domain-Name:my-bookmark"})
    @POST("my-bookmark/bookmark/syncCollection.htmls")
    @FormUrlEncoded
    Observable<ResponseDto<Object>> syncCollection(@Field("myCollection") String myCollection);

    /**
     * 查询所有书签
     * http://192.168.3.92:8080/my-bookmark/bookmark/syncCollection.htmls
     *
     * @return
     */
    @Headers({"Domain-Name:my-bookmark"})
    @GET("my-bookmark/bookmark/findAllCollection.htmls")
    Observable<ResponseDto<List<Collection>>> findAllCollection();


    /**
     * 检查更新
     * http://192.168.3.92:8080/my-bookmark/appUpdate/checkUpdate.htmls
     * @param appId 应用id
     * @param versionCode 应用版本号
     * @return
     */
    @Headers({"Domain-Name:my-bookmark"})
    @GET("my-bookmark/appUpdate/checkUpdate.htmls")
    Observable<ResponseDto<UpdateInfoDto>> checkUpdate(@Query("appId") String appId, @Query("versionCode") int versionCode);

    /**
     * 下载Apk文件
     * http://192.168.3.92:8080/my-bookmark/appUpdate/downloadApkFile.htmls
     */
    @Headers({"Domain-Name:my-bookmark"})
    @Streaming
    @GET("my-bookmark/appUpdate/downloadApkFile.htmls")
    Observable<ResponseBody> downloadApkFile();

}
