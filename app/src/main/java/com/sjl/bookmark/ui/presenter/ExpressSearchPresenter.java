package com.sjl.bookmark.ui.presenter;

import android.text.TextUtils;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.sjl.bookmark.api.KuaiDi100ApiService;
import com.sjl.bookmark.entity.ExpressCompany;
import com.sjl.bookmark.entity.ExpressName;
import com.sjl.bookmark.ui.contract.ExpressSearchContract;
import com.sjl.core.net.RetrofitHelper;
import com.sjl.core.net.RxSchedulers;
import com.sjl.core.util.log.LogUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ExpressSearchPresenter.java
 * @time 2018/4/26 17:37
 * @copyright(C) 2018 song
 */
public class ExpressSearchPresenter extends ExpressSearchContract.Presenter {


    @Override
    public Map<String, ExpressCompany> initCompany() {
        Map<String, ExpressCompany> companyMap = new HashMap<>();
        try {
            InputStream is = mContext.getAssets().open("company.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer);

            Gson gson = new Gson();
            JsonParser parser = new JsonParser();
            JsonArray jArray = parser.parse(json).getAsJsonArray();
            for (JsonElement obj : jArray) {
                //        // {"name":"安能物流","code":"annengwuliu","logo":"56/annengwuliu.png"},

                ExpressCompany company = gson.fromJson(obj, ExpressCompany.class);
                if (!TextUtils.isEmpty(company.getCode())) {
                    companyMap.put(company.getCode(), company);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return companyMap;
    }

    /**
     * 匹配建议的快递公司列表
     *
     * @param postId
     */
    @Override
    public void getSuggestionList(String postId) {
        KuaiDi100ApiService apiService = RetrofitHelper.getInstance().getApiService(KuaiDi100ApiService.class);
        apiService.queryExpressNameByNo(1, postId)
                .compose(RxSchedulers.<ExpressName>applySchedulers())
                .as(this.<ExpressName>bindLifecycle())
                .subscribe(new Consumer<ExpressName>() {
                    @Override
                    public void accept(ExpressName expressName) throws Exception {
                        mView.showSuggestionCompany(expressName);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("搜索快递公司异常", throwable);
                    }
                });


    }

    @Deprecated
    @Override
    public void getSuggestionList(EditText editText) {
        RxTextView.textChanges(editText)
                //限流时间500ms
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                //CharSequence转换为String
                .map(new Function<CharSequence, String>() {
                    @Override
                    public String apply(CharSequence charSequence) throws Exception {
                        String s = charSequence.toString();
                        return s;

                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        getSuggestionList(s);
                    }
                });
    }
}
