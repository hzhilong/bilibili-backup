package io.github.hzhilong.bilibili.backup.gui.worker;

import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.base.utils.StringUtils;
import io.github.hzhilong.bilibili.backup.api.bean.QRCode;
import io.github.hzhilong.bilibili.backup.api.bean.Upper;
import io.github.hzhilong.bilibili.backup.api.bean.UpperInfoCallback;
import io.github.hzhilong.bilibili.backup.app.service.impl.LoginService;
import io.github.hzhilong.bilibili.backup.api.user.User;
import io.github.hzhilong.bilibili.backup.app.bean.SavedUser;
import io.github.hzhilong.bilibili.backup.gui.dialog.LoginUserDialog;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import javax.swing.*;

/**
 * 二维码登录的线程
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class LoginRunnable extends BaseRunnable {

    private final LoginUserDialog dialog;
    private final JLabel lblTipMsg;
    private final QRCode qrCode;
    private LoginService loginService;
    private String cookie;
    private Upper upper;

    public LoginRunnable(OkHttpClient client, LoginUserDialog dialog, JLabel lblTipMsg, QRCode qrCode) {
        super(client);
        this.dialog = dialog;
        this.lblTipMsg = lblTipMsg;
        this.qrCode = qrCode;
    }

    @Override
    public void run() {
        loginService = new LoginService(client);
        String cookie = null;
        try {
            // 等待扫码
            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < 180000 && StringUtils.isEmpty(cookie)) {
                if (isInterrupt()) {
                    return;
                }
                cookie = loginService.login(qrCode);
                Thread.sleep(2000);
            }
            if (StringUtils.isEmpty(cookie)) {
                updateTipMsg("扫码超时！");
            } else {
                this.cookie = cookie;
                updateTipMsg("扫码成功！");
                Thread.sleep(888);
                updateTipMsg("正在获取账号信息...");
                getUserInfo();
            }
        } catch (BusinessException e) {
            updateTipMsg(e.getMessage());
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            updateTipMsg("内部错误");
        }
    }

    private void getUserInfo() throws BusinessException {
        loginService.getUpper(new User(cookie), new UpperInfoCallback() {
            @Override
            public void success(Upper upper) {
                LoginRunnable.this.upper = upper;
                loginComplete();
            }

            @Override
            public void fail(User user) {

            }
        });
    }

    private void loginComplete() {
        SwingUtilities.invokeLater(() -> {
            dialog.setSavedUser(new SavedUser(upper, cookie));
            dialog.setVisible(false);
            dialog.dispose();
        });
    }

    private void updateTipMsg(String msg) {
        SwingUtilities.invokeLater(() -> lblTipMsg.setText(msg));
    }
}
