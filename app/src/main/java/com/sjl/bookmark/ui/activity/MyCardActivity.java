package com.sjl.bookmark.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.renny.zxing.utils.QRCodeFactory;
import com.sjl.bookmark.BuildConfig;
import com.sjl.bookmark.R;
import com.sjl.bookmark.app.AppConstant;
import com.sjl.bookmark.kotlin.language.I18nUtils;
import com.sjl.core.mvp.BaseActivity;
import com.sjl.core.util.SnackbarUtils;
import com.sjl.core.util.ViewUtils;
import com.sjl.core.util.log.LogUtils;

import java.io.File;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import butterknife.BindView;

/**
 * 我的名片
 *
 * @author Kelly
 * @version 1.0.0
 * @filename MyCardActivity.java
 * @time 2018/2/24 9:51
 * @copyright(C) 2018 song
 */
public class MyCardActivity extends BaseActivity implements View.OnClickListener{
    @BindView(R.id.common_toolbar)
    Toolbar mToolBar;
    @BindView(R.id.iv_qr_create)
    ImageView imageView;
    @BindView(R.id.btn_save_card)
    Button mSaveCard;
    @BindView(R.id.btn_share_card)
    Button mShareCard;
    @BindView(R.id.cv_content)
    CardView mMyCard;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_qr_create;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {
        bindingToolbar(mToolBar, I18nUtils.getString(R.string.my_card));
        mSaveCard.setOnClickListener(this);
        mShareCard.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        try {

            int widthAndHeight = ViewUtils.dp2px(this, 250);
            StringBuilder sb = new StringBuilder();//内容越多，像素点越多
            sb.append("qq:1057291963");
            sb.append("\n");
            sb.append("Email:kelly168163@163.com");

            Bitmap qrCode = QRCodeFactory.createQRCode(sb.toString(), widthAndHeight);
            imageView.setImageBitmap(qrCode);
        } catch (Exception e) {
            LogUtils.e("生成二维码图片（不带图片）异常", e);
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

    @Override
    public void onClick(View v) {
        File file = new File(AppConstant.ROOT_PATH+"myCard.png");
        switch (v.getId()){
            case R.id.btn_save_card://保存二维码
                ViewUtils.saveBitmap(mMyCard,file.getAbsolutePath());
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                //判断是否是AndroidN以及更高的版本
                Uri uri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileProvider", file);
                } else {//android 7.0以下
                    uri = Uri.fromFile(file);
                }
                intent.setData(uri);
                sendBroadcast(intent);//发送广播，可以在相册中显示出来
                SnackbarUtils.makeShort(mMyCard,getString(R.string.img_save_success)).show();
                break;
            case R.id.btn_share_card://分享二维码
                if (!file.exists()){
                    ViewUtils.saveBitmap(mMyCard,file.getAbsolutePath());
                }
                Intent share_intent = new Intent();
                //判断是否是AndroidN以及更高的版本
                Uri shareUri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    share_intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    shareUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileProvider", file);
                } else {//android 7.0以下
                    shareUri = Uri.fromFile(file);
                }
                share_intent.setAction(Intent.ACTION_SEND);//设置分享行为
                share_intent.setType("image/*");  //设置分享内容的类型
                share_intent.putExtra(Intent.EXTRA_STREAM, shareUri);
                share_intent = Intent.createChooser(share_intent, getString(R.string.share_title));
                startActivity(share_intent);
                break;
            default:
                break;
        }
    }
}
