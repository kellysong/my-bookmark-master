package com.sjl.bookmark.entity.table;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 历史快递
 *
 * @author Kelly
 * @version 1.0.0
 * @filename HistoryExpress.java
 * @time 2018/4/26 14:55
 * @copyright(C) 2018 song
 */
@Entity
public class HistoryExpress {
    // 运单号
    @Id
    private String postId;

    // 快递公司请求参数
    @NotNull
    private String companyParam;

    // 快递公司
    @NotNull
    private String companyName;

    // 快递公司Logo
    @NotNull
    private String companyIcon;

    // 签收状态
    @NotNull
    private String checkStatus;//0未签收，1已签收

    // 运单备注
    private String remark;

    private String signTime;

    @Generated(hash = 1994239931)
    public HistoryExpress(String postId, @NotNull String companyParam,
            @NotNull String companyName, @NotNull String companyIcon,
            @NotNull String checkStatus, String remark, String signTime) {
        this.postId = postId;
        this.companyParam = companyParam;
        this.companyName = companyName;
        this.companyIcon = companyIcon;
        this.checkStatus = checkStatus;
        this.remark = remark;
        this.signTime = signTime;
    }

    @Generated(hash = 625492317)
    public HistoryExpress() {
    }

    public String getPostId() {
        return this.postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getCompanyParam() {
        return this.companyParam;
    }

    public void setCompanyParam(String companyParam) {
        this.companyParam = companyParam;
    }

    public String getCompanyName() {
        return this.companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyIcon() {
        return this.companyIcon;
    }

    public void setCompanyIcon(String companyIcon) {
        this.companyIcon = companyIcon;
    }

    public String getCheckStatus() {
        return this.checkStatus;
    }

    public void setCheckStatus(String checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSignTime() {
        return this.signTime;
    }

    public void setSignTime(String signTime) {
        this.signTime = signTime;
    }
}
