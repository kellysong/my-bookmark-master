package com.sjl.bookmark.ui.adapter

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename RecyclerViewDivider.java
 * @time 2018/2/27 14:45
 * @copyright(C) 2018 song
 */
class RecyclerViewDivider(context: Context, orientation: Int) : ItemDecoration() {
    private var mPaint: Paint? = null
    private var mDivider: Drawable?
    private var mDividerHeight = 2 //分割线高度，默认为1px
    private val mOrientation //列表的方向：LinearLayoutManager.VERTICAL或LinearLayoutManager.HORIZONTAL
            : Int

    /**
     * 自定义分割线
     *
     * @param context
     * @param orientation 列表方向
     * @param drawableId  分割线图片
     */
    constructor(context: Context, orientation: Int, drawableId: Int) : this(context, orientation) {
        mDivider = ContextCompat.getDrawable(context, drawableId)
        mDividerHeight = mDivider!!.intrinsicHeight
    }

    /**
     * 自定义分割线
     *
     * @param context
     * @param orientation   列表方向
     * @param dividerHeight 分割线高度
     * @param dividerColor  分割线颜色
     */
    constructor(context: Context, orientation: Int, dividerHeight: Int, dividerColor: Int) : this(context, orientation) {
        mDividerHeight = dividerHeight
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint!!.color = dividerColor
        mPaint!!.style = Paint.Style.FILL
    }

    //获取分割线尺寸
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        if (this.mDivider == null) {
            outRect[0, 0, 0] = mDividerHeight
        } else {
            if (this.mOrientation == LinearLayoutManager.VERTICAL) {
                //纵向RecyclerView
                val childAdapterPosition = parent.getChildAdapterPosition(view)
                val lastCount = parent.adapter?.itemCount?.minus(1)
                //如果不是最后一条 正常赋值 如果是最后一条 赋值为0
                if (childAdapterPosition != lastCount){
                    this.mDivider?.let {
                        outRect.set(0, 0, 0, it.intrinsicHeight)
                    }
                } else {
                    outRect.set(0, 0, 0, 0)
                }
            } else {
                //横向RecyclerView
                val childAdapterPosition = parent.getChildAdapterPosition(view)
                val lastCount = parent.adapter?.itemCount?.minus(1)
                //如果不是最后一条 正常赋值 如果是最后一条 赋值为0
                if (childAdapterPosition != lastCount){
                    this.mDivider?.let {
                        outRect.set(0, 0, it.intrinsicWidth, 0)
                    }
                } else {
                    outRect.set(0, 0, 0, 0)
                }
            }

        }

    }

    //绘制分割线
    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        if (mOrientation == LinearLayoutManager.VERTICAL) {
            drawVertical(c, parent)
        } else {
            drawHorizontal(c, parent)
        }
    }

    //绘制横向 item 分割线
    private fun drawHorizontal(canvas: Canvas, parent: RecyclerView) {
        val left = parent.paddingLeft
        val right = parent.measuredWidth - parent.paddingRight
        val childSize = parent.childCount
        for (i in 0 until childSize) {
            val child = parent.getChildAt(i)
            val layoutParams = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + layoutParams.bottomMargin
            val bottom = top + mDividerHeight
            if (mDivider != null) {
                mDivider!!.setBounds(left, top, right, bottom)
                mDivider!!.draw(canvas)
            }
            mPaint?.let {
                canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), it)
            }
        }
    }

    //绘制纵向 item 分割线
    private fun drawVertical(canvas: Canvas, parent: RecyclerView) {
        val top = parent.paddingTop
        val bottom = parent.measuredHeight - parent.paddingBottom
        val childSize = parent.childCount
        for (i in 0 until childSize) {
            val child = parent.getChildAt(i)
            val layoutParams = child.layoutParams as RecyclerView.LayoutParams
            val left = child.right + layoutParams.rightMargin
            val right = left + mDividerHeight
            if (mDivider != null) {
                mDivider!!.setBounds(left, top, right, bottom)
                mDivider!!.draw(canvas)
            }
            mPaint?.let {
                canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), it)
            }

        }
    }

    companion object {
        private val ATTRS = intArrayOf(android.R.attr.listDivider)
    }

    /**
     * 默认分割线：高度为2px，颜色为灰色
     *
     * @param context
     * @param orientation 列表方向
     */
    init {
        require(!(orientation != LinearLayoutManager.VERTICAL && orientation != LinearLayoutManager.HORIZONTAL)) { "请输入正确的参数！" }
        mOrientation = orientation
        val a = context.obtainStyledAttributes(ATTRS)
        mDivider = a.getDrawable(0)
        a.recycle()
    }
}