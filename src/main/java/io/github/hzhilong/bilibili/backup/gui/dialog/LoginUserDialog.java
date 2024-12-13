package io.github.hzhilong.bilibili.backup.gui.dialog;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import io.github.hzhilong.bilibili.backup.app.bean.SavedUser;
import io.github.hzhilong.bilibili.backup.api.bean.QRCode;
import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.bilibili.backup.gui.utils.LayoutUtil;
import io.github.hzhilong.bilibili.backup.gui.worker.GetLoginQRWorker;
import io.github.hzhilong.bilibili.backup.gui.worker.LoginRunnable;

import javax.swing.*;
import java.awt.*;

/**
 * 登录账号对话框
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class LoginUserDialog extends BaseDialog {

    private JLabel lblQRCode;

    private JLabel lblTipMsg;

    private GetLoginQRWorker getLoginQRWorker;
    private LoginRunnable loginRunnable;

    private final OkHttpClient client;

    @Getter
    @Setter
    private SavedUser savedUser;

    public LoginUserDialog(Window parent, OkHttpClient client) {
        super(parent, "登录新账号");
        this.client = client;
        try {
            initData();
            initUI();
            setModal(true);
        } catch (BusinessException e) {
            throw new RuntimeException(e);
        }
    }

    private void initData() {
    }


    private void initUI() throws BusinessException {
        setSize(500, 400);
        setMinimumSize(new Dimension(500, 400));
        // 对话框居于屏幕中央
        setLocationRelativeTo(null);
        // 点击对话框关闭按钮时，销毁对话框
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridBagLayout());
        // 添加内容
        LayoutUtil.addGridBarY(contentPanel, new JLabel("请使用手机哔哩哔哩扫码登录："), 0);

        lblQRCode = new JLabel();
        LayoutUtil.addGridBarY(contentPanel, lblQRCode, 1);

        lblTipMsg = new JLabel();
        LayoutUtil.addGridBarY(contentPanel, lblTipMsg, 2);

        // 左上角显示
        add(contentPanel, new GridBagConstraints(0, 0, 1, 1,
                1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(10, 10, 10, 10), 0, 0));
        initListener();
        refreshQRCode();
    }

    private void initListener() {

    }

    private void refreshQRCode() {
        if (getLoginQRWorker != null) {
            getLoginQRWorker.cancel(true);
        }
        if (loginRunnable != null) {
            loginRunnable.setInterrupt(true);
        }
        lblTipMsg.setText("正在获取登录二维码...");
        getLoginQRWorker = new GetLoginQRWorker(client, this, lblQRCode, lblTipMsg);
        getLoginQRWorker.execute();
    }

    public void waitLogin(QRCode qrCode) {
        loginRunnable = new LoginRunnable(client, this, lblTipMsg, qrCode);
        new Thread(loginRunnable).start();
    }

}
