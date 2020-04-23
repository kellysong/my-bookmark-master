package com.sjl.bookmark.ui.activity;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bm.library.Info;
import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sjl.bookmark.BuildConfig;
import com.sjl.bookmark.R;
import com.sjl.bookmark.app.AppConstant;
import com.sjl.bookmark.entity.UserInfo;
import com.sjl.bookmark.ui.base.extend.BaseSwipeBackActivity;
import com.sjl.core.entity.EventBusDto;
import com.sjl.core.net.GlideCircleTransform;
import com.sjl.core.net.RxBus;
import com.sjl.core.util.log.LogUtils;
import com.sjl.core.util.SerializeUtils;
import com.sjl.core.util.UriUtils;
import com.sjl.core.util.ViewUtils;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 个人中心
 *
 * @author Kelly
 * @version 1.0.0
 * @filename PersonCenterActivity.java
 * @time 2018/11/29 9:16
 * @copyright(C) 2018 song
 */
public class PersonCenterActivity extends BaseSwipeBackActivity {
    private static final int REQUEST_PERSONALITY = 300;

    @BindView(R.id.common_toolbar)
    Toolbar mToolBar;
    @BindView(R.id.rl_avatar)
    RelativeLayout rlAvatar;
    @BindView(R.id.rl_nickname)
    RelativeLayout rlNickname;
    @BindView(R.id.rl_sex)
    RelativeLayout rlSex;
    @BindView(R.id.rl_phone)
    RelativeLayout rlPhone;
    @BindView(R.id.rl_personality)
    RelativeLayout rlPersonality;

    @BindView(R.id.iv_avatar)
    ImageView ivAvatar;
    @BindView(R.id.zoom_photo_view)
    PhotoView zoomView;

    @BindView(R.id.tv_nickname)
    TextView tvNickname;
    @BindView(R.id.tv_sex)
    TextView tvSex;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.tv_personality)
    TextView tvPersonality;
    @BindView(R.id.fab_save)
    FloatingActionButton fabSave;

    private boolean changeAvatar;
    private String oldName;
    private String oldSex;
    private String oldPersonality;
    private Bitmap head = null;
    private String headPath;

    private UserInfo userInfo;
    private Info zoomViewInfo;

    @Override
    protected int getLayoutId() {
        return R.layout.person_activity;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {
        bindingToolbar(mToolBar, "个人中心");
    }

    @Override
    protected void initData() {
        userInfo = SerializeUtils.deserialize("userInfo", UserInfo.class);
        if (userInfo != null) {
            oldName = userInfo.getName();
            oldSex = userInfo.getSex();
            oldPersonality = userInfo.getPersonality();
            if (!TextUtils.isEmpty(userInfo.getAvatar())) {
                LogUtils.i("avatar:" + userInfo.getAvatar());
                Glide.with(this)
                        .load(userInfo.getAvatar())
                        .placeholder(R.mipmap.default_avatar)
                        .transform(new GlideCircleTransform(this))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)//磁盘缓存
                        .skipMemoryCache(true)//跳过内存缓存，否则会显示上次内存中的图片
                        .into(ivAvatar);


                zoomView.enable();// 需要启动缩放需要手动开启

            }
            oldPersonality = userInfo.getPersonality();
            tvNickname.setText(oldName);
            tvSex.setText(oldSex);
            tvPhone.setText("13537772914");//以后注册显示
            if (!TextUtils.isEmpty(oldPersonality)) {
                tvPersonality.setText(oldPersonality);
            } else {
                oldPersonality = getResources().getString(R.string.personality);
            }
        }

    }

    @OnClick({R.id.rl_avatar, R.id.rl_nickname, R.id.rl_sex, R.id.rl_personality, R.id.fab_save, R.id.iv_avatar, R.id.zoom_photo_view})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_avatar:
                //选择图片并裁剪
                showTypeDialog();
                break;
            case R.id.iv_avatar://头像图片
                ivAvatar.setVisibility(View.GONE);
                zoomView.setVisibility(View.VISIBLE);
                //获取img的信息
                zoomView.setImageDrawable(ivAvatar.getDrawable());
//                        Info info = PhotoView.getImageViewInfo(ImageView);//有bug

                zoomViewInfo = zoomView.getInfo();
                //zoomPhotoView 从ivAvatar变换到当前位置
                zoomView.animaFrom(zoomViewInfo);
                break;
            case R.id.zoom_photo_view://缩放图片
                //从当前位置变回到img的位置
                zoomView.animaTo(zoomViewInfo, new Runnable() {
                    @Override
                    public void run() {
                        zoomView.setVisibility(View.GONE);
                        ivAvatar.setVisibility(View.VISIBLE);
                    }
                });

                break;
            case R.id.rl_nickname:
                //修改昵称
                View inflate = View.inflate(this, R.layout.dialog_nickname, null);
                final EditText etNickname = (EditText) inflate.findViewById(R.id.et_nickname);
                etNickname.setText(tvNickname.getText());
                etNickname.selectAll();
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setView(inflate)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tvNickname.setText(etNickname.getText());
                                if (!etNickname.getText().toString().equals(oldName)) {
                                    tvNickname.setTextColor(getResources().getColor(R.color.colorPrimary));
                                } else {
                                    tvNickname.setTextColor(getResources().getColor(R.color.gray));
                                }
                                showSaveButton();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create();
                final Window window = dialog.getWindow();
                final WindowManager.LayoutParams params = window.getAttributes();
                params.gravity = Gravity.TOP;
                params.y = ViewUtils.getScreenHeight(this) / 4;
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                dialog.show();
                //修复dialog自适应软键盘的问题
                break;
            case R.id.rl_sex:
                //修改性别
                final String[] sexArr = new String[]{"男", "女"};
                String sex = tvSex.getText().toString();
                int index = 0;
                for (int i = 0; i < sexArr.length; i++) {
                    if (sexArr[i].equals(sex)) {
                        index = i;
                        break;
                    }
                }
                new AlertDialog.Builder(this)
                        .setSingleChoiceItems(sexArr, index, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tvSex.setText(sexArr[which]);
                                if (!sexArr[which].equals(oldSex)) {
                                    tvSex.setTextColor(getResources().getColor(R.color.colorPrimary));
                                } else {
                                    tvSex.setTextColor(getResources().getColor(R.color.gray));
                                }
                                showSaveButton();
                                dialog.dismiss();
                            }
                        }).setNegativeButton("取消", null).show();
                break;
            case R.id.rl_personality:
                //修改个性签名
                Intent intent = new Intent(this, PersonalityActivity.class);
                intent.putExtra("personality", tvPersonality.getText().toString());
                startActivityForResult(intent, REQUEST_PERSONALITY);
                break;
            case R.id.fab_save:
                //提交修改
                new AlertDialog.Builder(this)
                        .setMessage("确定保存用户信息?")
                        .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String nickname = tvNickname.getText().toString();
                                String sex = tvSex.getText().toString();
                                String personality = tvPersonality.getText().toString();
                                updateUser(changeAvatar, nickname, sex, personality);
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;
        }
    }

    /**
     * 更新用户信息
     *
     * @param changeAvatar
     * @param nickname
     * @param sex
     * @param personality
     */
    private void updateUser(boolean changeAvatar, String nickname, String sex, String personality) {
        UserInfo temp = new UserInfo();
        if (changeAvatar) {
            temp.setAvatar(headPath);
        } else {
            if (userInfo != null) {
                temp.setAvatar(userInfo.getAvatar());
            }
        }
        temp.setName(nickname);
        temp.setSex(sex);
        temp.setPersonality(personality);
        SerializeUtils.serialize("userInfo", temp);
        EventBusDto<String> eventBusDto = new EventBusDto<String>(0, "更新首页头像：" + headPath);
        RxBus.getInstance().post(AppConstant.RxBusFlag.FLAG_2, eventBusDto);//更新侧滑菜单头像
        hideSaveButton();
        tvNickname.setTextColor(getResources().getColor(R.color.gray));
        tvSex.setTextColor(getResources().getColor(R.color.gray));
        tvPersonality.setTextColor(getResources().getColor(R.color.gray));
        this.changeAvatar = false;
        if (userInfo == null) {//第一次
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show();
        }

    }

    private void showSaveButton() {
        //弹出保存按钮
        if (fabSave.getVisibility() == View.GONE) {
            String personality = tvPersonality.getText().toString();
            if (userInfo == null) {
                fabSave.setVisibility(View.VISIBLE);
                ObjectAnimator.ofFloat(fabSave, "translationY", -150).start();
                return;
            }
            if (!tvNickname.getText().toString().equals(userInfo.getName()) || !tvSex.getText().toString().equals(userInfo.getSex())
                    || (!personality.equals(userInfo.getPersonality()) && !personality.equals(this.getResources().getString(R.string.personality))) || changeAvatar) {
                fabSave.setVisibility(View.VISIBLE);
                ObjectAnimator.ofFloat(fabSave, "translationY", -150).start();
            } else {
                hideSaveButton();
            }
        }
    }

    private void hideSaveButton() {
        if (fabSave.getVisibility() == View.VISIBLE) {
            ObjectAnimator.ofFloat(fabSave, "translationY", 200).start();
            fabSave.postDelayed(new Runnable() {
                @Override
                public void run() {
                    fabSave.setVisibility(View.GONE);
                }
            }, 1000);
        }
    }

    private void showTypeDialog() {
        final String[] items = new String[]{"拍照", "从手机相册选择"};
        // 创建对话框构建器
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 设置参数
        builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0://调用照相机
                        try {
                            Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            File dir = new File(AppConstant.USER_HEAD_PATH);
                            if (!dir.exists()) {
                                dir.mkdirs();
                            }
                            File file = new File(dir, "head.jpg");//指定拍照输出路径
                            //判断是否是AndroidN以及更高的版本
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                intent1.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                Uri contentUri = FileProvider.getUriForFile(PersonCenterActivity.this, BuildConfig.APPLICATION_ID + ".fileProvider", file);
                                intent1.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                            } else {//android 7.0以下
                                intent1.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                            }
                            startActivityForResult(intent1, 100);
                        } catch (Exception e) {
                            LogUtils.e("打开相机异常", e);
                        }
                        dialog.dismiss();
                        break;
                    case 1:// 在相册中选取
                        Intent intent2 = new Intent(Intent.ACTION_PICK, null);
                        //打开文件
                        intent2.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(intent2, 200);
                        dialog.dismiss();
                        break;
                    default:
                        break;
                }
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        LogUtils.i("requestCode=" + requestCode + ",resultCode=" + resultCode);
        switch (requestCode) {
            case 100:
                if (resultCode == RESULT_OK) {
                    File temp = new File(AppConstant.USER_HEAD_PATH + "/head.jpg");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Uri contentUri = FileProvider.getUriForFile(PersonCenterActivity.this, BuildConfig.APPLICATION_ID + ".fileProvider", temp);
                        //content://com.sjl.bookmark.fileProvider/head_external_files/head.jpg
                        cropPhotoNew(contentUri);// 裁剪图片
                    } else {//android 7.0以下
                        cropPhotoNew(Uri.fromFile(temp));// 裁剪图片
                    }
                }
                break;
            case 200:
                if (resultCode == RESULT_OK) {
                    cropPhotoNew(data.getData());// 裁剪图片
                }
                break;
            case UCrop.REQUEST_CROP:
                if (resultCode == RESULT_OK) {
                    final Uri resultUri = UCrop.getOutput(data);
                    LogUtils.i("裁剪结果:" + resultUri.getPath());
                    /**
                     * 开发中遇到的问题，使用glide加载网络图片，每次更换头像后返回页面要同步显示已改过的头像。

                     我们服务端是每次上传的个人头像只是替换原图，路径并不变。

                     这就导致glide加载时会使用缓存的图片，导致页面图片显示不同步。
                     */
                    Glide.with(this)
                            .load(resultUri)
                            .transform(new GlideCircleTransform(this))
                            .diskCacheStrategy(DiskCacheStrategy.NONE)//磁盘缓存
                            .skipMemoryCache(true)//跳过内存缓存，否则会显示上次内存中的图片
                            .into(ivAvatar);
                    changeAvatar = true;
                    headPath = UriUtils.fileUriToPath(this, resultUri);

//                    try {
//                        InputStream input = this.getContentResolver().openInputStream(resultUri);
//                        head = BitmapFactory.decodeStream(input);
//                        ivAvatar.setImageBitmap(head);// 用ImageView显示出来
//                        input.close();
//                    } catch (FileNotFoundException e) {
//                        LogUtils.w(e);
//                    } catch (IOException e) {
//                        LogUtils.w(e);
//                    }
                } else if (resultCode == UCrop.RESULT_ERROR) {
                    final Throwable cropError = UCrop.getError(data);
                    LogUtils.e("裁切图片失败", cropError);
                }
                break;
            case REQUEST_PERSONALITY:
                if (resultCode != RESULT_OK) {
                    return;
                }
                String personality = data.getStringExtra("personality");
                tvPersonality.setText(personality);
                if (!personality.equals(oldPersonality)) {
                    tvPersonality.setTextColor(getResources().getColor(R.color.colorPrimary));
                } else {
                    tvPersonality.setTextColor(getResources().getColor(R.color.gray));
                }
                break;
            default:
                break;
        }
        if (resultCode == RESULT_OK) {
            showSaveButton();
        }

    }

    /**
     * @param sourceUri 原uri
     */
    private void cropPhotoNew(Uri sourceUri) {
        File dir = new File(AppConstant.USER_HEAD_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, "head_crop.jpg");
        Uri destinationUri = Uri.fromFile(file);// 裁剪图片
        //裁剪后保存到文件中,sourceUri和destinationUri的文件路径不能相同
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(80);
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        // 隐藏底部工具
        options.setHideBottomControls(true);
        //设置裁剪图片可操作的手势
//        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL);
        options.setToolbarTitle("裁剪");//设置标题栏文字
        options.setMaxScaleMultiplier(3);//设置最大缩放比例300%
        options.setHideBottomControls(false);//隐藏下边控制栏
        options.setShowCropGrid(false);  //设置是否显示裁剪网格


        UCrop.of(sourceUri, destinationUri).withAspectRatio(1, 1).withOptions(options).withMaxResultSize(640, 640).start(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (head != null && !head.isRecycled()) {
            head.recycle();
            head = null;
        }

    }


    @Override
    public void onBackPressed() {
        if (zoomView.getVisibility() == View.VISIBLE) {
            if (zoomViewInfo != null) {
                zoomView.animaTo(zoomViewInfo, new Runnable() {
                    @Override
                    public void run() {
                        zoomView.setVisibility(View.GONE);
                        ivAvatar.setVisibility(View.VISIBLE);
                    }
                });
            }
        } else {
            super.onBackPressed();
        }
    }


}
