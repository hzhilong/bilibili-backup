package top.ybgnb.bilibili.backup.ui.worker;

import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.biliapi.bean.BuResult;
import top.ybgnb.bilibili.backup.biliapi.bean.QRCode;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.biliapi.service.impl.LoginService;
import top.ybgnb.bilibili.backup.biliapi.utils.ListUtil;
import top.ybgnb.bilibili.backup.biliapi.utils.QRUtil;
import top.ybgnb.bilibili.backup.ui.component.LoginUserDialog;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * @ClassName GetLoginQRWorker
 * @Description 二维码登录线程
 * @Author hzhilong
 * @Time 2024/11/28
 * @Version 1.0
 */
public class GetLoginQRWorker extends SwingWorker<BuResult<QRCode>, BuResult<Image>> {

    private OkHttpClient client;
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
    protected BuResult<QRCode> doInBackground() throws Exception {
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
