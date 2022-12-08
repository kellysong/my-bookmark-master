package com.sjl.bookmark.ui.adapter

import android.content.Intent
import android.os.Parcelable
import android.view.LayoutInflater
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.sjl.bookmark.R
import com.sjl.bookmark.entity.Category
import com.sjl.bookmark.net.HttpConstant
import com.sjl.bookmark.ui.activity.ArticleTypeActivity
import me.gujun.android.taggroup.TagGroup
import java.util.ArrayList

/**
 * Android知识分类右侧
 *
 * @author Kelly
 * @version 1.0.0
 * @filename CategoryRightAdapter
 * @time 2022/12/7 17:42
 * @copyright(C) 2022 song
 */
class CategoryRightAdapter(data: List<Category>?) : BaseQuickAdapter<Category, BaseViewHolder>(R.layout.category_knowledge_right_recycle_item, data) {
    override fun convert(helper: BaseViewHolder, item: Category) {
//        helper.setText(R.id.typeItemFirst, item.name)
        val tagGroup = helper.getView<TagGroup>(R.id.tg_category)
        tagGroup.removeAllViews()
        val from = LayoutInflater.from(mContext)
        for (childrenBean in item.children) {
            val tv: TextView =  from.inflate(R.layout.search_label_tv, null, false) as TextView
            tv.text = childrenBean.name
            tv.setOnClickListener {
                val chapterName = childrenBean.name
                val intent = Intent(mContext, ArticleTypeActivity::class.java)
                intent.putExtra(HttpConstant.CONTENT_TITLE_KEY, chapterName)
                val children: MutableList<Category.ChildrenBean?> = ArrayList()
                children.add(Category.ChildrenBean(childrenBean.id, chapterName))
                intent.putParcelableArrayListExtra(HttpConstant.CONTENT_CHILDREN_DATA_KEY, children as ArrayList<out Parcelable?>)
                intent.putExtra(HttpConstant.CONTENT_OPEN_FLAG, "0")
                mContext.startActivity(intent)
            }
            tagGroup.addView(tv)
        }

    }
}