package com.sjl.bookmark.entity;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename DataResponse.java
 * @time 2018/3/21 16:05
 * @copyright(C) 2018 song
 */
public class DataResponse<T> {
    private int errorCode;
    private Object errorMsg;
    private T data;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public Object getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(Object errorMsg) {
        this.errorMsg = errorMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
