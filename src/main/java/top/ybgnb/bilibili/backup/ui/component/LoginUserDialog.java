package top.ybgnb.bilibili.backup.ui.component;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.app.bean.SavedUser;
import top.ybgnb.bilibili.backup.biliapi.bean.QRCode;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.ui.utils.LayoutUtil;
import top.ybgnb.bilibili.backup.ui.worker.GetLoginQRWorker;
import top.ybgnb.bilibili.backup.ui.worker.LoginRunnable;

import javax.swing.*;
import java.awt.*;

/**
 * @ClassName LoginUserDialog
 * @Description 登录账号对话框
 * @Author hzhilong
 * @Time 2024/11/28
 * @Version 1.0
 */
@Slf4j
public class LoginUserDialog extends JDialog {

    private JPanel contentPanel;

    private JLabel lblQRCode;

    private JLabel lblTipMsg;

    private GetLoginQRWorker getLoginQRWorker;
    private LoginRunnable loginRunnable;

    private OkHttpClient client;

    @Getter
    @Setter
    private SavedUser savedUser;

    public LoginUserDialog(OkHttpClient client) {
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
        setTitle("登录新账号");
        setSize(500, 400);
        setMinimumSize(new Dimension(500, 400));
        // 对话框局域屏幕中央
        setLocationRelativeTo(null);
        // 点击对话框关闭按钮时，销毁对话框
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }


    private void initUI() throws BusinessException {
        setLayout(new GridBagLayout());
        contentPanel = new JPanel();
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
