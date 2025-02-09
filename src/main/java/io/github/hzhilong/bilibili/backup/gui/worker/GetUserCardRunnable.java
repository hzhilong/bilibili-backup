package io.github.hzhilong.bilibili.backup.gui.worker;

import io.github.hzhilong.base.bean.BuCallback;
import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.base.utils.ListUtil;
import io.github.hzhilong.bilibili.backup.api.bean.UserCard;
import io.github.hzhilong.bilibili.backup.api.user.User;
import io.github.hzhilong.bilibili.backup.app.service.impl.UserService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取用户名片信息的线程
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class GetUserCardRunnable extends BaseRunnable {

    private List<Long> uids;
    private final String cookie;
    private final BuCallback<List<UserCard>> buCallback;
    private UserService userService;

    public GetUserCardRunnable(OkHttpClient client, String cookie, List<Long> uids, BuCallback<List<UserCard>> buCallback) {
        super(client);
        this.cookie = cookie;
        this.uids = uids;
        this.buCallback = buCallback;
        this.userService = new UserService(client, new User(cookie));
    }

    @Override
    public void run() {
        log.info("获取用户名片信息：{}", uids);
        if (ListUtil.isEmpty(uids)) {
            buCallback.fail("uids为空");
            return;
        }
        List<UserCard> cards = new ArrayList<>(uids.size());
        for (int i = 0; i < uids.size(); i++) {
            Long uid = uids.get(i);
            try {
                cards.add(userService.getCard(String.valueOf(uid), true));
            } catch (BusinessException e) {
                buCallback.fail("获取uid " + uid + "用户信息时错误：" + e.getMessage());
                return;
//                cards.add(null);
            }
            if (i != uids.size() - 1) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {
                }
            }
        }
        buCallback.success(cards);
    }
}
