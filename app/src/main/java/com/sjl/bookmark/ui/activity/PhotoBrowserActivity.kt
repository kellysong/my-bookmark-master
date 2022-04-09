package com.sjl.bookmark.ui.activity

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.chrisbanes.photoview.PhotoView
import com.sjl.bookmark.R
import com.sjl.core.util.file.FileUtils
import com.sjl.core.util.file.FileUtils.SaveResultCallback
import com.sjl.core.util.log.LogWriter
import kotlinx.android.synthetic.main.activity_photo_browser.*
import java.io.File

/**
 * webview图片浏览
 */
class PhotoBrowserActivity : Activity(), View.OnClickListener {

    private var curImageUrl: String? = ""
    private lateinit var imageUrls: Array<String>
    private var curPosition: Int = -1
    private lateinit var initialedPositions: IntArray
    private var objectAnimator: ObjectAnimator? = null
    private var curPage: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_photo_browser)
        imageUrls = intent.getStringArrayExtra("imageUrls") as Array<String>
        curImageUrl = intent.getStringExtra("curImageUrl")
        initialedPositions = IntArray(imageUrls.size)
        initInitialedPositions()
        saveTv.setOnClickListener(this)
        crossIv.setOnClickListener(this)
        pager.pageMargin = (resources.displayMetrics.density * 15).toInt()
        pager.adapter = object : PagerAdapter() {
            override fun getCount(): Int {
                return imageUrls.size
            }

            override fun isViewFromObject(view: View, `object`: Any): Boolean {
                return view === `object`
            }

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                val view: PhotoView = PhotoView(this@PhotoBrowserActivity)
                if (imageUrls[position] != null && "" != imageUrls[position]) {
                    view.scaleType = ImageView.ScaleType.FIT_CENTER
                    Glide.with(this@PhotoBrowserActivity)
                        .load(imageUrls[position])
                        .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .fitCenter()
                        .listener(object : RequestListener<Drawable?> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any,
                                target: Target<Drawable?>,
                                isFirstResource: Boolean
                            ): Boolean {
                                if (position == curPosition) {
                                    hideLoadingAnimation()
                                }
                                showErrorLoading()
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any,
                                target: Target<Drawable?>,
                                dataSource: DataSource,
                                isFirstResource: Boolean
                            ): Boolean {
                                occupyOnePosition(position)
                                if (position == curPosition) {
                                    hideLoadingAnimation()
                                }
                                return false
                            }
                        }).into(view)
                    container.addView(view)
                }
                return view
            }

            override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                releaseOnePosition(position)
                container.removeView(`object` as View?)
            }

            override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
                curPage = `object` as View?
            }
        }
        curPosition = if (returnClickedPosition() == -1) 0 else returnClickedPosition()
        pager.currentItem = curPosition
        pager.tag = curPosition
        if (initialedPositions[curPosition] != curPosition) { //如果当前页面未加载完毕，则显示加载动画，反之相反；
            showLoadingAnimation()
        }
        photoOrderTv.text = (curPosition + 1).toString() + "/" + imageUrls.size //设置页面的编号
        //实现对页面滑动事件的监听——>此处主要用来处理设置当前页面的position、动画、页面序号显示的逻辑
        pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (initialedPositions[position] != position) { //如果当前页面未加载完毕，则显示加载动画，反之相反；
                    showLoadingAnimation()
                } else {
                    hideLoadingAnimation()
                }
                curPosition = position
                photoOrderTv.text = (position + 1).toString() + "/" + imageUrls.size //设置页面的编号
                pager.tag = position //为当前view设置tag
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    /**
     * 获得用户点击的是哪一张图片的位置并设置当前是哪一个page
     * @return
     */
    private fun returnClickedPosition(): Int {
        if (imageUrls == null || curImageUrl == null) {
            return -1
        }
        //通过遍历当前url与所有url来匹配获取索引
        for (i in imageUrls.indices) {
            if ((curImageUrl == imageUrls.get(i))) {
                return i
            }
        }
        return -1
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.crossIv -> finish()
            R.id.saveTv -> savePhotoToLocal()
            else -> {}
        }
    }

    private fun showLoadingAnimation() {
        centerIv.visibility = View.VISIBLE
        centerIv.setImageResource(R.mipmap.loading)
        if (objectAnimator == null) {
            objectAnimator = ObjectAnimator.ofFloat(centerIv, "rotation", 0f, 360f)
            objectAnimator?.run {
                duration = 2000
                repeatCount = ValueAnimator.INFINITE
                if (Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR2) {
                    setAutoCancel(true)
                }
            }

        }
        objectAnimator?.start()
    }

    private fun hideLoadingAnimation() {
        releaseResource()
        centerIv.visibility = View.GONE
    }

    private fun showErrorLoading() {
        centerIv.visibility = View.VISIBLE
        releaseResource()
        centerIv.setImageResource(R.mipmap.load_error)
    }

    private fun releaseResource() {
        objectAnimator?.cancel()
        if (centerIv.animation != null) {
            centerIv.animation.cancel()
        }
    }

    private fun occupyOnePosition(position: Int) {
        initialedPositions[position] = position
    }

    private fun releaseOnePosition(position: Int) {
        initialedPositions[position] = -1
    }

    private fun initInitialedPositions() {
        for (i in initialedPositions.indices) {
            initialedPositions[i] = -1
        }
    }

    private fun savePhotoToLocal() {
//        ViewGroup containerTemp = (ViewGroup) mPager.findViewWithTag(mPager.getCurrentItem());
//        if (containerTemp == null) {
//            return;
//        }
//        PhotoView photoViewTemp = (PhotoView) containerTemp.getChildAt(0);
        val photoViewTemp: PhotoView? = curPage as PhotoView?
        if (photoViewTemp != null) {
            val glideBitmapDrawable: BitmapDrawable =
                photoViewTemp.drawable as BitmapDrawable? ?: return
            val bitmap: Bitmap = glideBitmapDrawable.bitmap ?: return
            FileUtils.savePhoto(this, bitmap, object : SaveResultCallback {
                override fun onSavedSuccess(file: File) {
                    runOnUiThread {
                        Toast.makeText(
                            this@PhotoBrowserActivity,
                            R.string.save_success,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onSavedFailed(e: Exception) {
                    LogWriter.e("保存图片失败",e)
                    runOnUiThread {
                        Toast.makeText(
                            this@PhotoBrowserActivity,
                            R.string.save_failed,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            })
        }
    }

    override fun onDestroy() {
        releaseResource()
        curPage = null
        if (pager != null) {
            pager.removeAllViews()
        }
        super.onDestroy()
    }
}