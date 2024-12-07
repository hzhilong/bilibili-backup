package top.ybgnb.bilibili.backup.ui.component;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.biliapi.service.BackupRestoreItem;
import top.ybgnb.bilibili.backup.ui.bean.BackupDir;
import top.ybgnb.bilibili.backup.ui.utils.BackupFileUtil;
import top.ybgnb.bilibili.backup.ui.utils.LayoutUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

/**
 * @ClassName BackupRestoreItemSelector
 * @Description 备份/还原项目选择器
 * @Author hzhilong
 * @Time 2024/11/28
 * @Version 1.0
 */
@Slf4j
public class BackupRestoreItemSelector extends JPanel implements ComponentInit {

    private JPanel contentPanel;

    private JLabel lblTitle;

    private LinkedHashMap<String, BackupRestoreItem> items;
    @Getter
    private LinkedHashSet<BackupRestoreItem> selectedItems;
    private java.util.List<JCheckBox> defaultChkItems = new ArrayList<>();
    private java.util.List<GridBagConstraints> defaultChkItemsConstraints = new ArrayList<>();
    private JPanel itemsPanel;
    private int numberOfRows;

    public BackupRestoreItemSelector(LinkedHashMap<String, BackupRestoreItem> items) {
        this.items = items;
        this.numberOfRows = BackupRestoreItem.values().length;
    }

    public BackupRestoreItemSelector(LinkedHashMap<String, BackupRestoreItem> items, int numberOfRows) {
        this.items = items;
        this.numberOfRows = numberOfRows;
    }

    @Override
    public void initData() throws BusinessException {
        if (items == null) {
            resetItemsData(BackupRestoreItem.values());
        }
        selectedItems = new LinkedHashSet<>(items.size());
    }

    private void resetItemsData(java.util.List<BackupRestoreItem> items) {
        if (this.items != null) {
            this.items.clear();
        } else {
            this.items = new LinkedHashMap<>(items == null ? 0 : items.size());
        }
        if (items != null) {
            for (BackupRestoreItem item : items) {
                this.items.put(item.getName(), item);
            }
        }
    }

    private void resetItemsData(BackupRestoreItem[] items) {
        if (this.items != null) {
            this.items.clear();
        } else {
            this.items = new LinkedHashMap<>();
        }
        if (items != null) {
            for (BackupRestoreItem item : items) {
                this.items.put(item.getName(), item);
            }
        }
    }

    @Override
    public void initUI() throws BusinessException {
        setLayout(new GridBagLayout());

        contentPanel = new JPanel();
        contentPanel.setLayout(new GridBagLayout());

        lblTitle = new JLabel("请选择操作的项目：");
        LayoutUtil.addGridBarY(contentPanel, lblTitle, 0);

        itemsPanel = new JPanel();
        itemsPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = LayoutUtil.getGridBagConstraints(0, 1);
        constraints.weightx = 1;
        initDefaultChkItems();
        contentPanel.add(itemsPanel, constraints);

        // 左上角显示
        add(contentPanel, new GridBagConstraints(0, 0, 1, 1,
                1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
    }

    private void initDefaultChkItems() {
        BackupRestoreItem[] defaultItems = BackupRestoreItem.values();
        for (int i = 0; i < defaultItems.length; i++) {
            BackupRestoreItem item = defaultItems[i];
            String itemName = item.getName();
            JCheckBox jCheckBox = new JCheckBox(itemName);
            jCheckBox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        selectedItems.add(items.get(itemName));
                    } else {
                        selectedItems.remove(items.get(itemName));
                    }
                }
            });
            GridBagConstraints constraints = LayoutUtil.getGridBagConstraints(i % numberOfRows, i / numberOfRows);
            this.defaultChkItems.add(jCheckBox);
            this.defaultChkItemsConstraints.add(constraints);
            itemsPanel.add(jCheckBox, constraints);
        }
    }

    public void refreshItems(BackupDir backupDir) {
        if (backupDir == null) {
            resetItemsData(new BackupRestoreItem[0]);
        } else {
            resetItemsData(BackupFileUtil.getBackupRestoreItems(backupDir));
        }
        refreshItemsUI();
    }

    private void refreshItemsUI() {
        selectedItems.clear();
        int pos = 0;
        this.itemsPanel.removeAll();
        for (JCheckBox chkItem : defaultChkItems) {
            if (items != null && items.containsKey(chkItem.getText())) {
                LayoutUtil.addGridBar(itemsPanel, chkItem, pos % numberOfRows, pos / numberOfRows);
                pos++;
            }
        }
        this.itemsPanel.revalidate();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        for (JCheckBox chkItem : defaultChkItems) {
            chkItem.setEnabled(enabled);
        }
    }
}
