package io.github.hzhilong.bilibili.backup.api.request;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InterruptedIOException;

/**
 * 节流拦截器
 *
 * @author hzhilong
 * @version 1.0
 */
public class ThrottlingInterceptor implements Interceptor {

    private final long delayMillis;

    public ThrottlingInterceptor(long delayMillis) {
        this.delayMillis = delayMillis;
    }

    @NotNull
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
