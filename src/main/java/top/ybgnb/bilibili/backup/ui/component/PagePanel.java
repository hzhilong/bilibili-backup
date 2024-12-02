package top.ybgnb.bilibili.backup.ui.component;

import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.ui.utils.LayoutUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @ClassName PagePanel
 * @Description 页面面板
 * @Author hzhilong
 * @Time 2024/11/27
 * @Version 1.0
 */
public abstract class PagePanel extends JPanel implements ComponentInit {

    protected OkHttpClient client;

    /**
     * 固定的内容面板
     */
    private JPanel fixedContentPanel;
    /**
     * 动态的内容面板
     */
    private JPanel dynamicContentPanel;
    /**
     * 固定内容面板的辅助区域（帮助布局（子组件数为1的时候））
     */
    protected JComponent fixedAuxiliaryArea;
    /**
     * 动态内容面板的辅助区域（帮助布局（子组件数为1的时候））
     */
    protected JComponent dynamicAuxiliaryArea;
    /**
     * 实际显示内容view的面板
     */
    protected JPanel fixedContentViewPanel;
    protected JPanel dynamicContentViewPanel;

    public PagePanel(OkHttpClient client) {
        this.client = client;
    }

    /**
     * 初始化内容面板（固定+动态）
     */
    @Override
    public void initUI() throws BusinessException {
        this.setLayout(new GridBagLayout());
        // debug
//        this.setBackground(BackupFileSelector.defaultColors[0]);

        fixedContentPanel = initContentPanel(this, 0, 1, 0, GridBagConstraints.HORIZONTAL);
//        fixedContentPanel.setBackground(BackupFileSelector.defaultColors[1]);
        dynamicContentPanel = initContentPanel(this, 1, 1, 1, GridBagConstraints.BOTH);
//        dynamicContentPanel.setBackground(BackupFileSelector.defaultColors[2]);

        fixedAuxiliaryArea = addFixedAuxiliaryArea();
//        fixedAuxiliaryArea.setBackground(BackupFileSelector.defaultColors[3]);
        fixedContentViewPanel = initContentPanel(fixedContentPanel, 1, 1, 0, GridBagConstraints.NONE);
//        fixedContentViewPanel.setBackground(BackupFileSelector.defaultColors[4]);

        dynamicAuxiliaryArea = addDynamicAuxiliaryArea();
//        dynamicAuxiliaryArea.setBackground(BackupFileSelector.defaultColors[5]);
        dynamicContentViewPanel = initContentPanel(dynamicContentPanel, 1, 1, 1, GridBagConstraints.BOTH);
//        dynamicContentViewPanel.setBackground(BackupFileSelector.defaultColors[6]);
    }

    private JPanel initContentPanel(JPanel parent, int gridy, int weightx, int weighty, int fill) {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new GridBagLayout());
        parent.add(jPanel, new GridBagConstraints(0, gridy, 1, 1,
                weightx, weighty, GridBagConstraints.NORTHWEST, fill,
                new Insets(0, 0, 0, 0), 0, 0));
        return jPanel;
    }

    /**
     * 设置内容可见性
     *
     * @param contentPanel
     * @param flag
     */
    private void setContentVisible(JPanel contentPanel, boolean flag) {
        Component[] components = contentPanel.getComponents();
        if (components != null) {
            for (Component component : components) {
                component.setVisible(flag);
            }
        }
    }

    /**
     * 设置固定内容可见性
     *
     * @param flag
     */
    public void setFixedContentVisible(boolean flag) {
        setContentVisible(fixedContentViewPanel, flag);
    }

    /**
     * 设置动态内容可见性
     *
     * @param flag
     */
    public void setDynamicContentVisible(boolean flag) {
        setContentVisible(dynamicContentViewPanel, flag);
    }

    private void setContentEnabled(JPanel contentPanel, boolean flag) {
        Component[] components = contentPanel.getComponents();
        if (components != null) {
            for (Component component : components) {
                component.setEnabled(flag);
            }
        }
    }

    public void setFixedContentEnabled(boolean flag) {
        setContentEnabled(fixedContentViewPanel, flag);
    }

    public void setDynamicContentEnabled(boolean flag) {
        setContentEnabled(dynamicContentViewPanel, flag);
    }


    /**
     * 添加内容
     *
     * @param container
     * @param component
     * @param constraints
     * @return
     * @throws BusinessException
     */
    private GridBagConstraints addContent(Container container, Component component, GridBagConstraints constraints) throws BusinessException {
        container.add(component, constraints);
        if (component instanceof ComponentInit) {
            ComponentInit componentInit = (ComponentInit) component;
            componentInit.init();
        }
        return constraints;
    }

    private GridBagConstraints addContent(Container container, Component component, int x, int y) throws BusinessException {
        return addContent(container, component, LayoutUtil.getGridBagConstraints(x, y));
    }

    /**
     * 添加辅助区域
     *
     * @return
     */
    private JComponent addFixedAuxiliaryArea() {
        JLabel area = new JLabel();
        fixedContentPanel.add(area, new GridBagConstraints(0, 0, 1, 1,
                1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
        return area;
    }

    private JComponent addDynamicAuxiliaryArea() {
        JLabel area = new JLabel();
        dynamicContentPanel.add(area, new GridBagConstraints(0, 0, 1, 1,
                1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
        return area;
    }

    /**
     * 添加固定的内容
     *
     * @param component
     * @param x
     * @param y
     * @return
     * @throws BusinessException
     */
    public GridBagConstraints addFixedContent(Component component, int x, int y) throws BusinessException {
        return addContent(fixedContentViewPanel, component, x, y);
    }

    public GridBagConstraints addFixedContent(Component component, GridBagConstraints constraints) throws BusinessException {
        return addContent(fixedContentViewPanel, component, constraints);
    }

    /**
     * 添加动态的内容
     *
     * @param component
     * @param x
     * @param y
     * @return
     * @throws BusinessException
     */
    public GridBagConstraints addDynamicContent(Component component, int x, int y) throws BusinessException {
        return addContent(dynamicContentViewPanel, component, x, y);
    }

    public GridBagConstraints addDynamicContent(Component component, GridBagConstraints constraints) throws BusinessException {
        return addContent(dynamicContentViewPanel, component, constraints);
    }

    /**
     * 添加分割线到固定的内容区域
     *
     * @param x
     * @param y
     * @param gridWidth
     * @return
     * @throws BusinessException
     */
    public JSeparator addSeparatorToFixed(int x, int y, int gridWidth) throws BusinessException {
        JSeparator separator = new JSeparator();
        addFixedContent(separator, LayoutUtil.getSeparatorConstraints(x, y, gridWidth));
        return separator;
    }

    public JSeparator addSeparatorToFixed(int x, int y) throws BusinessException {
        return addSeparatorToFixed(x, y, 1);
    }

    /**
     * 添加分割线到动态的内容区域
     *
     * @param x
     * @param y
     * @return
     * @throws BusinessException
     */
    public JSeparator addSeparatorToDynamic(int x, int y, int gridWidth) throws BusinessException {
        JSeparator separator = new JSeparator();
        addDynamicContent(separator, LayoutUtil.getSeparatorConstraints(x, y, gridWidth));
        return separator;
    }

    public JSeparator addSeparatorToDynamic(int x, int y) throws BusinessException {
        return addSeparatorToDynamic(x, y, 1);
    }

    /**
     * 添加日志输出组件到动态内容
     *
     * @param x
     * @param y
     * @param gridWidth
     * @return
     * @throws BusinessException
     */
    public JScrollPane addTxtLogToDynamic(int x, int y, int gridWidth) throws BusinessException {
        // 日志区域
        JTextArea txtLog = new JTextArea();
        txtLog.setEditable(false);
        txtLog.setLineWrap(true);
        JScrollPane scrollPaneLog = new JScrollPane(txtLog);
        scrollPaneLog.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPaneLog.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        GridBagConstraints tempConstraints = LayoutUtil.getGridBagConstraints(x, y);
        tempConstraints.fill = GridBagConstraints.BOTH;
        tempConstraints.weightx = 1;
        tempConstraints.weighty = 1;
        tempConstraints.gridwidth = gridWidth;
        addDynamicContent(scrollPaneLog, tempConstraints);
        return scrollPaneLog;
    }

    public JScrollPane addTxtLogToDynamic(int x, int y) throws BusinessException {
        return addTxtLogToDynamic(x, y, 1);
    }
}
