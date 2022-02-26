package com.sjl.bookmark.ui.presenter

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.sjl.bookmark.entity.ExpressCompany
import com.sjl.bookmark.ui.contract.ExpressCompanyContract
import com.sjl.core.util.log.LogUtils
import java.io.IOException
import java.io.InputStream
import java.util.*

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ExpressCompanyPresenter.java
 * @time 2018/4/29 20:37
 * @copyright(C) 2018 song
 */
class ExpressCompanyPresenter : ExpressCompanyContract.Presenter() {
    override fun initCompany(): List<ExpressCompany> {
        val companyList: MutableList<ExpressCompany> = ArrayList()
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
                val company: ExpressCompany = gson.fromJson(obj, ExpressCompany::class.java)
                companyList.add(company)
            }
        } catch (e: IOException) {
            LogUtils.e("解析快递公司json文件异常", e)
        }
        return companyList
    }
}