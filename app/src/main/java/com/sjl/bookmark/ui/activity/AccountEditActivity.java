package com.sjl.bookmark.ui.activity;

import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.ArrayMap;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.sjl.bookmark.R;
import com.sjl.bookmark.app.AppConstant;
import com.sjl.bookmark.entity.table.Account;
import com.sjl.bookmark.kotlin.language.I18nUtils;
import com.sjl.bookmark.ui.contract.AccountEditContract;
import com.sjl.bookmark.ui.fragment.AccountListFragment;
import com.sjl.bookmark.ui.presenter.AccountEditPresenter;
import com.sjl.core.mvp.BaseActivity;
import com.sjl.core.util.ValidatorUtils;
import com.sjl.core.util.ViewUtils;
import com.sjl.core.util.datetime.TimeUtils;
import com.sjl.core.util.log.LogUtils;
import com.sjl.core.util.security.DESUtils;

import org.angmarch.views.NiceSpinner;

import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;


/**
 * 账号保存、修改和删除Activity
 *
 * @author Kelly
 * @version 1.0.0
 * @filename AccountEditActivity.java
 * @time 2018/3/7 10:42
 * @copyright(C) 2018 song
 */
public class AccountEditActivity extends BaseActivity<AccountEditPresenter> implements AccountEditContract.View,TextWatcher,View.OnFocusChangeListener,View.OnClickListener {
    @BindView(R.id.common_toolbar)
    Toolbar mToolBar;
    @BindView(R.id.sv_content)
    ScrollView mScrollView;

    @BindView(R.id.met_title)
    MaterialEditText mTitle;
    @BindView(R.id.met_userName)
    MaterialEditText mUserName;
    @BindView(R.id.met_password)
    MaterialEditText mPassword;
    @BindView(R.id.cb_eye)
    CheckBox mEye;
    @BindView(R.id.met_userEmail)
    MaterialEditText mUserEmail;
    @BindView(R.id.met_userPhone)
    MaterialEditText mUserPhone;
    @BindView(R.id.met_remark)
    MaterialEditText mRemark;
    @BindView(R.id.np_type)
    NiceSpinner mAccountType;//账号类别
    @BindView(R.id.np_state)
    NiceSpinner mAccountState;//账号状态

    @BindView(R.id.timeTextView)
    TextView mTimeTextView;
    @BindView(R.id.btn_delete)
    Button mDelete;
    private MenuItem menuItem;
    private int mAccountTypePosition;
    private int mAccountStatePosition;//账号状态索引


    @Override
    protected int getLayoutId() {
        return R.layout.account_edit_activity;
    }

    @Override
    protected void initView() {

    }


    @Override
    protected void initListener() {
        bindingToolbar(mToolBar, I18nUtils.getString(R.string.text_add));
        //修复ScrollView中EditText导致自动滚动问题
        mScrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        mScrollView.setFocusable(true);
        mScrollView.setFocusableInTouchMode(true);
        mScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.requestFocusFromTouch();
                return false;
            }
        });

        mAccountType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mAccountTypePosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mAccountState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mAccountStatePosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mEye.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mPassword.setTransformationMethod(PasswordTransformationMethod
                            .getInstance());
                } else {
                    mPassword.setTransformationMethod(HideReturnsTransformationMethod
                            .getInstance());
                }
                mPassword.setSelection(mPassword.getText().toString().length());
            }
        });
    }

    @Override
    protected void initData() {
        mPresenter.init(getIntent());
    }


    /**
     * 每次在display Menu之前，都会去调用，只要按一次Menu按鍵，就会调用一次。所以可以在这里动态的改变menu。
     * @param menu
     * @return
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuItem = menu.getItem(0);

        setItemMenuVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done://新增或修改
                String titleName = mTitle.getText().toString().trim();
                String userName = mUserName.getText().toString().trim();
                String passWord = mPassword.getText().toString().trim();

                String userEmail = mUserEmail.getText().toString().trim();
                String userPhone = mUserPhone.getText().toString().trim();
                String remark = mRemark.getText().toString().trim();
                if (!TextUtils.isEmpty(userEmail) && !ValidatorUtils.isEmail(userEmail)){
                    Toast.makeText(this,"邮箱格式不对",Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (!TextUtils.isEmpty(userPhone) && !ValidatorUtils.isMobile(userPhone)){
                    Toast.makeText(this,"手机号格式不对",Toast.LENGTH_SHORT).show();
                    return false;
                }
                long result = mPresenter.saveAccount(new Account(null, mAccountTypePosition, mAccountStatePosition, titleName, userName, passWord, userEmail, userPhone, remark, new Date()));
                if (result == 0){
                    ViewUtils.hideKeyBoard(this,mTitle);
                    closeActivity(AccountListFragment.EDIT_SUCCESS);//触发fragment里面的onActivityResult
                }else if(result == 1){//触发activity里面的onActivityResult
                    ViewUtils.hideKeyBoard(this,mTitle);
                    closeActivity(AccountIndexActivity.ADD_SUCCESS);
                }else{
                    Toast.makeText(this,"未知错误,result="+result,Toast.LENGTH_SHORT).show();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    @Override
    public void initSpinner(ArrayMap<String, List<String>> data) {
        mAccountType.attachDataSource(data.get("accountType"));
        mAccountState.attachDataSource(data.get("accountState"));
    }

    @Override
    public void initCreateModel(int position) {
        mAccountStatePosition = position;
        LogUtils.i("mAccountStatePosition="+mAccountStatePosition);
        mAccountState.setBackgroundColor(getResources().getColor(R.color.cl_ns_forbid_bg));
        mAccountState.setSelectedIndex(mAccountStatePosition);
        mAccountState.setEnabled(false);
        addTextChangedListener();
    }

    @Override
    public void initViewModel(Account account) {
        if (account == null){
            LogUtils.w("account为空,初始化账号信息失败");
            return;
        }
        mToolBar.setTitle(I18nUtils.getString(R.string.text_detail));
        mTimeTextView.setVisibility(View.VISIBLE);
        mTimeTextView.setText("最后修改日期："+ TimeUtils.formatDateToStr(account.getDate(),TimeUtils.DATE_FORMAT_1));
        ViewUtils.hideKeyBoard(this,mTitle);
        mTitle.setText(DESUtils.decryptBase64DES(AppConstant.DES_ENCRYPTKEY,account.getAccountTitle()));
        mUserName.setText(DESUtils.decryptBase64DES(AppConstant.DES_ENCRYPTKEY,account.getUsername()));
        mPassword.setText(DESUtils.decryptBase64DES(AppConstant.DES_ENCRYPTKEY,account.getPassword()));

        if (!TextUtils.isEmpty(account.getEmail())){
            mUserEmail.setText(DESUtils.decryptBase64DES(AppConstant.DES_ENCRYPTKEY,account.getEmail()));
        }
        if (!TextUtils.isEmpty(account.getPhone())){
            mUserPhone.setText(DESUtils.decryptBase64DES(AppConstant.DES_ENCRYPTKEY,account.getPhone()));
        }


        mAccountType.setSelectedIndex(account.getAccountType());
        mAccountStatePosition = account.getAccountState();
        mAccountState.setSelectedIndex(account.getAccountState());


        mPassword.setTransformationMethod(HideReturnsTransformationMethod
                .getInstance());
        mEye.setChecked(false);
        addFocusChangeListener();
        addTextChangedListener();
        mDelete.setVisibility(View.VISIBLE);
        mDelete.setOnClickListener(this);
    }

    private void addTextChangedListener() {
        mTitle.addTextChangedListener(this);
        mUserName.addTextChangedListener(this);
        mPassword.addTextChangedListener(this);
        mUserEmail.addTextChangedListener(this);
        mUserPhone.addTextChangedListener(this);
        mAccountType.addTextChangedListener(this);
        mAccountState.addTextChangedListener(this);

    }

    private void addFocusChangeListener() {
        mTitle.setOnFocusChangeListener(this);
        mUserName.setOnFocusChangeListener(this);
        mPassword.setOnFocusChangeListener(this);
        mUserEmail.setOnFocusChangeListener(this);
        mUserPhone.setOnFocusChangeListener(this);
        mAccountType.setOnFocusChangeListener(this);
        mAccountState.setOnFocusChangeListener(this);

    }



    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String titleName = mTitle.getText().toString().trim();
        String userName = mUserName.getText().toString().trim();
        String passWord = mPassword.getText().toString().trim();
        if (!TextUtils.isEmpty(passWord) && !TextUtils.isEmpty(titleName) && !TextUtils.isEmpty(userName)) {
            setItemMenuVisible(true);
        } else {
            setItemMenuVisible(false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            mToolBar.setTitle("编辑");
        }
    }

    private void setItemMenuVisible(boolean visible) {
       if (menuItem != null){
           menuItem.setVisible(visible);
       }else{
           LogUtils.i("menuItem为空");
       }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_delete) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("是否删除账号密码?");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mPresenter.deleteAccount();
                    setResult(AccountIndexActivity.ADD_SUCCESS);
                    finish();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
        }
    }

    private void closeActivity(int resultCode) {
        setResult(resultCode);
        finish();
    }
}
