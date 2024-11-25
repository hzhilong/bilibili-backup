package top.ybgnb.bilibili.backup.app.business;

import lombok.extern.slf4j.Slf4j;
import top.ybgnb.bilibili.backup.app.bean.SavedUser;
import top.ybgnb.bilibili.backup.app.bean.UpperInfoCallback;
import top.ybgnb.bilibili.backup.app.menu.UserMenu;
import top.ybgnb.bilibili.backup.app.state.UserManager;
import top.ybgnb.bilibili.backup.biliapi.bean.ApiResult;
import top.ybgnb.bilibili.backup.biliapi.bean.QRCode;
import top.ybgnb.bilibili.backup.biliapi.bean.Upper;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.biliapi.request.BaseApi;
import top.ybgnb.bilibili.backup.biliapi.service.impl.LoginService;
import top.ybgnb.bilibili.backup.biliapi.user.User;
import top.ybgnb.bilibili.backup.biliapi.utils.QRUtil;
import top.ybgnb.bilibili.backup.biliapi.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * @ClassName BaseBusinessForLoginUser
 * @Description 登录用户的业务基类
 * @Author hzhilong
 * @Time 2024/11/22
 * @Version 1.0
 */
@Slf4j
public abstract class BaseBusinessForLoginUser extends BaseBusiness {

    /**
     * 选择用户
     */
    protected SavedUser chooseUser(Scanner scanner) throws BusinessException {
        // 选择登录过的用户
        SavedUser savedUser = UserMenu.chooseLoggedUser(scanner, true);
        String cookie;
        if (savedUser != null) {
            //  之前登录过
            cookie = savedUser.getCookie();
        } else {
            log.info("请使用扫码方式登录用户\n");
            cookie = loginUserByQR();
        }

        Upper upper = getUpper(new User(cookie),
                // UP信息回调（在已登录用户失效时删除保存的cookie文件）
                new UpperInfoCallback() {
                    @Override
                    public void success(Upper upper) throws BusinessException {
                        UserManager.save(new SavedUser(upper, cookie));
                    }

                    @Override
                    public void fail(User user) {
                        UserManager.delete(user.getUid());
                    }
                });
        return new SavedUser(upper, cookie);
    }

    /**
     * 使用二维码登录用户
     */
    private String loginUserByQR() throws BusinessException {
        LoginService loginService = new LoginService(client);
        log.info("正在获取登录二维码...");
        QRCode qrCode = loginService.generateQRCode();
        log.info("请使用手机哔哩哔哩扫码登录：");
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
                log.info("扫码超时！");
                throw new BusinessException("扫码超时！\n");
            }
            log.info("登录成功");
            return cookie;
        } finally {
            file.deleteOnExit();
        }
    }

    /**
     * 获取UP信息
     */
    public Upper getUpper(User user, UpperInfoCallback upperInfoCallback) throws BusinessException {
        log.info("正在获取用户信息，请稍候...");
        ApiResult<Upper> userInfo = new BaseApi<Upper>(client, new User(user.getCookie()),
                "https://api.bilibili.com/x/space/myinfo", true, Upper.class).apiGet();
        if (userInfo._isFail()) {
            log.error(userInfo.getMessage());
            if (upperInfoCallback != null) {
                upperInfoCallback.fail(user);
            }
            throw new BusinessException("获取当前用户信息失败");
        }
        if (upperInfoCallback != null) {
            upperInfoCallback.success(userInfo.getData());
        }
        return userInfo.getData();
    }
}
