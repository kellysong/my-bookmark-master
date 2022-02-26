package com.sjl.bookmark.ui.activity

import android.animation.ObjectAnimator
import android.content.*
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES
import android.provider.MediaStore
import android.text.TextUtils
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import butterknife.OnClick
import com.bm.library.Info
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.sjl.bookmark.BuildConfig
import com.sjl.bookmark.R
import com.sjl.bookmark.app.AppConstant
import com.sjl.bookmark.entity.UserInfo
import com.sjl.bookmark.ui.base.extend.BaseSwipeBackActivity
import com.sjl.core.entity.EventBusDto
import com.sjl.core.net.GlideCircleTransform
import com.sjl.core.net.RxBus
import com.sjl.core.util.*
import com.sjl.core.util.log.LogUtils
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.person_activity.*
import kotlinx.android.synthetic.main.toolbar_default.*
import java.io.File

/**
 * 个人中心
 *
 * @author Kelly
 * @version 1.0.0
 * @filename PersonCenterActivity.java
 * @time 2018/11/29 9:16
 * @copyright(C) 2018 song
 */
class PersonCenterActivity : BaseSwipeBackActivity() {

    private var changeAvatar: Boolean = false
    private var oldName: String? = null
    private var oldSex: String? = null
    private var oldPersonality: String? = null
    private var head: Bitmap? = null
    private var headPath: String? = null
    private var userInfo: UserInfo? = null
    private var zoomViewInfo: Info? = null
    override fun getLayoutId(): Int {
        return R.layout.person_activity
    }

    override fun initView() {}
    override fun initListener() {
        bindingToolbar(common_toolbar, getString(R.string.person_center))
    }

    override fun initData() {
        userInfo = SerializeUtils.deserialize<UserInfo>("userInfo", UserInfo::class.java)
        userInfo?.apply {
            oldName = name
            oldSex = sex
            oldPersonality = personality
            if (!TextUtils.isEmpty(avatar)) {
                LogUtils.i("avatar:$avatar")
                Glide.with(this@PersonCenterActivity)
                    .load(avatar)
                    .placeholder(R.mipmap.default_avatar)
                    .transform(GlideCircleTransform())
                    .diskCacheStrategy(DiskCacheStrategy.NONE) //磁盘缓存
                    .skipMemoryCache(true) //跳过内存缓存，否则会显示上次内存中的图片
                    .into(iv_avatar)
                zoom_photo_view.enable() // 需要启动缩放需要手动开启
            }
            tv_nickname.text = oldName
            tv_sex.text = oldSex
            tv_phone.text = "135****2914" //以后注册显示
            if (!TextUtils.isEmpty(oldPersonality)) {
                tv_personality.text = oldPersonality
            } else {
                oldPersonality = resources.getString(R.string.personality)
            }
        }
    }

    @OnClick(
        R.id.rl_avatar,
        R.id.rl_nickname,
        R.id.rl_sex,
        R.id.rl_personality,
        R.id.fab_save,
        R.id.iv_avatar,
        R.id.zoom_photo_view
    )
    fun onClick(view: View) {
        when (view.id) {
            R.id.rl_avatar ->                 //选择图片并裁剪
                showTypeDialog()
            R.id.iv_avatar -> {
                iv_avatar.visibility = View.GONE
                zoom_photo_view.visibility = View.VISIBLE
                //获取img的信息
                zoom_photo_view.setImageDrawable(iv_avatar.drawable)
                //                        Info info = PhotoView.getImageViewInfo(ImageView);//有bug
                zoomViewInfo = zoom_photo_view.info
                //zoomPhotoView 从ivAvatar变换到当前位置
                zoom_photo_view.animaFrom(zoomViewInfo)
            }
            R.id.zoom_photo_view ->                 //从当前位置变回到img的位置
                zoom_photo_view.animaTo(zoomViewInfo, object : Runnable {
                    override fun run() {
                        zoom_photo_view.visibility = View.GONE
                        iv_avatar.visibility = View.VISIBLE
                    }
                })
            R.id.rl_nickname -> {
                //修改昵称
                val inflate: View = View.inflate(this, R.layout.dialog_nickname, null)
                val etNickname: EditText = inflate.findViewById<View>(R.id.et_nickname) as EditText
                etNickname.setText(tv_nickname.text)
                etNickname.selectAll()
                val dialog: AlertDialog = AlertDialog.Builder(this)
                    .setView(inflate)
                    .setPositiveButton(R.string.sure, object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface, which: Int) {
                            tv_nickname.text = etNickname.text
                            if (!(etNickname.text.toString() == oldName)) {
                                tv_nickname.setTextColor(resources.getColor(R.color.colorPrimary))
                            } else {
                                tv_nickname.setTextColor(resources.getColor(R.color.gray))
                            }
                            showSaveButton()
                        }
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .create()
                val window: Window = dialog.window
                val params: WindowManager.LayoutParams = window.attributes
                params.gravity = Gravity.TOP
                params.y = ViewUtils.getScreenHeight(this) / 4
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                dialog.show()
            }
            R.id.rl_sex -> {
                //修改性别
                val sexArr: Array<String> =
                    arrayOf(getString(R.string.man), getString(R.string.woman))
                val sex: String = tv_sex.text.toString()
                var index: Int = 0
                var i: Int = 0
                while (i < sexArr.size) {
                    if ((sexArr.get(i) == sex)) {
                        index = i
                        break
                    }
                    i++
                }
                AlertDialog.Builder(this)
                    .setSingleChoiceItems(sexArr, index, object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface, which: Int) {
                            tv_sex.text = sexArr.get(which)
                            if (!(sexArr.get(which) == oldSex)) {
                                tv_sex.setTextColor(resources.getColor(R.color.colorPrimary))
                            } else {
                                tv_sex.setTextColor(resources.getColor(R.color.gray))
                            }
                            showSaveButton()
                            dialog.dismiss()
                        }
                    }).setNegativeButton(R.string.cancel, null).show()
            }
            R.id.rl_personality -> {
                //修改个性签名
                val intent: Intent = Intent(this, PersonalityActivity::class.java)
                intent.putExtra("personality", tv_personality!!.text.toString())
                startActivityForResult(intent, REQUEST_PERSONALITY)
            }
            R.id.fab_save ->                 //提交修改
                AlertDialog.Builder(this)
                    .setMessage(R.string.save_user_info_hint)
                    .setPositiveButton(R.string.save, object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface, which: Int) {
                            val nickname: String = tv_nickname.text.toString()
                            val sex: String = tv_sex.text.toString()
                            val personality: String = tv_personality!!.text.toString()
                            updateUser(changeAvatar, nickname, sex, personality)
                        }
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show()
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
    private fun updateUser(
        changeAvatar: Boolean,
        nickname: String,
        sex: String,
        personality: String
    ) {
        val temp: UserInfo = UserInfo()
        if (changeAvatar) {
            temp.avatar = headPath
        } else {
            if (userInfo != null) {
                temp.avatar = userInfo!!.avatar
            }
        }
        temp.name = nickname
        temp.sex = sex
        temp.personality = personality
        SerializeUtils.serialize("userInfo", temp)
        val eventBusDto: EventBusDto<String> = EventBusDto(0, "更新首页头像：" + headPath)
        RxBus.getInstance().post(AppConstant.RxBusFlag.FLAG_2, eventBusDto) //更新侧滑菜单头像
        hideSaveButton()
        tv_nickname!!.setTextColor(resources.getColor(R.color.gray))
        tv_sex!!.setTextColor(resources.getColor(R.color.gray))
        tv_personality!!.setTextColor(resources.getColor(R.color.gray))
        this.changeAvatar = false
        if (userInfo == null) { //第一次
            Toast.makeText(this, R.string.save_success, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, R.string.update_success, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showSaveButton() {
        //弹出保存按钮
        if (fab_save.visibility == View.GONE) {
            val personality: String = tv_personality!!.text.toString()
            if (userInfo == null) {
                fab_save.visibility = View.VISIBLE
                ObjectAnimator.ofFloat(fab_save, "translationY", -150f).start()
                return
            }
            if ((!(tv_nickname!!.text
                    .toString() == userInfo!!.name) || !(tv_sex!!.text
                    .toString() == userInfo!!.sex)
                        || (!(personality == userInfo!!.personality) && !(personality == resources.getString(
                    R.string.personality
                ))) || changeAvatar)
            ) {
                fab_save.visibility = View.VISIBLE
                ObjectAnimator.ofFloat(fab_save, "translationY", -150f).start()
            } else {
                hideSaveButton()
            }
        }
    }

    private fun hideSaveButton() {
        if (fab_save.visibility == View.VISIBLE) {
            ObjectAnimator.ofFloat(fab_save, "translationY", 200f).start()
            fab_save!!.postDelayed(object : Runnable {
                override fun run() {
                    fab_save.visibility = View.GONE
                }
            }, 1000)
        }
    }

    private fun showTypeDialog() {
        val items: Array<String> =
            arrayOf(getString(R.string.take_photo), getString(R.string.choose_album))
        // 创建对话框构建器
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        // 设置参数
        builder.setSingleChoiceItems(items, 0, object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {
                when (which) {
                    0 -> {
                        try {
                            val intent1: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            val dir: File = File(AppConstant.USER_HEAD_PATH)
                            if (!dir.exists()) {
                                dir.mkdirs()
                            }
                            val file: File = File(dir, "head.jpg") //指定拍照输出路径
                            //判断是否是AndroidN以及更高的版本
                            if (Build.VERSION.SDK_INT >= VERSION_CODES.N) {
                                intent1.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                                val contentUri: Uri = FileProvider.getUriForFile(
                                    this@PersonCenterActivity,
                                    BuildConfig.APPLICATION_ID + ".fileProvider",
                                    file
                                )
                                intent1.putExtra(MediaStore.EXTRA_OUTPUT, contentUri)
                            } else { //android 7.0以下
                                intent1.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file))
                            }
                            startActivityForResult(intent1, 100)
                        } catch (e: Exception) {
                            LogUtils.e("打开相机异常", e)
                        }
                        dialog.dismiss()
                    }
                    1 -> {
                        val intent2: Intent = Intent(Intent.ACTION_PICK, null)
                        //打开文件
                        intent2.setDataAndType(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            "image/*"
                        )
                        startActivityForResult(intent2, 200)
                        dialog.dismiss()
                    }
                    else -> {}
                }
            }
        })
        builder.create().show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        LogUtils.i("requestCode=$requestCode,resultCode=$resultCode")
        when (requestCode) {
            100 -> if (resultCode == RESULT_OK) {
                val temp: File = File(AppConstant.USER_HEAD_PATH + "/head.jpg")
                if (Build.VERSION.SDK_INT >= VERSION_CODES.N) {
                    val contentUri: Uri = FileProvider.getUriForFile(
                        this@PersonCenterActivity,
                        BuildConfig.APPLICATION_ID + ".fileProvider",
                        temp
                    )
                    //content://com.sjl.bookmark.fileProvider/head_external_files/head.jpg
                    cropPhotoNew(contentUri) // 裁剪图片
                } else { //android 7.0以下
                    cropPhotoNew(Uri.fromFile(temp)) // 裁剪图片
                }
            }
            200 -> if (resultCode == RESULT_OK) {
                cropPhotoNew(data!!.data) // 裁剪图片
            }
            UCrop.REQUEST_CROP -> if (resultCode == RESULT_OK) {
                val resultUri: Uri? = UCrop.getOutput((data)!!)
                LogUtils.i("裁剪结果:" + resultUri!!.path)
                /**
                 * 开发中遇到的问题，使用glide加载网络图片，每次更换头像后返回页面要同步显示已改过的头像。
                 *
                 * 我们服务端是每次上传的个人头像只是替换原图，路径并不变。
                 *
                 * 这就导致glide加载时会使用缓存的图片，导致页面图片显示不同步。
                 */
                Glide.with(this)
                    .load(resultUri)
                    .transform(GlideCircleTransform())
                    .diskCacheStrategy(DiskCacheStrategy.NONE) //磁盘缓存
                    .skipMemoryCache(true) //跳过内存缓存，否则会显示上次内存中的图片
                    .into((iv_avatar)!!)
                changeAvatar = true
                headPath = UriUtils.fileUriToPath(this, resultUri)

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
                val cropError: Throwable? = UCrop.getError((data)!!)
                LogUtils.e("裁切图片失败", cropError)
            }
            REQUEST_PERSONALITY -> {
                if (resultCode != RESULT_OK) {
                    return
                }
                val personality: String = data!!.getStringExtra("personality")
                tv_personality.text = personality
                if (!(personality == oldPersonality)) {
                    tv_personality.setTextColor(resources.getColor(R.color.colorPrimary))
                } else {
                    tv_personality.setTextColor(resources.getColor(R.color.gray))
                }
            }
            else -> {}
        }
        if (resultCode == RESULT_OK) {
            showSaveButton()
        }
    }

    /**
     * @param sourceUri 原uri
     */
    private fun cropPhotoNew(sourceUri: Uri) {
        val dir: File = File(AppConstant.USER_HEAD_PATH)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val file: File = File(dir, "head_crop.jpg")
        val destinationUri: Uri = Uri.fromFile(file) // 裁剪图片
        //裁剪后保存到文件中,sourceUri和destinationUri的文件路径不能相同
        val options: UCrop.Options = UCrop.Options()
        options.setCompressionQuality(80)
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG)
        // 隐藏底部工具
        options.setHideBottomControls(true)
        //设置裁剪图片可操作的手势
//        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL);
        options.setToolbarTitle(getString(R.string.tailor)) //设置标题栏文字
        options.setMaxScaleMultiplier(3f) //设置最大缩放比例300%
        options.setHideBottomControls(false) //隐藏下边控制栏
        options.setShowCropGrid(false) //设置是否显示裁剪网格
        UCrop.of(sourceUri, destinationUri).withAspectRatio(1f, 1f).withOptions(options)
            .withMaxResultSize(640, 640).start(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        head?.apply {
            if (!isRecycled) {
                recycle()
                head = null
            }
        }
    }

    override fun onBackPressed() {
        if (zoom_photo_view.visibility == View.VISIBLE) {
            if (zoomViewInfo != null) {
                zoom_photo_view.animaTo(zoomViewInfo, object : Runnable {
                    override fun run() {
                        zoom_photo_view.visibility = View.GONE
                        iv_avatar.visibility = View.VISIBLE
                    }
                })
            }
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        private val REQUEST_PERSONALITY: Int = 300
    }
}