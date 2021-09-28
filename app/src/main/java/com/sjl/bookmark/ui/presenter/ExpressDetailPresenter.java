package com.sjl.bookmark.ui.presenter;

import android.content.Intent;

import com.sjl.bookmark.api.KuaiDi100ApiService;
import com.sjl.bookmark.app.AppConstant;
import com.sjl.bookmark.app.MyApplication;
import com.sjl.bookmark.dao.impl.HistoryExpressService;
import com.sjl.bookmark.entity.ExpressDetail;
import com.sjl.bookmark.entity.ExpressDetail2;
import com.sjl.bookmark.entity.ExpressSearchInfo;
import com.sjl.bookmark.entity.table.HistoryExpress;
import com.sjl.bookmark.ui.contract.ExpressDetailContract;
import com.sjl.core.net.RetrofitHelper;
import com.sjl.core.net.RxSchedulers;
import com.sjl.core.util.datetime.TimeUtils;
import com.sjl.core.util.log.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import okhttp3.Headers;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ExpressDetailPresenter.java
 * @time 2018/5/2 11:15
 * @copyright(C) 2018 song
 */
public class ExpressDetailPresenter extends ExpressDetailContract.Presenter {
    private HistoryExpressService mHistoryExpressService;
    private static final String[] userAgents = new String[]{
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36 Edge/17.17134",

            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.835.163 Safari/535.1",
            "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0) Gecko/20100101 Firefox/6.0",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1 Safari/534.50",
            "Opera/9.80 (Windows NT 6.1; U; zh-cn) Presto/2.9.168 Version/11.50",
            "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Win64; x64; Trident/5.0; .NET CLR 2.0.50727; SLCC2; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; InfoPath.3; .NET4.0C; Tablet PC 2.0; .NET4.0E)",
            "Mozilla/5.0 (Windows; U; Windows NT 6.1; ) AppleWebKit/534.12 (KHTML, like Gecko) Maxthon/3.0 Safari/534.12",
            "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US) AppleWebKit/534.3 (KHTML, like Gecko) Chrome/6.0.472.33 Safari/534.3 SE 2.X MetaSr 1.0",
            "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; InfoPath.3; .NET4.0C; .NET4.0E)",
            "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.41 Safari/535.1 QQBrowser/6.9.11079.201",

            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.157 Safari/537.36",

            "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; AcooBrowser; .NET CLR 1.1.4322; .NET CLR 2.0.50727)",
            "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0; Acoo Browser; SLCC1; .NET CLR 2.0.50727; Media Center PC 5.0; .NET CLR 3.0.04506)",
            "Mozilla/4.0 (compatible; MSIE 7.0; AOL 9.5; AOLBuild 4337.35; Windows NT 5.1; .NET CLR 1.1.4322; .NET CLR 2.0.50727)",
            "Mozilla/5.0 (Windows; U; MSIE 9.0; Windows NT 9.0; en-US)",
            "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Win64; x64; Trident/5.0; .NET CLR 3.5.30729; .NET CLR 3.0.30729; .NET CLR 2.0.50727; Media Center PC 6.0)",
            "Mozilla/5.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; .NET CLR 1.0.3705; .NET CLR 1.1.4322)",
            "Mozilla/4.0 (compatible; MSIE 7.0b; Windows NT 5.2; .NET CLR 1.1.4322; .NET CLR 2.0.50727; InfoPath.2; .NET CLR 3.0.04506.30)",
            "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN) AppleWebKit/523.15 (KHTML, like Gecko, Safari/419.3) Arora/0.3 (Change: 287 c9dfb30)",
            "Mozilla/5.0 (X11; U; Linux; en-US) AppleWebKit/527+ (KHTML, like Gecko, Safari/419.3) Arora/0.6",
            "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.2pre) Gecko/20070215 K-Ninja/2.1.1",
            "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9) Gecko/20080705 Firefox/3.0 Kapiko/3.0",
            "Mozilla/5.0 (X11; Linux i686; U;) Gecko/20070322 Kazehakase/0.4.5",
            "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.8) Gecko Fedora/1.9.0.8-1.fc10 Kazehakase/0.5.6",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.56 Safari/535.11",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_3) AppleWebKit/535.20 (KHTML, like Gecko) Chrome/19.0.1036.7 Safari/535.20",
            "Opera/9.80 (Macintosh; Intel Mac OS X 10.6.8; U; fr) Presto/2.9.168 Version/11.52"
    };

    public ExpressDetailPresenter() {
        mHistoryExpressService = new HistoryExpressService(MyApplication.getContext());
    }

    @Override
    public void init(Intent intent) {
        ExpressSearchInfo searchInfo = (ExpressSearchInfo) intent.getSerializableExtra(AppConstant.Extras.SEARCH_INFO);
        mView.showExpressSource(searchInfo);

    }

    /**
     * 获取快递备注信息
     *
     * @param postId
     * @return
     */
    @Override
    public String getExpressRemark(String postId) {
        HistoryExpress express = mHistoryExpressService.queryHistoryExpress(postId);
        if (express == null) {
            return null;
        } else {
            return express.getRemark();
        }
    }

    /**
     * 查询快递明细
     *
     * @param searchInfo
     */
    @Override
    public void queryExpressDetail(final ExpressSearchInfo searchInfo) {

        final KuaiDi100ApiService apiService = RetrofitHelper.getInstance().getApiService(KuaiDi100ApiService.class);
//        aliCloudQuery(apiService,searchInfo);

        disguiseKuaiDi100Query(searchInfo);

       /* double random = Math.random();
        apiService.queryExpressInfoSchedule(cookie, searchInfo.getCode(), searchInfo.getPost_id(), random, "")
                .compose(RxSchedulers.<ExpressDetail>applySchedulers())
                .as(ExpressDetailPresenter.this.<ExpressDetail>bindLifecycle())
                .subscribe(new Consumer<ExpressDetail>() {
                    @Override
                    public void accept(ExpressDetail expressDetail) throws Exception {
                        LogUtils.i("快递信息：" + expressDetail.toString());
                        mView.showExpressDetail(expressDetail);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("搜索快递明细异常", throwable);
                        mView.showErrorInfo();
                    }
                });*/

    }

    private void aliCloudQuery(KuaiDi100ApiService apiService, ExpressSearchInfo searchInfo) {
        apiService.queryExpressInfoScheduleNew(searchInfo.getPost_id(), "")
                .map(new Function<ExpressDetail2, ExpressDetail>() {
                    @Override
                    public ExpressDetail apply(ExpressDetail2 expressDetail2) throws Exception {
                        if ("0".equals(expressDetail2.getStatus())) {
                            List<ExpressDetail2.ResultBean.ListBean> list = expressDetail2.getResult().getList();
                            ExpressDetail expressDetail = new ExpressDetail();
                            expressDetail.setStatus("200");
                            if (expressDetail2.getResult().getDeliverystatus().equals("3")){
                                expressDetail.setIscheck("1");//已经签收
                            }else {
                                expressDetail.setIscheck("0");
                            }
                            expressDetail.setCom(searchInfo.getCode());
                            ExpressDetail.DataBean dataBean;
                            List< ExpressDetail.DataBean> dataBeanList = new ArrayList<>();
                            for (ExpressDetail2.ResultBean.ListBean listBean:list){
                                dataBean = new ExpressDetail.DataBean();
                                dataBean.setFtime(listBean.getTime());
                                dataBean.setTime(listBean.getTime());
                                dataBean.setContext(listBean.getStatus());
                                dataBeanList.add(dataBean);
                            }
                            expressDetail.setData(dataBeanList);
                            return  expressDetail;
                        } else {
                            ExpressDetail expressDetail = new ExpressDetail();
                            expressDetail.setStatus(expressDetail.getStatus());
                            expressDetail.setMessage(expressDetail2.getMsg());

                            return expressDetail;
                        }
                    }
                })
                .compose(RxSchedulers.<ExpressDetail>applySchedulers())
                .as(this.<ExpressDetail>bindLifecycle())
                .subscribe(new Consumer<ExpressDetail>() {
                    @Override
                    public void accept(ExpressDetail expressDetail) throws Exception {
                        mView.showExpressDetail(expressDetail);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("搜索快递明细异常", throwable);
                        mView.showErrorInfo();
                    }
                });

    }

    private void disguiseKuaiDi100Query(ExpressSearchInfo searchInfo) {
        final KuaiDi100ApiService apiService = RetrofitHelper.getInstance().getApiService(KuaiDi100ApiService.class);
        apiService.getCookie().concatMap(new Function<Response<ResponseBody>, ObservableSource<ExpressDetail>>() {
            @Override
            public ObservableSource<ExpressDetail> apply(Response<ResponseBody> responseBodyResponse) throws Exception {
                double random = Math.random();
                Headers headers = responseBodyResponse.headers();
                List<String> cookies = headers.values("Set-Cookie");
                StringBuilder cookiesSb = new StringBuilder();
                /**
                 * globacsrftoken=NLHvl3wteSgfNyfLwgeUBe4-ZSRlHi6UO_VNMZ6eTqk; Domain=www.kuaidi100.com; Expires=Tue, 21-Sep-2021 02:20:19 GMT; Path=/globalauto.do
                 * csrftoken=NLHvl3wteShwh8Ji-YjD884Dh0IfWiNYSIg6CVVixvc; Domain=.kuaidi100.com; Expires=Tue, 21-Sep-2021 02:20:19 GMT; Path=/query
                 * _adadqeqwe1321312dasddocTitle=kuaidi100; domain=.kuaidi100.com; path=/;
                 * _adadqeqwe1321312dasddocReferrer=; domain=.kuaidi100.com; path=/;
                 * _adadqeqwe1321312dasddocHref=; domain=.kuaidi100.com; path=/;
                 * _adadqeqwe1321312dasddocTitle=kuaidi100; domain=.kuaidi100.com; path=/all;
                 */
             /*   for (String c : cookies) {
                    System.out.println(c);
                    if (c.contains("csrftoken")) {
                        String[] split = c.split(";");
                        cookiesSb.append(split[0]).append(";");
                    } else if (c.contains("_clck")) {
                        String[] split = c.split(";");
                        cookiesSb.append(split[0]).append(";");
                    } else if (c.contains("_clsk")) {
                        String[] split = c.split(";");
                        cookiesSb.append(split[0]).append(";");
                    } else if (c.endsWith("Title")) {
                        String[] split = c.split(";");
                        cookiesSb.append(split[0]).append(";");
                    } else if (c.contains("Referrer")) {
                        String[] split = c.split(";");
                        cookiesSb.append(split[0]).append(";");
                    } else if (c.contains("Href")) {
                        String[] split = c.split(";");
                        cookiesSb.append(split[0]).append(";");
                    }
                }*/
                String cookiesStr = cookies.toString();
//                String cookiesStr = cookiesSb.toString();
                LogUtils.w("Set-Cookie:" + cookiesStr);
                String cookie1 = disguiseCookie("Hm_lvt_22ea01af58ba2be0fec7c11b25e88e6c", 4, 5 * 1000);
                LogUtils.w("cookie1:" + cookie1);
                String cookie2 = disguiseCookie("Hm_lpvt_22ea01af58ba2be0fec7c11b25e88e6c", 1, 0);
                LogUtils.w("cookie2:" + cookie2);
                String cookie = cookiesStr + ";" + cookie1 + ";" + cookie2;
                Map<String, String> headersMap = new HashMap<>();
                LogUtils.w("cookiesStr:" + cookiesStr);
                headersMap.put("Cookie", cookie);
                headersMap.put("User-Agent", getRandomUserAgent());
                return apiService.queryExpressInfoSchedule(headersMap, searchInfo.getCode(), searchInfo.getPost_id(), random, "");
            }
        }).compose(RxSchedulers.<ExpressDetail>applySchedulers())
                .as(this.<ExpressDetail>bindLifecycle())
                .subscribe(new Consumer<ExpressDetail>() {
                    @Override
                    public void accept(ExpressDetail expressDetail) throws Exception {
                        LogUtils.i("快递信息：" + expressDetail.toString());
                        mView.showExpressDetail(expressDetail);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("搜索快递明细异常", throwable);
                        mView.showErrorInfo();
//                        aliCloudQuery(apiService,searchInfo);
                    }
                });
    }

    /**
     * 获取随机的UserAgent,伪装UserAgent
     *
     * @return
     */
    public static String getRandomUserAgent() {
        int index = (int) (Math.random() * userAgents.length);
        return userAgents[index];
    }

    /**
     * 伪装cookie
     *
     * @param name
     * @param num
     * @param step
     * @return
     */
    @NonNull
    public static String disguiseCookie(String name, int num, int step) {
        StringBuilder sb = new StringBuilder();
        sb.append(name + "=");
        long timestamp = System.currentTimeMillis();
        for (int i = num; i > 0; i--) {
            timestamp -= step;
            sb.append(TimeUtils.dateToTimestamp(timestamp) + ",");
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }

    /**
     * 更新本地快递信息
     *
     * @param searchInfo    查询信息
     * @param expressDetail 快递明细,可为空，不为空时更新验收时间
     */
    @Override
    public void updateExpressDetail(ExpressSearchInfo searchInfo, ExpressDetail expressDetail) {
        HistoryExpress history;
        boolean ret = checkExistExpress(searchInfo.getPost_id());
        if (ret) {
            LogUtils.i("存在本地快递信息,更新");
            history = mHistoryExpressService.queryHistoryExpress(searchInfo.getPost_id());
        } else {
            LogUtils.i("不存在本地快递信息，新增");
            history = new HistoryExpress();
        }
        history.setPostId(searchInfo.getPost_id());
        history.setCompanyParam(searchInfo.getCode());
        history.setCompanyName(searchInfo.getName());
        history.setCompanyIcon(searchInfo.getLogo());
        history.setCheckStatus(searchInfo.getIs_check());
        if (expressDetail != null) {
            List<ExpressDetail.DataBean> data = expressDetail.getData();
            if (data != null && !data.isEmpty()) {
                history.setSignTime(data.get(0).getTime());
            }

        }
        //会导致loadAll顺序改变
        mHistoryExpressService.createOrUpdateHistoryExpress(history);
    }


    /**
     * 判断本地是否缓存有快递信息
     *
     * @param postId
     * @return
     */
    @Override
    public boolean checkExistExpress(String postId) {
        boolean ret = mHistoryExpressService.isExistHistoryExpress(postId);
        return ret;
    }

    /**
     * 更新快递单备注信息
     *
     * @param postId
     * @param remark
     */
    @Override
    public void updateExpressRemark(String postId, String remark) {
        HistoryExpress history = mHistoryExpressService.queryHistoryExpress(postId);
        history.setRemark(remark);
        mHistoryExpressService.updateHistoryExpress(history);
    }
}
