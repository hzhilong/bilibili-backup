package top.ybgnb.bilibili.backup.ui.component;

import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @ClassName LinkLabel
 * @Description 链接文本
 * @Author hzhilong
 * @Time 2024/11/27
 * @Version 1.0
 */
public class LinkLabel extends JLabel {

    private String label;
    private String url;

    public LinkLabel(String label, String url) {
        this.label = label;
        this.url = url;
        try {
            this.initData();
            this.initUI();
        } catch (BusinessException e) {

        }
    }

    private void initData() throws BusinessException {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                try {
                    Desktop.getDesktop().browse(new URI(LinkLabel.this.url));
                } catch (IOException | URISyntaxException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                // 手形光标样式
                LinkLabel.this.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
        });
    }

    private void initUI() throws BusinessException {
        this.setText("<html>" + this.label + "<a href=\"" + this.url + "\">" + this.url + "</a></html>");
    }
}
