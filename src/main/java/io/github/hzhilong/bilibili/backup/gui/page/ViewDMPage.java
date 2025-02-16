package io.github.hzhilong.bilibili.backup.gui.page;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.github.hzhilong.base.bean.BuCallback;
import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.base.utils.FileUtil;
import io.github.hzhilong.base.utils.ListUtil;
import io.github.hzhilong.base.utils.StringUtils;
import io.github.hzhilong.baseapp.component.BaseFrame;
import io.github.hzhilong.baseapp.component.LinkLabel;
import io.github.hzhilong.baseapp.dialog.BaseLoadingDialog;
import io.github.hzhilong.baseapp.utils.LayoutUtil;
import io.github.hzhilong.bilibili.backup.api.bean.SimpleDM;
import io.github.hzhilong.bilibili.backup.api.bean.Upper;
import io.github.hzhilong.bilibili.backup.api.bean.UserCard;
import io.github.hzhilong.bilibili.backup.api.bean.VideoPart;
import io.github.hzhilong.bilibili.backup.app.constant.AppConstant;
import io.github.hzhilong.bilibili.backup.app.utils.DMBackupUtil;
import io.github.hzhilong.bilibili.backup.gui.worker.GetUserCardRunnable;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class ViewDMPage extends PagePanel {
    private String cookie;
    private JFileChooser choFile;
    private File currFile;
    private java.util.List<SimpleDM> listData;
    private JTable table;
    private JScrollPane tablePanel;
    private JTextField txtSearchContent;
    private TableRowSorter<TableModel> rowSorter;
    private BaseLoadingDialog loadingDialog;

    public ViewDMPage(BaseFrame parent, String appIconPath, OkHttpClient client, String cookie) {
        super(parent, appIconPath, client);
        this.cookie = cookie;
        this.loadingDialog = new BaseLoadingDialog(parentWindow, AppConstant.APP_ICON, "提示", "请求数据中，请稍后...");
    }

    @Override
    public void initData() {
        super.initData();
        choFile = new JFileChooser();
        choFile.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().endsWith(".processed.json");
            }

            @Override
            public String getDescription() {
                return "已处理的弹幕数据";
            }
        });
        choFile.setCurrentDirectory(new File("."));
    }

    @Override
    public void initUI() {
        int posX = 0;
        JButton btnChooseFile = new JButton("打开弹幕文件(processed.json)");
        addFixedContent(btnChooseFile, posX++, 0);
        JButton btnSaveFile = new JButton("保存并覆盖原文件");
        addFixedContent(btnSaveFile, posX++, 0);
        JButton btnSaveHtml = new JButton("重新生成html并覆盖");
        addFixedContent(btnSaveHtml, posX++, 0);
        JButton btnNewHtml = new JButton("新建html(当前显示的数据)");
        addFixedContent(btnNewHtml, posX++, 0);
        addFixedContent(new JLabel("搜索弹幕内容："), posX++, 0);
        txtSearchContent = new JTextField(16);
        addFixedContent(txtSearchContent, posX++, 0);

        initTablePanel(1, posX);

        btnChooseFile.addActionListener(e -> chooseFile());
        btnSaveFile.addActionListener(e -> saveFile());
        btnSaveHtml.addActionListener(e -> saveHtmlFile());
        btnNewHtml.addActionListener(e -> newHtmlFile());
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                initAllColumnSize(table);
            }
        });
        txtSearchContent.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    searchContent(txtSearchContent.getText());
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }

    private void showLoadingDialog(boolean flag) {
        if (flag) {
            loadingDialog.showDialog();
        } else {
            loadingDialog.closeDialog(500);
        }
    }

    private void initTablePanel(int posY, int gridWidth) {
        if (table != null) {
            tablePanel.removeAll();
        }
        if (tablePanel != null) {
            this.remove(tablePanel);
        }
        table = initTable();
        tablePanel = new JScrollPane(table);
        GridBagConstraints constraints = LayoutUtil.getGridBagConstraints(0, posY, gridWidth, 8);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.weighty = 1;
        addDynamicContent(tablePanel, constraints);
    }

    private void chooseFile() {
        int flag = choFile.showDialog(parentWindow, "选择");
        if (flag == JFileChooser.APPROVE_OPTION) {
            refreshTableData(choFile.getSelectedFile());
        }
    }

    private void refreshTableData(File file) {
        log.info("刷新数据 {}", file);
        if (file == null) {
            return;
        }
        try {
            this.listData = JSONArray.parseArray(FileUtil.readJsonFile(file), SimpleDM.class);
        } catch (BusinessException e) {
            showMsg("加载文件内容出错：" + e.getMessage());
            return;
        }
        this.currFile = file;
        txtSearchContent.setText("");
        refreshCurrTableData();
    }

    private void refreshCurrTableData() {
        int selectedRow = table.getSelectedRow();
        List<? extends RowSorter.SortKey> sortKeys = table.getRowSorter().getSortKeys();
        AbstractTableModel tableModel = getTableModel();
        table.setModel(tableModel);
        if (rowSorter == null) {
            rowSorter = new TableRowSorter<>(tableModel);
        }
        initCellRenderer(table);
        table.setRowSorter(rowSorter);
        updateRowHeights(table);
        // 恢复排序
        rowSorter.setSortKeys(sortKeys);
        if (selectedRow > -1 && listData != null && selectedRow < listData.size()) {
            table.setRowSelectionInterval(selectedRow, selectedRow);
        }
    }

    private void searchContent(String txt) {
        if (ListUtil.isEmpty(listData) || rowSorter == null) {
            return;
        }
        if (StringUtils.isEmpty(txt)) {
            rowSorter.setRowFilter(null);
        } else {
            // 忽略大小写
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + txt));
        }
        updateRowHeights(table);
    }

    // 计算并设置所有行的行高
    private static void updateRowHeights(JTable table) {
        if (table.getRowCount() <= 0) {
            return;
        }
        Component comp0 = table.prepareRenderer(table.getCellRenderer(0, 7), 0, 0);
        int minHeight = comp0.getPreferredSize().height;
        for (int row = 0; row < table.getRowCount(); row++) {
            // 获取单元格渲染器组件
            Component comp6 = table.prepareRenderer(table.getCellRenderer(row, 6), row, 6);
            // 计算组件的高度（考虑边框和边距）
            int height = comp6.getPreferredSize().height + table.getRowMargin() + 10;
            table.setRowHeight(row, Math.max(height, minHeight));
        }
    }

    private void saveFile() {
        log.info("保存并覆盖原文件 {}", currFile);
        if (currFile == null || listData == null) {
            showMsg("请先打开已备份的弹幕文件");
            return;
        }
        try {
            FileUtil.writeJsonFile(currFile.getParentFile().getPath(), currFile.getName(), listData);
            showMsg("保存文件成功");
        } catch (BusinessException e) {
            log.error(e.getMessage(), e);
            showMsg("保存文件失败：" + e.getMessage());
        }
    }

    private void saveHtmlFile(boolean isFilterData) {
        if (isFilterData) {
            log.info("新建html文件 {}");
        } else {
            log.info("重新生成html并覆盖 {}", currFile);
        }
        if (currFile == null || listData == null) {
            showMsg("请先打开已备份的弹幕文件");
            return;
        }
        String dirPath = currFile.getParentFile().getPath();
        String videoJson;
        try {
            videoJson = FileUtil.readJsonFile(dirPath, DMBackupUtil.VIDEO_PART_FILE_NAME);
        } catch (BusinessException e) {
            showMsg("该文件夹内缺少【" + DMBackupUtil.VIDEO_PART_FILE_NAME + "】文件");
            return;
        }
        VideoPart videoPart = JSONObject.parseObject(videoJson, VideoPart.class);
        if (videoPart == null) {
            showMsg("【" + DMBackupUtil.VIDEO_PART_FILE_NAME + "】文件内容为空，或者格式错误");
            return;
        }
        try {
            if (!isFilterData) {
                DMBackupUtil.createHtmlFile(DMBackupUtil.getHtmlTemplate(), videoPart, this.listData, dirPath, true);
            } else {
                // 只保存当前显示的数据
                // 假设 table 是你的 JTable 对象
                TableRowSorter<?> sorter = (TableRowSorter<?>) table.getRowSorter();
                List<SimpleDM> newList = new ArrayList<>(table.getRowCount());
                // 遍历过滤后的视图行索引
                for (int viewIndex = 0; viewIndex < table.getRowCount(); viewIndex++) {
                    // 将视图索引转换为模型索引
                    int modelIndex = sorter.convertRowIndexToModel(viewIndex);
                    newList.add(listData.get(modelIndex));
//                    Object value = model.getValueAt(modelIndex, columnIndex);
                }
                String newName = "index_" + (new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date())) + ".html";
                DMBackupUtil.createHtmlFile(DMBackupUtil.getHtmlTemplate(), videoPart, newList, dirPath, newName, true);
            }
            try {
                Desktop.getDesktop().open(new File(dirPath));
            } catch (IOException ignored) {
                System.out.println();
            }
            showMsg("操作成功");
        } catch (BusinessException e) {
            showMsg(e.getMessage());
        }
    }

    private void saveHtmlFile() {
        saveHtmlFile(false);
    }

    private void newHtmlFile() {
        saveHtmlFile(true);
    }

    private static final String[] columnNames = new String[]{"序号", "视频位置", "弹幕内容", "发送时间", "权重", "用户CRC", "反查的用户UID", "操作"};

    private JTable initTable() {
        AbstractTableModel model = getTableModel();
        JTable table = new JTable(model);
        setFont(getFont().deriveFont(15f));
        table.setAutoCreateRowSorter(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        initTableColumns(table);
        return table;
    }


    private void initTableColumns(JTable table) {
        // 获取父容器（例如JScrollPane）的宽度
        TableColumnModel columnModel = initCellRenderer(table);
        table.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                initAllColumnSize(table);
            }
        });
    }

    @NotNull
    private TableColumnModel initCellRenderer(JTable table) {
        TableColumnModel columnModel = table.getColumnModel();
        table.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setColor(component, table, isSelected, row);
                return component;
            }
        });
        columnModel.getColumn(6).setCellRenderer(new UIDTableCellRenderer());
        columnModel.getColumn(6).setCellEditor(new UIDTableCellEditor());
        columnModel.getColumn(7).setCellRenderer(new OptBtnTableCellRenderer());
        columnModel.getColumn(7).setCellEditor(new OptBtnTableCellEditor());
        return columnModel;
    }

    private int initFixedColumnSize(TableColumnModel columnModel) {
        // 0"序号", 1"视频位置", 2"弹幕内容", 3"发送时间", 4"权重", 5"用户CRC", 6"反查的用户UID", 7"操作"
        int[] ws = {60, 100, 0, 170, 70, 100, 130, 140};
        int s = 0;
        for (int i = 0; i < ws.length; i++) {
            int w = ws[i];
            if (w > 0) {
                TableColumn column = columnModel.getColumn(i);
                column.setPreferredWidth(w);
                s = s + w;
            }
        }
        return s;
    }

    private void filterUID(SimpleDM dm) {
        log.info("过滤用户UID：{}", dm);
        List<Long> uids = dm.getUids();
        if (ListUtil.isEmpty(uids)) {
            return;
        }
        List<Upper> users = dm.getUsers();
        if (users != null) {
            log.info("已过滤");
            return;
        }
        showLoadingDialog(true);
        new Thread(new GetUserCardRunnable(client, cookie, dm.getUids(), new BuCallback<List<UserCard>>() {
            @Override
            public void success(List<UserCard> list) {
                List<Upper> users = new ArrayList<>(uids.size());
                for (UserCard card : list) {
                    if (card != null) {
                        UserCard.CardDTO cardDTO = card.getCard();
                        users.add(new Upper(Long.valueOf(cardDTO.getMid()), cardDTO.getName(), cardDTO.getFace(), cardDTO.getSex(), cardDTO.getLevelInfo().getCurrentLevel(),
                                StringUtils.isEmpty(cardDTO.getFace()) || cardDTO.getFace().contains("noface.jpg")));
                    }
                }
                dm.setUsers(users);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        refreshCurrTableData();
                    }
                });
                showLoadingDialog(false);
            }

            @Override
            public void fail(String msg) {
                showLoadingDialog(false);
                showMsg(msg);
            }

            @Override
            public void interrupt() {
                showLoadingDialog(false);
            }
        })).start();
    }

    private void initAllColumnSize(JTable table) {
        if (table != null) {
            TableColumnModel columnModel = table.getColumnModel();
            // 当表格大小变化时重新计算列宽
            int parentWidth = table.getParent().getWidth();
            int sw = initFixedColumnSize(columnModel);
            columnModel.getColumn(2).setPreferredWidth(parentWidth - sw);
        }
    }

    @NotNull
    private AbstractTableModel getTableModel() {
        AbstractTableModel model = new AbstractTableModel() {

            @Override
            public int getRowCount() {
                return ListUtil.getSize(listData);
            }

            @Override
            public int getColumnCount() {
                return columnNames.length;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                if (columnIndex == 0) {
                    return String.valueOf(rowIndex + 1);
                }
                if (listData == null) {
                    return "";
                }
                SimpleDM dm = listData.get(rowIndex);
                // "序号", "视频位置", "弹幕内容", "发送时间", "权重", "用户CRC", "反查的用户UID", "操作"
                switch (columnIndex) {
                    case 1:
                        return dm.getProgress();
                    case 2:
                        return dm.getContent();
                    case 3:
                        return dm.getTime();
                    case 4:
                        return String.valueOf(dm.getWeight());
                    case 5:
                        return dm.getMidHash();
                    case 6:
                    case 7:
                        return dm;
                    default:
                        return "-";
                }
            }

            @Override
            public String getColumnName(int column) {
                return columnNames[column];
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return (columnIndex == 6 || columnIndex == 7) ? SimpleDM.class : String.class;
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return columnIndex == 6 || columnIndex == 7;
            }
        };
        return model;
    }

    public void showMsg(String msg) {
        JOptionPane.showMessageDialog(parentWindow, msg, "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    private static class UIDTableCellRenderer extends DefaultTableCellRenderer {

        private JPanel panel;
        private List<LinkLabel> labels;

        public UIDTableCellRenderer() {
            panel = new JPanel(new GridBagLayout());
            labels = new ArrayList<>(10);
            for (int i = 0; i < 10; i++) {
                LinkLabel linkLabel = new LinkLabel("");
                labels.add(linkLabel);
                LayoutUtil.addGridBar(panel, linkLabel, 0, i);
            }
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            initUIDComp(panel, labels, table, (SimpleDM) value, isSelected, row, false);
            return panel;
        }

    }

    private static class UIDTableCellEditor extends AbstractCellEditor implements TableCellEditor {

        private JPanel panel;
        private Object currentRowValue;
        private List<LinkLabel> labels;

        public UIDTableCellEditor() {
            panel = new JPanel(new GridBagLayout());
            labels = new ArrayList<>(10);
            for (int i = 0; i < 10; i++) {
                LinkLabel linkLabel = new LinkLabel("");
                labels.add(linkLabel);
                LayoutUtil.addGridBar(panel, linkLabel, 0, i);
            }
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            initUIDComp(panel, labels, table, (SimpleDM) value, true, row, true);
            this.currentRowValue = value;
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return currentRowValue;
        }
    }

    private class OptBtnTableCellRenderer extends DefaultTableCellRenderer {

        private JPanel panel;
        private JButton btnFilter;
        private SimpleDM currDM;

        public OptBtnTableCellRenderer() {
            panel = new JPanel(new GridBagLayout());
            btnFilter = new JButton("过滤");
            btnFilter.setToolTipText("反查得到的部分UID未被注册，可过滤掉该数据");
            LayoutUtil.addGridBar(panel, btnFilter, 0, 0, GridBagConstraints.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            currDM = (SimpleDM) value;
            btnFilter.setVisible(currDM.getUids() != null);
            setColor(panel, table, isSelected, row);
            return panel;
        }

    }

    private class OptBtnTableCellEditor extends AbstractCellEditor implements TableCellEditor {

        private JPanel panel;
        private JButton btnFilter;
        private SimpleDM currDM;

        public OptBtnTableCellEditor() {
            panel = new JPanel(new GridBagLayout());
            btnFilter = new JButton("过滤");
            LayoutUtil.addGridBar(panel, btnFilter, 0, 0, GridBagConstraints.CENTER);
            btnFilter.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    filterUID(currDM);
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currDM = (SimpleDM) value;
            setColor(panel, table, true, row);
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return currDM;
        }
    }

    private static void setColor(Component component, JTable table, boolean isSelected, int row) {
        // 根据选中状态设置颜色
        if (isSelected) {
            // 使用 JTable 的默认选中颜色
            component.setBackground(table.getSelectionBackground());
            component.setForeground(table.getSelectionForeground());
        } else {
            // 使用默认背景色
            component.setBackground(table.getBackground());
            component.setForeground(table.getForeground());
        }
    }

    private static void initUIDComp(JPanel panel, List<LinkLabel> labels, JTable table, SimpleDM value, boolean isSelected, int row, boolean isTips) {
        SimpleDM dm = value;
        List<Upper> users = dm.getUsers();
        if (users == null) {
            List<Long> uids = dm.getUids();
            for (int i = 0; i < Math.min(uids.size(), labels.size()); i++) {
                Long uid = uids.get(i);
                labels.get(i).setText(String.valueOf(uid), "https://space.bilibili.com/" + uid);
                labels.get(i).setVisible(true);
                if (isTips) {
                    labels.get(i).setToolTipText(null);
                }
            }
            for (int i = Math.min(uids.size(), labels.size()); i < labels.size(); i++) {
                labels.get(i).setVisible(false);
                if (isTips) {
                    labels.get(i).setToolTipText(null);
                }
            }
        } else {
            for (int i = 0; i < Math.min(users.size(), labels.size()); i++) {
                Upper user = users.get(i);
                Long uid = user.getMid();
                labels.get(i).setText(String.valueOf(uid), "https://space.bilibili.com/" + uid);
                labels.get(i).setVisible(true);
                if (isTips) {
                    if (user.getNoFace()) {
                        labels.get(i).setToolTipText(String.format("<html>UID：%s<br>用户名：%s<br>性别：%s<br>等级：%s<br>空白头像</html>", user.getMid(), user.getName(), user.getSex(), user.getLevel()));
                    } else {
                        labels.get(i).setToolTipText(String.format("<html>UID：%s<br>用户名：%s<br>性别：%s<br>等级：%s</html>", user.getMid(), user.getName(), user.getSex(), user.getLevel()));
                    }
                }
            }
            for (int i = Math.min(users.size(), labels.size()); i < labels.size(); i++) {
                labels.get(i).setVisible(false);
                if (isTips) {
                    labels.get(i).setToolTipText(null);
                }
            }
        }
        setColor(panel, table, isSelected, row);
    }
}
