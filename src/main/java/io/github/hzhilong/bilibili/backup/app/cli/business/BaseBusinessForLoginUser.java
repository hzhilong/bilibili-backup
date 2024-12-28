package io.github.hzhilong.bilibili.backup.app.cli.business;

import lombok.extern.slf4j.Slf4j;
import io.github.hzhilong.bilibili.backup.app.bean.SavedUser;
import io.github.hzhilong.bilibili.backup.api.bean.UpperInfoCallback;
import io.github.hzhilong.bilibili.backup.app.cli.UserMenu;
import io.github.hzhilong.bilibili.backup.app.state.UserManager;
import io.github.hzhilong.bilibili.backup.api.bean.QRCode;
import io.github.hzhilong.bilibili.backup.api.bean.Upper;
import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.bilibili.backup.app.service.impl.LoginService;
import io.github.hzhilong.bilibili.backup.api.user.User;
import io.github.hzhilong.base.utils.QRUtil;
import io.github.hzhilong.base.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * 登录账号的业务基类
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public abstract class BaseBusinessForLoginUser extends BaseBusiness {

    /**
     * 选择账号
     */
    protected SavedUser chooseUser(Scanner scanner) throws BusinessException {
        // 选择登录过的账号
        SavedUser savedUser = UserMenu.chooseLoggedUser(scanner, true);
        String cookie;
        if (savedUser != null) {
            //  之前登录过
            cookie = savedUser.getCookie();
        } else {
            log.info("请使用扫码方式登录账号\n");
            cookie = loginUserByQR();
        }

        Upper upper = getUpper(new User(cookie),
                // UP信息回调（在已登录账号失效时删除保存的cookie文件）
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
     * 使用二维码登录账号
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
                } catch (InterruptedException ignored) {
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
        return new LoginService(client).getUpper(user, upperInfoCallback);
    }
}
