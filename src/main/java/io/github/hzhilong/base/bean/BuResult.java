package io.github.hzhilong.base.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * 业务处理结果
 *
 * @author hzhilong
 * @version 1.0
 */
public class BuResult<T> {

    private boolean isSuccess;
    @Setter
    @Getter
    private String code;
    @Setter
    @Getter
    private String msg;
    @Setter
    @Getter
    private T data;

    public BuResult(boolean isSuccess) {
        super();
        this.isSuccess = isSuccess;
    }

    public BuResult(boolean isSuccess, String msg) {
        super();
        this.isSuccess = isSuccess;
        this.msg = msg;
    }

    public BuResult(boolean isSuccess, String code, String msg, T data) {
        super();
        this.isSuccess = isSuccess;
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public BuResult() {
        super();
    }

    public static <T> BuResult<T> newSuccess() {
        return new BuResult<>(true);
    }

    public static <T> BuResult<T> newSuccess(T data) {
        BuResult<T>
                buResult = new BuResult<>(true);
        buResult.setData(data);
        return buResult;
    }

    public static <T> BuResult<T> newFailedData(T data) {
        BuResult<T>
                buResult = new BuResult<>(false);
        buResult.setData(data);
        return buResult;
    }

    public static <T> BuResult<T> newFailed(String msg) {
        return new BuResult<>(false, msg);
    }

    public static <T> BuResult<T> newFailedData(String msg, String code, T data) {
        return new BuResult<>(false, msg, code, data);
    }

    public void setSuccessAndData(T data) {
        this.isSuccess = true;
        this.data = data;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public boolean isFail() {
        return !isSuccess;
    }

    public void setFailed(String msg) {
        this.msg = msg;
        this.isSuccess = false;
    }

    public void setSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public void setSuccess(String msg) {
        this.isSuccess = true;
        this.msg = msg;
    }
}
