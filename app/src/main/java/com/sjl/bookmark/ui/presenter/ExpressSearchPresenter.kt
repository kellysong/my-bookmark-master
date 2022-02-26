package com.sjl.bookmark.ui.presenter

import android.text.TextUtils
import android.widget.EditText
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.jakewharton.rxbinding2.widget.RxTextView
import com.sjl.bookmark.api.KuaiDi100ApiService
import com.sjl.bookmark.entity.ExpressCompany
import com.sjl.bookmark.entity.ExpressName
import com.sjl.bookmark.ui.contract.ExpressSearchContract
import com.sjl.bookmark.ui.presenter.ExpressDetailPresenter
import com.sjl.core.net.RetrofitHelper
import com.sjl.core.net.RxSchedulers
import com.sjl.core.util.log.LogUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import java.io.IOException
import java.io.InputStream
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ExpressSearchPresenter.java
 * @time 2018/4/26 17:37
 * @copyright(C) 2018 song
 */
class ExpressSearchPresenter : ExpressSearchContract.Presenter() {
    override fun initCompany(): Map<String, ExpressCompany> {
        val companyMap: MutableMap<String, ExpressCompany> = HashMap()
        try {
            val `is`: InputStream = mContext.assets.open("company.json")
            val size: Int = `is`.available()
            val buffer: ByteArray = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            val json: String = String(buffer)
            val gson: Gson = Gson()
            val parser: JsonParser = JsonParser()
            val jArray: JsonArray = parser.parse(json).asJsonArray
            for (obj: JsonElement? in jArray) {
                //        // {"name":"安能物流","code":"annengwuliu","logo":"56/annengwuliu.png"},
                val company: ExpressCompany = gson.fromJson(obj, ExpressCompany::class.java)
                if (!TextUtils.isEmpty(company.code)) {
                    companyMap.put(company.code, company)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return companyMap
    }

    /**
     * 匹配建议的快递公司列表
     *
     * @param postId
     */
    override fun getSuggestionList(postId: String) {
        val apiService: KuaiDi100ApiService = RetrofitHelper.getInstance().getApiService(
            KuaiDi100ApiService::class.java
        )
        val cookie1: String = ExpressDetailPresenter.Companion.disguiseCookie(
            "Hm_lvt_22ea01af58ba2be0fec7c11b25e88e6c",
            4,
            5 * 1000
        )
        LogUtils.w(cookie1)
        val cookie2: String = ExpressDetailPresenter.Companion.disguiseCookie(
            "Hm_lpvt_22ea01af58ba2be0fec7c11b25e88e6c",
            1,
            0
        )
        LogUtils.w(cookie2)
        val cookie: String = cookie1 + ";" + cookie2
        val headersMap: MutableMap<String?, String?> = HashMap()
        headersMap.put("Cookie", cookie)
        headersMap.put("User-Agent", ExpressDetailPresenter.randomUserAgent)
        apiService.queryExpressNameByNo(headersMap, postId)
            .compose(RxSchedulers.applySchedulers<ExpressName>())
            .`as`(bindLifecycle())
            .subscribe(object : Consumer<ExpressName> {
                @Throws(Exception::class)
                override fun accept(expressName: ExpressName) {
                    mView.showSuggestionCompany(expressName)
                }
            }, object : Consumer<Throwable?> {
                @Throws(Exception::class)
                override fun accept(throwable: Throwable?) {
                    LogUtils.e("搜索快递公司异常", throwable)
                }
            })
    }

    @Deprecated("")
    override fun getSuggestionList(editText: EditText) {
        RxTextView.textChanges(editText) //限流时间500ms
            .debounce(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread()) //CharSequence转换为String
            .map(object : Function<CharSequence, String> {
                @Throws(Exception::class)
                override fun apply(charSequence: CharSequence): String {
                    val s: String = charSequence.toString()
                    return s
                }
            })
            .subscribe(object : Consumer<String> {
                @Throws(Exception::class)
                override fun accept(s: String) {
                    getSuggestionList(s)
                }
            })
    }
}