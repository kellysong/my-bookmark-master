package com.sjl.bookmark.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES
import android.view.View
import androidx.core.content.FileProvider
import com.renny.zxing.utils.QRCodeFactory
import com.sjl.bookmark.BuildConfig
import com.sjl.bookmark.R
import com.sjl.bookmark.app.AppConstant
import com.sjl.bookmark.kotlin.language.I18nUtils
import com.sjl.core.mvp.BaseActivity
import com.sjl.core.mvp.NoPresenter
import com.sjl.core.util.SnackbarUtils
import com.sjl.core.util.ViewUtils
import com.sjl.core.util.log.LogUtils
import kotlinx.android.synthetic.main.activity_qr_create.*
import kotlinx.android.synthetic.main.toolbar_default.*
import java.io.File

/**
 * 我的名片
 *
 * @author Kelly
 * @version 1.0.0
 * @filename MyCardActivity.java
 * @time 2018/2/24 9:51
 * @copyright(C) 2018 song
 */
class MyCardActivity : BaseActivity<NoPresenter>(), View.OnClickListener {

    override fun getLayoutId(): Int {
        return R.layout.activity_qr_create
    }

    override fun initView() {}
    override fun initListener() {
        bindingToolbar(common_toolbar, I18nUtils.getString(R.string.my_card))
        btn_save_card.setOnClickListener(this)
        btn_share_card.setOnClickListener(this)
    }

    override fun initData() {
        try {
            val widthAndHeight = ViewUtils.dp2px(this, 250)
            val sb = StringBuilder() //内容越多，像素点越多
            sb.append("qq:1057291963")
            sb.append("\n")
            sb.append("Email:kelly168163@163.com")
            val qrCode = QRCodeFactory.createQRCode(sb.toString(), widthAndHeight)
            iv_qr_create!!.setImageBitmap(qrCode)
        } catch (e: Exception) {
            LogUtils.e("生成二维码图片（不带图片）异常", e)
        }

//        try {
//            Resources res = this.getResources();
//            Bitmap logoBm = BitmapFactory.decodeResource(res, R.mipmap.ic_launcher);
//            Bitmap qrCode = QRCodeFactory.createQRImage("QQ:1057291963", 400, logoBm);
//            imageView2.setImageBitmap(qrCode);
//        } catch (Exception e) {
//            LogUtils.e("生成二维码图片（带图片）异常", e);
//        }
    }

    override fun onClick(v: View) {
        val file = File(AppConstant.ROOT_PATH + "myCard.png")
        when (v.id) {
            R.id.btn_save_card -> {
                ViewUtils.saveBitmap(cv_content, file.absolutePath)
                val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                //判断是否是AndroidN以及更高的版本
                val uri: Uri
                if (Build.VERSION.SDK_INT >= VERSION_CODES.N) {
                    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    uri = FileProvider.getUriForFile(
                        this,
                        BuildConfig.APPLICATION_ID + ".fileProvider",
                        file
                    )
                } else { //android 7.0以下
                    uri = Uri.fromFile(file)
                }
                intent.data = uri
                sendBroadcast(intent) //发送广播，可以在相册中显示出来
                SnackbarUtils.makeShort(cv_content, getString(R.string.img_save_success)).show()
            }
            R.id.btn_share_card -> {
                if (!file.exists()) {
                    ViewUtils.saveBitmap(cv_content, file.absolutePath)
                }
                var share_intent = Intent()
                //判断是否是AndroidN以及更高的版本
                val shareUri: Uri
                if (Build.VERSION.SDK_INT >= VERSION_CODES.N) {
                    share_intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    shareUri = FileProvider.getUriForFile(
                        this,
                        BuildConfig.APPLICATION_ID + ".fileProvider",
                        file
                    )
                } else { //android 7.0以下
                    shareUri = Uri.fromFile(file)
                }
                share_intent.action = Intent.ACTION_SEND //设置分享行为
                share_intent.type = "image/*" //设置分享内容的类型
                share_intent.putExtra(Intent.EXTRA_STREAM, shareUri)
                share_intent = Intent.createChooser(share_intent, getString(R.string.share_title))
                startActivity(share_intent)
            }
            else -> {}
        }
    }
}