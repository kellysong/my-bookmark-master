package com.sjl.bookmark.api;


import com.sjl.bookmark.entity.ExpressDetail;
import com.sjl.bookmark.entity.ExpressDetail2;
import com.sjl.bookmark.entity.ExpressName;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.Query;



/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename KuaiDi100ApiService.java
 * @time 2018/1/29 15:36
 * @copyright(C) 2018 song
 */
public interface KuaiDi100ApiService {

    /**
     * 查询快递名称
     * http://www.kuaidi100.com/autonumber/autoComNum?resultv2=1&text=479389994039
     * http://www.kuaidi100.com/autonumber/autoComNum?resultv2=1&text=75140941296388
     *
     * @param resultv2
     * @param expressNo 快递运单号
     * @return
     */
    @Headers({"Domain-Name:kuaidi100"})
    @GET("autonumber/autoComNum")
    Observable<ExpressName> queryExpressNameByNo(@Query("resultv2") int resultv2, @Query("text") String expressNo);



    /**
     * 获取cookie
     *  Observable<T> 里面的泛型T 不能是  okhttp3.Response 。
     可以是 retrofit2.Response<T> ，但是T不能为okhttp3.Response,是okhttp3.ResponseBody

     SET-Cookie是来自Server的header，意即在Client设置某个Cookie

     Cookie是浏览器返回到Server所用的header

     * @return
     */
    @Headers({"User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.157 Safari/537.36"})
    @GET("https://www.kuaidi100.com")
    Observable<Response<ResponseBody>> getCookie();


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
    @Deprecated
    @Headers({"Domain-Name:kuaidi100"
            ,"User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.157 Safari/537.36"
            ,"Referer: https://www.kuaidi100.com/"})
    @GET("query")
    Observable<ExpressDetail> queryExpressInfoSchedule(@Header("Cookie") String cookie,@Query("type") String type,

                                                       @Query("postid") String postid, @Query("temp") double temp, @Query("phone") String phone);

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
    @Headers({"Domain-Name:kuaidi100"
            ,"Referer: https://www.kuaidi100.com/"})
    @GET("query")
    Observable<ExpressDetail> queryExpressInfoSchedule(@HeaderMap Map<String, String> headers, @Query("type") String type,

                                                       @Query("postid") String postid, @Query("temp") double temp, @Query("phone") String phone);




    /**
     * 采用阿里云查询接口，免费版有限制，暂时停用
     * @param no 快递单号
     * @param type 快递公司字母简写：不知道可不填 95%能自动识别，填写查询速度会更快
     * @return
     */
    @Headers({"Authorization:APPCODE dbab3c08c888405194305aa30e7c2109"})
    @GET("http://wuliu.market.alicloudapi.com/kdi")
    Observable<ExpressDetail2> queryExpressInfoScheduleNew(@Query("no") String no, @Query("type") String type);


}
