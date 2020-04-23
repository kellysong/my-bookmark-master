package com.sjl.bookmark.util;

import android.text.TextUtils;

import java.util.Set;

/**
 * 权限请求工具类
 *
 * @author Kelly
 * @version 1.0.0
 * @filename PermissionRequestUtils.java
 * @time 2018/5/4 14:15
 * @copyright(C) 2018 song
 */
public class PermissionRequestUtils {

    /**
     * 获取权限名称
     *
     * @param denied
     * @return
     */
    public static String getPermissionName(Set<String> denied) {
        StringBuilder sb = new StringBuilder();
        for (String deniedName : denied) {
            if (deniedName.contains("STORAGE")) {
                sb.append("存储、");
            } else if (deniedName.contains("CAMERA")) {
                sb.append("相机、");
            } else if (deniedName.contains("RECORD_AUDIO")) {
                sb.append("录音、");
            } else if (deniedName.contains("BODY_SENSORS")) {
                sb.append("传感器、");
            } else if (deniedName.contains("LOCATION")) {
                sb.append("定位、");
            } else if (deniedName.contains("CONTACTS")) {
                sb.append("联系人、");
            } else if (deniedName.contains("PHONE")) {
                sb.append("电话、");
            } else if (deniedName.contains("SMS")) {
                sb.append("短信、");
            } else if (deniedName.contains("READ_CALENDAR")) {
                sb.append("日历、");
            }else if (deniedName.contains("INSTALL_PACKAGES")) {
                sb.append("安装应用未知来源、");
            }
        }
        String str = sb.toString();
        if (!TextUtils.isEmpty(str)){
            return sb.deleteCharAt(str.length() - 1).toString();
        }else{
            return "";
        }
    }
}
