package top.ybgnb.bilibili.backup.ui.component;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.biliapi.service.BackupRestoreItem;
import top.ybgnb.bilibili.backup.biliapi.utils.ListUtil;
import top.ybgnb.bilibili.backup.ui.bean.BackupDir;
import top.ybgnb.bilibili.backup.ui.bean.BackupFile;
import top.ybgnb.bilibili.backup.ui.utils.BackupFileUtil;
import top.ybgnb.bilibili.backup.ui.utils.LayoutUtil;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @ClassName BackupFileSelector
 * @Description 备份文件选择器
 * @Author hzhilong
 * @Time 2024/11/28
 * @Version 1.0
 */
@Slf4j
public class BackupFileSelector extends JPanel implements ComponentInit {

    protected EventListenerList listenerList = new EventListenerList();
    private boolean firingActionEvent = false;
    protected String actionCommand = "backupFileChanged";

    /**
     * 当前选择的项目
     */
    @Getter
    private BackupDir currBackupDir;

    /**
     * 可选择的项目
     */
    private List<BackupDir> backupDirs;

    /**
     * 下拉列表
     */
    @Getter
    private JComboBox<BackupDir> cmbBackupDir;

    /**
     * 删除按钮
     */
    private JButton btnDelete;

    /**
     * 刷新按钮
     */
    private JButton btnRefresh;

    private JPanel contentPanel;

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

    private int numberOfRows;

    public BackupFileSelector() {
        this.numberOfRows = BackupRestoreItem.values().length;
    }

    public BackupFileSelector(int numberOfRows) {
        this.numberOfRows = numberOfRows;
    }

    @Override
    public void initData() throws BusinessException {
        initDefaultBackupCountLabel();
    }

    @Override
    public void initUI() throws BusinessException {
        setLayout(new GridBagLayout());

        contentPanel = new JPanel();
        contentPanel.setLayout(new GridBagLayout());

        LayoutUtil.addGridBar(contentPanel, new JLabel("请选择备份文件："), 0, 0);
        this.cmbBackupDir = new JComboBox<>();
        LayoutUtil.addGridBar(contentPanel, cmbBackupDir, 0, 1);

        this.btnDelete = new JButton("删除");
        this.btnDelete.setVisible(false);
        LayoutUtil.addGridBar(contentPanel, btnDelete, 1, 1);
        this.btnRefresh = new JButton("刷新");
        LayoutUtil.addGridBar(contentPanel, btnRefresh, 2, 1);

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
        cmbBackupDir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currBackupDir = (BackupDir) cmbBackupDir.getSelectedItem();
                btnDelete.setVisible(currBackupDir != null);
                if (!resetting) {
                    refreshBackupDirInfo();
                    fireActionEvent();
                }
            }
        });
        btnRefresh.addActionListener(e -> {
            refresh();
        });
        btnDelete.addActionListener(e -> {
            if (currBackupDir == null) {
                JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), "请先选择备份文件！", "提示", JOptionPane.OK_OPTION);
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
        backupDirs = BackupFileUtil.getBackupFiles();
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
        btnDelete.setVisible(defaultItem != null);
        cmbBackupDir.setSelectedItem(defaultItem);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        cmbBackupDir.setEnabled(enabled);
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
                    if (e == null)
                        e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
                                getActionCommand(),
                                mostRecentEventTime, modifiers);
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
