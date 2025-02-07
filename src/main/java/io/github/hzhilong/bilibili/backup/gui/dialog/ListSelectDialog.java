package io.github.hzhilong.bilibili.backup.gui.dialog;

import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.base.utils.ListUtil;
import io.github.hzhilong.baseapp.dialog.BaseDialog;
import io.github.hzhilong.baseapp.utils.LayoutUtil;
import lombok.Getter;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * 列表选择对话框
 *
 * @author hzhilong
 * @version 1.0
 */
public class ListSelectDialog<T> extends BaseDialog {

    public interface Callback<T> {

        String[] initColumnNames();

        String cellText(T data, int rowIndex, int columnIndex);

        default boolean onClose() {
            return true;
        }
    }

    private String tip;

    private List<T> list;

    @Getter
    private List<T> selectedList;

    private Callback<T> callback;

    private JTable table;
    private JButton btnOK;

    public ListSelectDialog(Window parent, String appIconPath, String title, String tip, List<T> list, Callback<T> callback) {
        super(parent, appIconPath, title);
        this.tip = tip;
        this.list = list;
        this.callback = callback;
        try {
            initData();
            initUI();
            setModal(true);
        } catch (BusinessException e) {
            throw new RuntimeException(e);
        }
    }

    private void initData() {
    }


    private void initUI() throws BusinessException {
        setSize(500, 300);
        setMinimumSize(new Dimension(500, 300));
        setLayout(new GridBagLayout());
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridBagLayout());

        // 添加内容
        LayoutUtil.addGridBar(contentPanel, new JLabel(tip), 0, 0, 1, 1);
        LayoutUtil.addGridBar(contentPanel, new JLabel("（按住 Ctrl 多选）"), 1, 0, 1, 1);

        table = initTable();
        JScrollPane listPanel = new JScrollPane(table);
        GridBagConstraints constraints = LayoutUtil.getGridBagConstraints(0, 1, 2, 8);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.weighty = 1;
        contentPanel.add(listPanel, constraints);

        JButton btnAll = new JButton("全选");
        LayoutUtil.addGridBar(contentPanel, btnAll, 0, 2 + 8, GridBagConstraints.WEST, 1, 1);

        btnOK = new JButton("确定");
        LayoutUtil.addGridBar(contentPanel, btnOK, 1, 2 + 8, GridBagConstraints.EAST, 1, 1);

        // 左上角显示
        add(contentPanel, new GridBagConstraints(0, 0, 1, 1,
                1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 10, 10, 10), 0, 0));

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (callback.onClose()) {
                    dispose();
                }
            }
        });
        btnAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selCount = table.getSelectedRows().length;
                if (selCount == ListUtil.getSize(list)) {
                    table.clearSelection();
                } else {
                    table.selectAll();
                }
            }
        });
        btnOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = table.getSelectedRows();
                selectedList = new ArrayList<>(selectedRows.length);
                for (int index : selectedRows) {
                    selectedList.add(list.get(table.getRowSorter().convertRowIndexToModel(index)));
                }
                dispose();
            }
        });
    }

    private JTable initTable() {
        String[] columnNames = callback.initColumnNames();
        AbstractTableModel model = new AbstractTableModel() {
            @Override
            public int getRowCount() {
                return ListUtil.getSize(list);
            }

            @Override
            public int getColumnCount() {
                return columnNames.length;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                T rowData = list.get(rowIndex);
                return callback.cellText(rowData, rowIndex, columnIndex);
            }

            @Override
            public String getColumnName(int column) {
                return columnNames[column];
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }
        };

        JTable table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        return table;
    }

}
