package com.sjl.bookmark.util;

import android.content.Context;
import android.text.TextUtils;

import com.sjl.bookmark.app.AppConstant;
import com.sjl.core.util.PreferencesHelper;
import com.zqc.opencc.android.lib.ChineseConverter;
import com.zqc.opencc.android.lib.ConversionType;


/**
 * 对文字操作的工具类
 */

public class WordUtils {


    /**
     * 将文本中的半角字符，转换成全角字符
     *
     * @param input
     * @return
     */
    public static String halfToFull(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 32) //半角空格
            {
                c[i] = (char) 12288;
                continue;
            }
            //根据实际情况，过滤不需要转换的符号
            //if (c[i] == 46) //半角点号，不转换
            // continue;

            if (c[i] > 32 && c[i] < 127)    //其他符号都转换为全角
                c[i] = (char) (c[i] + 65248);
        }
        return new String(c);
    }

    /**
     * 字符串全角转换为半角
     * @param input
     * @return
     */
    public static String fullToHalf(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) //全角空格
            {
                c[i] = (char) 32;
                continue;
            }

            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    /**
     * 繁簡轉換
     * @param input
     * @param context
     * @return
     */
    public static String convertCC(String input, Context context) {
        if (TextUtils.isEmpty(input))
            return "";

        ConversionType currentConversionType = ConversionType.S2TWP;
        int convertType = PreferencesHelper.getInstance(context).getInteger(AppConstant.SETTING.SHARED_READ_CONVERT_TYPE, 0);


        switch (convertType) {
            case 1:
                currentConversionType = ConversionType.TW2SP;
                break;
            case 2:
                currentConversionType = ConversionType.S2HK;
                break;
            case 3:
                currentConversionType = ConversionType.S2T;
                break;
            case 4:
                currentConversionType = ConversionType.S2TW;
                break;
            case 5:
                currentConversionType = ConversionType.S2TWP;
                break;
            case 6:
                currentConversionType = ConversionType.T2HK;
                break;
            case 7:
                currentConversionType = ConversionType.T2S;
                break;
            case 8:
                currentConversionType = ConversionType.T2TW;
                break;
            case 9:
                currentConversionType = ConversionType.TW2S;
                break;
            case 10:
                currentConversionType = ConversionType.HK2S;
                break;
        }

        return (convertType != 0) ? ChineseConverter.convert(input, currentConversionType, context) : input;
    }
}
