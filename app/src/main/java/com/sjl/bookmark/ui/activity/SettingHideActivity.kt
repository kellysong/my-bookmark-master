package com.sjl.bookmark.ui.activity

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.text.TextUtils
import android.widget.LinearLayout
import android.widget.TextView
import com.sjl.bookmark.R
import com.sjl.bookmark.kotlin.util.SpUtils
import com.sjl.core.mvp.BaseActivity
import com.sjl.core.mvp.NoPresenter
import kotlinx.android.synthetic.main.setting_hide_activity.*
import kotlinx.android.synthetic.main.toolbar_default.*
import java.util.*


/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename SettingHideActivity
 * @time 2022/12/3 14:01
 * @copyright(C) 2022 song
 */ 
class SettingHideActivity : BaseActivity<NoPresenter>(){
    override fun getLayoutId(): Int {
      return R.layout.setting_hide_activity
    }

    override fun initView() {
      
    }

    override fun initListener() {
        bindingToolbar(common_toolbar, "隐藏设置")
        btn_update_bookmark_url.setOnClickListener {
            val url = et_bookmark_url.text.toString().trim()
            if (!TextUtils.isEmpty(url)){
                SpUtils.saveBookmarkBaseUrl(url)
            }

        }
        btn_reset_bookmark_flag.setOnClickListener {
            val sharedPreferences = getSharedPreferences("bookmark", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean("readFlag", false)
            editor.commit()
            openActivity(MainActivity::class.java)
            finish()
        }

        btn_add_mourning_days.setOnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(this@SettingHideActivity,
                AlertDialog.THEME_HOLO_LIGHT,
                { datePicker, year, monthOfYear, dayOfMonth ->
                    val date = year.toString() + "-" + (formatDateNum(monthOfYear + 1)) + "-" + formatDateNum(dayOfMonth)
                    saveMourningDays(date)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
            datePickerDialog.show()

        }

        btn_clear_mourning_days.setOnClickListener {
            SpUtils.saveMourningDays("")
            showMourningDays()
        }

    }

    private fun formatDateNum(num: Int): String {


        return if (num < 10) {
            "0$num"
        } else {
            num.toString()
        }
    }

    private fun saveMourningDays(date: String) {
        val mourningDays = SpUtils.getMourningDays()
        if (mourningDays.isNullOrEmpty()){
            SpUtils.saveMourningDays("$date")
            return
        }
        val join = TextUtils.join(",", mourningDays)
        if (join.contains(date)){
            return
        }
        addMourningDayView(ll_mourning_days,date)
        SpUtils.saveMourningDays("$join,$date")

    }

    override fun initData() {
        et_bookmark_url.setText(SpUtils.getBookmarkBaseUrl())
        showMourningDays()
    }

    private fun showMourningDays() {
        ll_mourning_days.removeAllViews()
        val mourningDays = SpUtils.getMourningDays()
        if (mourningDays.isNullOrEmpty()){
            return
        }
        for (mourningDay in mourningDays) {
            addMourningDayView(ll_mourning_days,mourningDay)
        }


    }

    private fun addMourningDayView(llMourningDays: LinearLayout, mourningDay: String) {
        if (mourningDay.isNullOrEmpty()){
            return
        }
        val textView = TextView(this)
        textView.text = mourningDay
        val dimension = resources.getDimensionPixelOffset(R.dimen.dp_5)
        textView.setPadding(dimension,dimension,dimension,dimension)
       /* val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT)
        textView.layoutParams = lp*/
        llMourningDays.addView(textView)
    }

}