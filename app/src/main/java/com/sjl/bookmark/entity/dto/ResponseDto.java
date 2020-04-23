package com.sjl.bookmark.entity.dto;

import com.google.gson.Gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 响应dto
 *
 * @author song
 */
public class ResponseDto<T>  {
    private int resultCode;// 结果码
    private String resultMsg;// 结果描述信息
    private T resultObject; // 结果对象


    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public T getResultObject() {
        return resultObject;
    }

    public void setResultObject(T resultObject) {
        this.resultObject = resultObject;
    }

    /**
     * 使用GSON和泛型解析约定格式的JSON串
     */


    public static ResponseDto fromJson(String json, Class clazz) {
        Gson gson = new Gson();
        Type objectType = type(ResponseDto.class, clazz);
        return gson.fromJson(json, objectType);
    }

    public String toJson(Class<T> clazz) {
        Gson gson = new Gson();
        Type objectType = type(ResponseDto.class, clazz);
        return gson.toJson(this, objectType);
    }

    /**
     * @param raw
     * @param args
     * @return
     */
    static ParameterizedType type(final Class raw, final Type... args) {
        return new ParameterizedType() {

            @Override
            public Type getRawType() {
                return raw;
            }

            @Override
            public Type[] getActualTypeArguments() {
                return args;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };
    }

    @Override
    public String toString() {
        return "ResponseDto{" +
                "resultCode=" + resultCode +
                ", resultMsg='" + resultMsg + '\'' +
                ", resultObject=" + resultObject +
                '}';
    }
}
