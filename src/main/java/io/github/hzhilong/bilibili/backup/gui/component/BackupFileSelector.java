package io.github.hzhilong.bilibili.backup.gui.component;

import io.github.hzhilong.base.utils.ListUtil;
import io.github.hzhilong.bilibili.backup.api.service.BackupRestoreItem;
import io.github.hzhilong.bilibili.backup.app.bean.BackupDir;
import io.github.hzhilong.bilibili.backup.app.bean.BackupFile;
import io.github.hzhilong.bilibili.backup.app.utils.BackupFileUtil;
import io.github.hzhilong.bilibili.backup.gui.utils.LayoutUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 备份文件选择器
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class BackupFileSelector extends JPanel {

    protected EventListenerList listenerList = new EventListenerList();
    private boolean firingActionEvent = false;
    protected String actionCommand = "backupFileChanged";

    /**
     * 当前选择的项目
     */
    @Getter
    private BackupDir currBackupDir;

    /**
     * 下拉列表
     */
    @Getter
    private JComboBox<BackupDir> cmbBackupDir;

    /**
     * 打开按钮
     */
    private JButton btnOpen;

    /**
     * 删除按钮
     */
    private JButton btnDelete;

    /**
     * 刷新按钮
     */
    private JButton btnRefresh;

    private JPanel backupDirInfoPanel;

    private LinkedHashMap<BackupRestoreItem, BackupCountLabel> defaultBackupCountLabels;

    public static Color[] defaultColors = new Color[]{
            new Color(0, 174, 236),
            new Color(251, 114, 153),
            new Color(43, 43, 43),
            new Color(128, 122, 210),
            new Color(238, 187, 43),
            new Color(25, 190, 46),
            new Color(255, 154, 0),
    };

    private boolean resetting = false;

    private final int numberOfRows;

    public BackupFileSelector() {
        this.numberOfRows = BackupRestoreItem.values().length;
        init();
    }

    public BackupFileSelector(int numberOfRows) {
        this.numberOfRows = numberOfRows;
        init();
    }

    public void init() {
        initDefaultBackupCountLabel();
        setLayout(new GridBagLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridBagLayout());

        LayoutUtil.addGridBar(contentPanel, new JLabel("请选择备份文件："), 0, 0);
        this.cmbBackupDir = new JComboBox<>();
        LayoutUtil.addGridBar(contentPanel, cmbBackupDir, 0, 1);

        this.btnOpen = new JButton("打开");
        this.btnOpen.setVisible(false);
        LayoutUtil.addGridBar(contentPanel, btnOpen, 1, 1);
        this.btnDelete = new JButton("删除");
        this.btnDelete.setVisible(false);
        LayoutUtil.addGridBar(contentPanel, btnDelete, 2, 1);
        this.btnRefresh = new JButton("刷新");
        LayoutUtil.addGridBar(contentPanel, btnRefresh, 3, 1);

        backupDirInfoPanel = new JPanel();
        backupDirInfoPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = LayoutUtil.getGridBagConstraints(0, 2);
        constraints.gridwidth = 3;
        add(backupDirInfoPanel, constraints);

        // 左上角显示
        add(contentPanel, new GridBagConstraints(0, 0, 1, 1,
                1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
        initListener();
        // 刷新列表
        this.refresh();
    }

    private void initListener() {
        cmbBackupDir.addActionListener(e -> {
            currBackupDir = (BackupDir) cmbBackupDir.getSelectedItem();
            btnOpen.setVisible(currBackupDir != null);
            btnDelete.setVisible(currBackupDir != null);
            if (!resetting) {
                refreshBackupDirInfo();
                fireActionEvent();
            }
        });
        btnOpen.addActionListener(e -> {
            if (currBackupDir == null) {
                JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), "请先选择备份文件！", "提示", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                Desktop.getDesktop().open(currBackupDir.getDirFile());
            } catch (IOException ignored) {
            }
        });
        btnRefresh.addActionListener(e -> BackupFileSelector.this.refresh());
        btnDelete.addActionListener(e -> {
            if (currBackupDir == null) {
                JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), "请先选择备份文件！", "提示", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int result = JOptionPane.showConfirmDialog(SwingUtilities.getWindowAncestor(this),
                    "是否删除当前备份数据[" + currBackupDir + "]？", "提示",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                BackupFileUtil.deleteBackupDir(currBackupDir);
                refresh();
            }
        });
    }

    private void initDefaultBackupCountLabel() {
        BackupRestoreItem[] backupRestoreItems = BackupRestoreItem.values();
        defaultBackupCountLabels = new LinkedHashMap<>(backupRestoreItems.length);
        for (int i = 0; i < backupRestoreItems.length; i++) {
            BackupRestoreItem item = backupRestoreItems[i];
            defaultBackupCountLabels.put(item, new BackupCountLabel(
                    item.getName(), 0, Color.WHITE, defaultColors[i % defaultColors.length]
            ));
        }
    }

    private void refreshBackupDirInfo() {
        backupDirInfoPanel.removeAll();
        if (currBackupDir != null) {
            backupDirInfoPanel.setVisible(true);
            List<BackupFile> backupFiles = currBackupDir.getBackupFiles();
            for (int i = 0; i < backupFiles.size(); i++) {
                BackupFile backupFile = backupFiles.get(i);
                BackupRestoreItem item = backupFile.getItem();
                BackupCountLabel backupCountLabel = defaultBackupCountLabels.get(item);
                backupCountLabel.setCount(backupFile.getCount());
                LayoutUtil.addGridBar(backupDirInfoPanel, backupCountLabel, i % numberOfRows, i / numberOfRows);
            }
        } else {
            backupDirInfoPanel.setVisible(false);
        }
    }

    public void refresh() {
        refresh(null);
    }

    public void refresh(BackupDir defaultItem) {
        resetting = true;
        cmbBackupDir.removeAllItems();
        List<BackupDir> backupDirs = BackupFileUtil.getBackupFiles();
        if (ListUtil.isEmpty(backupDirs)) {
            resetting = false;
            cmbBackupDir.setSelectedItem(-1);
            return;
        }
        for (BackupDir backupDir : backupDirs) {
            cmbBackupDir.addItem(backupDir);
            if (defaultItem != null && backupDir.getName().equals(defaultItem.getName())) {
                defaultItem = backupDir;
            }
        }
        resetting = false;
        btnOpen.setVisible(defaultItem != null);
        btnDelete.setVisible(defaultItem != null);
        cmbBackupDir.setSelectedItem(defaultItem);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        cmbBackupDir.setEnabled(enabled);
        btnOpen.setEnabled(enabled);
        btnRefresh.setEnabled(enabled);
        btnDelete.setEnabled(enabled);
    }

    public void addActionListener(ActionListener l) {
        listenerList.add(ActionListener.class, l);
    }

    protected void fireActionEvent() {
        if (!firingActionEvent) {
            firingActionEvent = true;
            ActionEvent e = null;
            Object[] listeners = listenerList.getListenerList();
            long mostRecentEventTime = EventQueue.getMostRecentEventTime();
            int modifiers = 0;
            AWTEvent currentEvent = EventQueue.getCurrentEvent();
            if (currentEvent instanceof ActionEvent) {
                modifiers = ((ActionEvent) currentEvent).getModifiers();
            }
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == ActionListener.class) {
                    if (e == null) {
                        e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
                                getActionCommand(),
                                mostRecentEventTime, modifiers);
                    }
                    ((ActionListener) listeners[i + 1]).actionPerformed(e);
                }
            }
            firingActionEvent = false;
        }
    }

    private String getActionCommand() {
        return actionCommand;
    }
}
