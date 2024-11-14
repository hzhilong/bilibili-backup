package top.ybgnb.bilibili.backup.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.bean.QRCode;
import top.ybgnb.bilibili.backup.bean.Upper;
import top.ybgnb.bilibili.backup.constant.BuType;
import top.ybgnb.bilibili.backup.error.BusinessException;
import top.ybgnb.bilibili.backup.request.ThrottlingInterceptor;
import top.ybgnb.bilibili.backup.service.LoginService;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import static top.ybgnb.bilibili.backup.utils.CommonUtil.*;

/**
 * @ClassName CookieUtil
 * @Description
 * @Author hzhilong
 * @Time 2024/9/29
 * @Version 1.0
 */
@Slf4j
public class CookieUtil {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Cookie {
        BuType type;
        Upper user;
        String cookie;
    }

    public static void getCookie() throws BusinessException {
        try {
            Scanner sc = scannerThreadLocal.get();
            CookieUtil.Cookie cookie = CookieUtil.read(buTypeThreadLocal.get());
            log.info("是否使用上次登录的用户：{}", cookie.getUser().getName());
            log.info("输入Y：使用该用户；  输入其他：不使用");
            String nextLine = sc.nextLine();
            if ("Y".equals(nextLine) || "y".equals(nextLine)) {
                userCookieThreadLocal.set(cookie.getCookie());
            }
        } catch (BusinessException e) {
            log.info("未登录，需扫码登录");
            loginNewUser();
        }
    }

    private static void loginNewUser() throws BusinessException {
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(
                new ThrottlingInterceptor(1000)).build();
        LoginService loginService = new LoginService(client);
        log.info("正在获取登录二维码...");
//        log.info("正在获取登录二维码（长时间不显示的话，可按一下方向键下刷新屏幕）...");
//        log.info("可尝试按住【Ctrl+鼠标滚轮键】调整字体为较小状态（方便展示二维码）");
        QRCode qrCode = loginService.generateQRCode();
        log.info("请使用手机哔哩哔哩扫码登录：");
//        QRUtil.printQR(qrCode.getUrl());
//        log.info("请使用手机哔哩哔哩扫码登录：");
        File dir = new File("qr");
        if (!dir.exists()) {
            dir.mkdir();
        }
        String filePath = "qr/" + System.currentTimeMillis() + ".png";
        File file = QRUtil.writeQRFile(qrCode.getUrl(), filePath);
        try {
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec("cmd /c \"" + file.getAbsolutePath() + "\"");
            } catch (IOException e) {
                throw new BusinessException("打开二维码失败");
            }
            long startTime = System.currentTimeMillis();
            String cookie = null;
            while (System.currentTimeMillis() - startTime < 180000 && StringUtils.isEmpty(cookie)) {
                cookie = loginService.login(qrCode);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
            }
            if (StringUtils.isEmpty(cookie)) {
                throw new BusinessException("扫码超时\n");
            }
            log.info("登录成功");
            userCookieThreadLocal.set(cookie);
        } finally {
            file.deleteOnExit();
        }
    }

    public static void save(Cookie cookie) throws BusinessException {
        FileUtil.writeJsonFile("bin/cookies/", cookie.getType().toString(), cookie);
    }

    public static Cookie read(BuType type) throws BusinessException {
        return JSONObject.parseObject(FileUtil.readJsonFile("bin/cookies/", type.toString()), Cookie.class);
    }

    public static void delete(BuType type) {
        new File("bin/cookies/" + type.toString()).delete();
    }
}
