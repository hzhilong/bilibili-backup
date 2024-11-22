package top.ybgnb.bilibili.backup.app.business;

import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.biliapi.bean.Upper;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.biliapi.request.ThrottlingInterceptor;

import java.util.Scanner;

/**
 * @ClassName BaseBusiness
 * @Description 业务基类
 * @Author hzhilong
 * @Time 2024/11/22
 * @Version 1.0
 */
public abstract class BaseBusiness {

    protected OkHttpClient client;

    public BaseBusiness() {
        this.client = new OkHttpClient.Builder().addInterceptor(
                new ThrottlingInterceptor(1000)).build();
    }

    /**
     * 处理业务
     *
     * @return 当前登录的UP信息
     * @throws BusinessException 业务异常
     */
    public abstract Upper process(Scanner scanner) throws BusinessException;

}
