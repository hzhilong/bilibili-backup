package io.github.hzhilong.bilibili.backup.gui.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * 链接文本
 *
 * @author hzhilong
 * @version 1.0
 */
public class LinkLabel extends JLabel {

    private final String label;
    private final String url;

    public LinkLabel(String label, String url) {
        this.label = label;
        this.url = url;
        this.initData();
        this.initUI();
    }

    private void initData() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                try {
                    Desktop.getDesktop().browse(new URI(LinkLabel.this.url));
                } catch (IOException | URISyntaxException ignored) {
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

    private void initUI() {
        this.setText("<html>" + this.label + "<a href=\"" + this.url + "\">" + this.url + "</a></html>");
    }
}
