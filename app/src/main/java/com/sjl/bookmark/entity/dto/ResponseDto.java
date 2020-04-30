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
    private int code;// 结果码
    private String msg;// 结果描述信息
    private T data; // 结果对象

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
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
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
