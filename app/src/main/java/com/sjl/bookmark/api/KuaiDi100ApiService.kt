package com.sjl.bookmark.api

import com.sjl.bookmark.entity.ExpressDetail
import com.sjl.bookmark.entity.ExpressDetail2
import com.sjl.bookmark.entity.ExpressName
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename KuaiDi100ApiService.java
 * @time 2018/1/29 15:36
 * @copyright(C) 2018 song
 */
interface KuaiDi100ApiService {
    /**
     * 查询快递名称
     * http://www.kuaidi100.com/autonumber/autoComNum?resultv2=1&text=479389994039
     * https://www.kuaidi100.com/autonumber/autoComNum?text=YT5800956776422
     *
     * @param expressNo 快递运单号
     * @return
     */
    @Headers("Domain-Name:kuaidi100")
    @GET("autonumber/autoComNum")
    fun queryExpressNameByNo(
        @HeaderMap headers: Map<String?, String?>?,
        @Query("text") expressNo: String?
    ): Observable<ExpressName>

    /**
     * 获取cookie
     * Observable<T> 里面的泛型T 不能是  okhttp3.Response 。
     * 可以是 retrofit2.Response<T> ，但是T不能为okhttp3.Response,是okhttp3.ResponseBody
     *
     * SET-Cookie是来自Server的header，意即在Client设置某个Cookie
     *
     * Cookie是浏览器返回到Server所用的header
     *
     * @return
    </T></T> */
    @get:GET("https://www.kuaidi100.com")
    @get:Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.157 Safari/537.36")
    val cookie: Observable<Response<ResponseBody>>

    /**
     * 查询快递信息,已经受限制了（返回数据不对），不明原因
     * https://www.kuaidi100.com/query?type=jd&postid=97408130042&temp=0.47825892409661974&phone=
     *
     * csrftoken=zCVuHlUOhenpYgUulra6voAdLUeNKym_FflcMplrlSg;
     * Hm_lvt_22ea01af58ba2be0fec7c11b25e88e6c=1561095430,1561096160,1561098197,1561099940;
     * Hm_lpvt_22ea01af58ba2be0fec7c11b25e88e6c=1561100145
     *
     * csrftoken=DdFLg1IBs4vyxwQAXFSytYNtP_1I4MbFys9MNC8AgTo;
     * Hm_lvt_22ea01af58ba2be0fec7c11b25e88e6c=1561096160,1561098197,1561099940,1561100230;
     * Hm_lpvt_22ea01af58ba2be0fec7c11b25e88e6c=1561100230
     *
     * 必须加入Cookie、User-Agent、Referer，修复查询失败
     * @param type   快递类型或名称
     * @param postid 快递运单号
     * @param temp   时间戳
     * @return
     */
    @Deprecated("")
    @Headers(
        "Domain-Name:kuaidi100",
        "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.157 Safari/537.36",
        "Referer: https://www.kuaidi100.com/"
    )
    @GET("query")
    fun queryExpressInfoSchedule(
        @Header("Cookie") cookie: String?,
        @Query("type") type: String?,
        @Query("postid") postid: String?,
        @Query("temp") temp: Double,
        @Query("phone") phone: String?
    ): Observable<ExpressDetail>

    /**
     * 查询快递信息,已经受限制了（返回数据不对），不明原因
     * https://www.kuaidi100.com/query?type=jd&postid=97408130042&temp=0.47825892409661974&phone=
     *
     * csrftoken=zCVuHlUOhenpYgUulra6voAdLUeNKym_FflcMplrlSg;
     * Hm_lvt_22ea01af58ba2be0fec7c11b25e88e6c=1561095430,1561096160,1561098197,1561099940;
     * Hm_lpvt_22ea01af58ba2be0fec7c11b25e88e6c=1561100145
     *
     * csrftoken=DdFLg1IBs4vyxwQAXFSytYNtP_1I4MbFys9MNC8AgTo;
     * Hm_lvt_22ea01af58ba2be0fec7c11b25e88e6c=1561096160,1561098197,1561099940,1561100230;
     * Hm_lpvt_22ea01af58ba2be0fec7c11b25e88e6c=1561100230
     *
     * 必须加入Cookie、User-Agent、Referer，修复查询失败
     * @param type   快递类型或名称
     * @param postid 快递运单号
     * @param temp   时间戳
     * @return
     */
    @Headers(
        "Domain-Name:kuaidi100",
        "Referer: https://www.kuaidi100.com/",
        "X-Requested-With: XMLHttpRequest"
    )
    @GET("query")
    fun queryExpressInfoSchedule(
        @HeaderMap headers: Map<String?, String?>?,
        @Query("type") type: String?,
        @Query("postid") postid: String?,
        @Query("temp") temp: Double,
        @Query("phone") phone: String?
    ): Observable<ExpressDetail>

    /**
     * 采用阿里云查询接口，免费版有限制，暂时停用,个人账号，调用次数有限，切勿频繁使用https://market.aliyun.com/products/57126001/cmapi021863.html?spm=5176.2020520132.101.3.2f967218ShNdbf#sku=yuncode1586300000
     * @param no 快递单号
     * @param type 快递公司字母简写：不知道可不填 95%能自动识别，填写查询速度会更快
     * @return
     */
    @Headers("Authorization:APPCODE dbab3c08c888405194305aa30e7c2109")
    @GET("http://wuliu.market.alicloudapi.com/kdi")
    fun queryExpressInfoScheduleNew(
        @Query("no") no: String?,
        @Query("type") type: String?
    ): Observable<ExpressDetail2>
}