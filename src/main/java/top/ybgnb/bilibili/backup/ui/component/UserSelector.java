package top.ybgnb.bilibili.backup.ui.component;

import lombok.Getter;
import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.app.bean.SavedUser;
import top.ybgnb.bilibili.backup.app.state.UserManager;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.biliapi.utils.ListUtil;
import top.ybgnb.bilibili.backup.ui.utils.LayoutUtil;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * @ClassName UserSelector
 * @Description 账号选择器
 * @Author hzhilong
 * @Time 2024/11/28
 * @Version 1.0
 */
public class UserSelector extends JPanel implements ComponentInit {

    private OkHttpClient client;

    protected EventListenerList listenerList = new EventListenerList();
    private boolean firingActionEvent = false;
    protected String actionCommand = "userSelectorChanged";

    /**
     * 当前选择的账号
     */
    @Getter
    private SavedUser currUser;

    /**
     * 已保存的账号
     */
    private List<SavedUser> savedUsers;

    /**
     * 下拉列表
     */
    @Getter
    private JComboBox<SavedUser> cmbUser;

    /**
     * 登录按钮
     */
    private JButton btnLogin;

    /**
     * 登出按钮
     */
    private JButton btnLogOut;

    /**
     * 刷新按钮
     */
    private JButton btnRefresh;

    private JPanel contentPanel;

    private boolean resetting = false;

    public UserSelector(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public void initData() throws BusinessException {

    }

    @Override
    public void initUI() throws BusinessException {
        setLayout(new GridBagLayout());

        contentPanel = new JPanel();
        contentPanel.setLayout(new GridBagLayout());

        LayoutUtil.addGridBarX(contentPanel, new JLabel("当前账号："), 0);
        this.cmbUser = new JComboBox<>();
        LayoutUtil.addGridBarX(contentPanel, cmbUser, 1);

        this.btnLogOut = new JButton("登出");
        this.btnLogOut.setVisible(false);
        LayoutUtil.addGridBarX(contentPanel, btnLogOut, 2);
        this.btnRefresh = new JButton("刷新");
        LayoutUtil.addGridBarX(contentPanel, btnRefresh, 3);
        this.btnLogin = new JButton("登录新账号");
        LayoutUtil.addGridBarX(contentPanel, btnLogin, 4);
        // 左上角显示
        add(contentPanel, new GridBagConstraints(0, 0, 1, 1,
                1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        initListener();
        // 刷新账号
        this.refreshUser();
    }

    private void initListener() {
        cmbUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currUser = (SavedUser) cmbUser.getSelectedItem();
                btnLogOut.setVisible(currUser != null);
                if (!resetting) {
                    fireActionEvent();
                }
            }
        });
        btnRefresh.addActionListener(e -> {
            refreshUser();
        });
        btnLogin.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(SwingUtilities.getWindowAncestor(this),
                    "是否登录新账号？", "提示",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                loginUser();
            }
        });
        btnLogOut.addActionListener(e -> {
            if (currUser == null) {
                JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), "请先选择账号！", "提示", JOptionPane.OK_OPTION);
                return;
            }
            int result = JOptionPane.showConfirmDialog(SwingUtilities.getWindowAncestor(this),
                    "是否退出登录当前账号[" + currUser + "]？", "提示",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                UserManager.delete(currUser.getMid());
                refreshUser();
            }
        });
    }

    private void loginUser() {
        LoginUserDialog loginUserDialog = new LoginUserDialog(client);
        loginUserDialog.setVisible(true);
        SavedUser savedUser = loginUserDialog.getSavedUser();
        if (savedUser != null) {
            try {
                UserManager.save(savedUser);
                refreshUser(savedUser);
            } catch (BusinessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void refreshUser() {
        refreshUser(null);
    }

    public void refreshUser(SavedUser defaultUser) {
        resetting = true;
        cmbUser.removeAllItems();
        try {
            savedUsers = UserManager.readAllUser();
        } catch (BusinessException e) {
            savedUsers = null;
        }
        if (ListUtil.isEmpty(savedUsers)) {
            btnLogOut.setVisible(false);
            resetting = false;
            cmbUser.setSelectedItem(-1);
            return;
        }
        for (SavedUser savedUser : savedUsers) {
            cmbUser.addItem(savedUser);
            if (defaultUser != null && savedUser.getMid() == defaultUser.getMid()) {
                defaultUser = savedUser;
            }
        }
        resetting = false;
        btnLogOut.setVisible(defaultUser != null);
        cmbUser.setSelectedItem(defaultUser);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        cmbUser.setEnabled(enabled);
        btnLogin.setEnabled(enabled);
        btnRefresh.setEnabled(enabled);
        btnLogOut.setEnabled(enabled);
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
