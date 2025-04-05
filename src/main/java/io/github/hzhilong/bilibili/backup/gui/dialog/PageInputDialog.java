package io.github.hzhilong.bilibili.backup.gui.dialog;

import io.github.hzhilong.baseapp.dialog.BaseDialog;
import io.github.hzhilong.baseapp.utils.LayoutUtil;
import lombok.Getter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author hzhilong
 * @version 1.0
 */
public class PageInputDialog extends BaseDialog {

    private JTextField startField;
    private JTextField endField;

    // 返回结果数组 [-1,-1]表示取消
    @Getter
    private int[] result = {-1, -1};


    public PageInputDialog(Window parentWindow, String appIconPath, String title, String content, int start, int end) {
        super(parentWindow, appIconPath, title);

        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridBagLayout());
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPanel);

        LayoutUtil.addGridBar(contentPanel, new JLabel(content), 0, 0, 2, 1);

        LayoutUtil.addGridBar(contentPanel, new JLabel("开始页码:"), 0, 1);
        startField = new JTextField(String.valueOf(start));
        LayoutUtil.addGridBar(contentPanel, startField, 1, 1);

        LayoutUtil.addGridBar(contentPanel, new JLabel("结束页码:"), 0, 2);
        endField = new JTextField(String.valueOf(end));
        LayoutUtil.addGridBar(contentPanel, endField, 1, 2);

        JButton okButton = new JButton("确定");
        JButton cancelButton = new JButton("取消");
        LayoutUtil.addGridBar(contentPanel, cancelButton, 0, 3);
        LayoutUtil.addGridBar(contentPanel, okButton, 1, 3);

        // 确定按钮逻辑
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int start = Integer.parseInt(startField.getText());
                    int end = Integer.parseInt(endField.getText());

                    if (start < 1 || end < 1) {
                        JOptionPane.showMessageDialog(parentWindow, "页码必须大于0！");
                    } else if (start > end) {
                        JOptionPane.showMessageDialog(parentWindow, "开始页码不能大于结束页码！");
                    } else {
                        result[0] = start;
                        result[1] = end;
                        PageInputDialog.this.dispose();
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(parentWindow, "请输入有效的数字！");
                }
            }
        });

        // 取消按钮逻辑
        cancelButton.addActionListener(e -> PageInputDialog.this.dispose());
    }

}
