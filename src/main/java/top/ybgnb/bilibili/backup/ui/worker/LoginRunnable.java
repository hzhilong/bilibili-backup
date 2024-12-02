package top.ybgnb.bilibili.backup.ui.worker;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.app.bean.SavedUser;
import top.ybgnb.bilibili.backup.app.bean.UpperInfoCallback;
import top.ybgnb.bilibili.backup.biliapi.bean.QRCode;
import top.ybgnb.bilibili.backup.biliapi.bean.Upper;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.biliapi.service.impl.LoginService;
import top.ybgnb.bilibili.backup.biliapi.user.User;
import top.ybgnb.bilibili.backup.biliapi.utils.StringUtils;
import top.ybgnb.bilibili.backup.ui.component.LoginUserDialog;

import javax.swing.*;

/**
 * @ClassName LoginRunnable
 * @Description 二维码登录线程
 * @Author hzhilong
 * @Time 2024/11/28
 * @Version 1.0
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
            public void success(Upper upper) throws BusinessException {
                LoginRunnable.this.upper = upper;
                loginComplete();
            }

            @Override
            public void fail(User user) {

            }
        });
    }

    private void loginComplete() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                dialog.setSavedUser(new SavedUser(upper, cookie));
                dialog.setVisible(false);
                dialog.dispose();
            }
        });
    }

    private void updateTipMsg(String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                lblTipMsg.setText(msg);
            }
        });
    }
}
