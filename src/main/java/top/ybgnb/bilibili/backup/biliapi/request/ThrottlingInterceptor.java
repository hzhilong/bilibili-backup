package top.ybgnb.bilibili.backup.biliapi.request;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.io.InterruptedIOException;

/**
 * @ClassName ThrottlingInterceptor
 * @Description
 * @Author hzhilong
 * @Time 2024/9/27
 * @Version 1.0
 */
public class ThrottlingInterceptor implements Interceptor {

    private final long delayMillis;

    public ThrottlingInterceptor(long delayMillis) {
        this.delayMillis = delayMillis;
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();

        // 在发送请求前添加延时
        try {
            Thread.sleep(delayMillis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InterruptedIOException();
        }

        return chain.proceed(request);
    }
}
