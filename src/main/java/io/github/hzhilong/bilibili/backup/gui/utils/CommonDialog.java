package io.github.hzhilong.bilibili.backup.gui.utils;

import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.base.utils.StringUtils;
import io.github.hzhilong.bilibili.backup.app.constant.AppConstant;

import javax.swing.*;
import java.awt.*;

/**
 * 通用的对话框
 *
 * @author hzhilong
 * @version 1.0
 */
public class CommonDialog {

    public static String getUid(Window parentWindow) throws BusinessException {
        String uid = JOptionPane.showInputDialog(parentWindow, "请输入对方的UID：",
                "提示", JOptionPane.QUESTION_MESSAGE);
        if (StringUtils.isEmpty(uid)) {
            JOptionPane.showMessageDialog(parentWindow, "请输入用户UID！", "提示", JOptionPane.ERROR_MESSAGE);
            throw new BusinessException("请输入用户UID！");
        } else if (!AppConstant.NUM_PATTERN.matcher(uid).find()) {
            JOptionPane.showMessageDialog(parentWindow, "用户UID为纯数字！", "提示", JOptionPane.ERROR_MESSAGE);
            throw new BusinessException("用户UID为纯数字！");
        }
        return uid;
    }
}
