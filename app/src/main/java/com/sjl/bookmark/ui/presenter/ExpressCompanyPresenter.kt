package com.sjl.bookmark.ui.presenter;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sjl.bookmark.entity.ExpressCompany;
import com.sjl.bookmark.ui.contract.ExpressCompanyContract;
import com.sjl.core.util.log.LogUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ExpressCompanyPresenter.java
 * @time 2018/4/29 20:37
 * @copyright(C) 2018 song
 */
public class ExpressCompanyPresenter extends ExpressCompanyContract.Presenter {


    @Override
    public List<ExpressCompany> initCompany() {
        List<ExpressCompany> companyList = new ArrayList<>();
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
                ExpressCompany company = gson.fromJson(obj, ExpressCompany.class);
                companyList.add(company);
            }
        } catch (IOException e) {
            LogUtils.e("解析快递公司json文件异常",e);
        }
        return companyList;
    }
}
