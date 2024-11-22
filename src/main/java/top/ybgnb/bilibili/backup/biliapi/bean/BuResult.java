package top.ybgnb.bilibili.backup.biliapi.bean;

/**
 * 处理结果 只是系统内使用，并不直接返回给前端
 *
 * @author hzhilong
 * @version 1.0
 * @date 2022-04-10
 */
public class BuResult<T> {

    private boolean isSuccess = false;
    private String code;
    private String msg;
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
        return new BuResult<T>(false, msg);
    }

    public static <T> BuResult<T> newFailedData(String msg, String code, T data) {
        return new BuResult<T>(false, msg, code, data);
    }

    public void setSuccessAndData(T data) {
        this.isSuccess = true;
        this.data = data;
    }

    public void setFailed(String msg) {
        this.msg = msg;
        this.isSuccess = false;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public boolean isFail() {
        return !isSuccess;
    }

    public void setSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
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

    public void setSuccess(String msg) {
        this.isSuccess = true;
        this.msg = msg;
    }
}
