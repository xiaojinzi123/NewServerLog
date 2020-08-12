package com.xiaojinzi.bean;

public class ResultVORes<T> {

    public static final int CODE_SUCCESS = 0;

    /**
     * 非 0 都是错误的
     */
    private int errorCode;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 数据
     */
    private T data;

    public ResultVORes(int errorCode, String errorMessage, T data) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.data = data;
    }

    public static <T> ResultVORes<T> success(T t) {
        return new ResultVORes(CODE_SUCCESS, null, t);
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
