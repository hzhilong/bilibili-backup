package io.github.hzhilong.bilibili.backup.gui.worker;

import io.github.hzhilong.base.bean.BuResult;
import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.base.utils.ListUtil;
import io.github.hzhilong.base.utils.QRUtil;
import io.github.hzhilong.bilibili.backup.api.bean.QRCode;
import io.github.hzhilong.bilibili.backup.app.service.impl.LoginService;
import io.github.hzhilong.bilibili.backup.gui.dialog.LoginUserDialog;
import okhttp3.OkHttpClient;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * 获取二维码并且进行登录的线程
 *
 * @author hzhilong
 * @version 1.0
 */
public class GetLoginQRWorker extends SwingWorker<BuResult<QRCode>, BuResult<Image>> {

    private final OkHttpClient client;
    private final LoginUserDialog dialog;
    private final JLabel lblQRCode;
    private final JLabel lblTipMsg;

    public GetLoginQRWorker(OkHttpClient client, LoginUserDialog dialog, JLabel lblQRCode, JLabel lblTipMsg) {
        this.client = client;
        this.dialog = dialog;
        this.lblQRCode = lblQRCode;
        this.lblTipMsg = lblTipMsg;
    }

    @Override
    protected BuResult<QRCode> doInBackground() {
        // 显示二维码
        BuResult<Image> getImgResult = BuResult.newSuccess();
        LoginService loginService = new LoginService(client);
        QRCode qrCode = null;
        try {
            qrCode = loginService.generateQRCode();
            getImgResult.setData(QRUtil.createQRImage(qrCode.getUrl()));
        } catch (BusinessException e) {
            getImgResult.setFailed(e.getMessage());
        }
        publish(getImgResult);
        dialog.waitLogin(qrCode);
        return BuResult.newSuccess(qrCode);
    }

    @Override
    protected void process(List<BuResult<Image>> chunks) {
        if (ListUtil.notEmpty(chunks)) {
            BuResult<Image> buResult = chunks.get(chunks.size() - 1);
            if (buResult.isFail()) {
                lblTipMsg.setText(buResult.getMsg());
            } else {
                lblQRCode.setIcon(new ImageIcon(buResult.getData()));
                lblTipMsg.setText("获取二维码成功，等待扫码中...");
            }
        }
    }

}
