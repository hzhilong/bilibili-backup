package io.github.hzhilong.bilibili.backup.gui.worker.tools;

import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.base.utils.StringUtils;
import io.github.hzhilong.bilibili.backup.api.bean.ApiResult;
import io.github.hzhilong.bilibili.backup.api.bean.NavNum;
import io.github.hzhilong.bilibili.backup.api.bean.Relation;
import io.github.hzhilong.bilibili.backup.api.bean.UserCard;
import io.github.hzhilong.bilibili.backup.api.request.AddQueryParams;
import io.github.hzhilong.bilibili.backup.api.request.BaseApi;
import io.github.hzhilong.bilibili.backup.api.user.User;
import io.github.hzhilong.bilibili.backup.app.bean.SavedUser;
import io.github.hzhilong.bilibili.backup.app.service.impl.FollowerService;
import io.github.hzhilong.bilibili.backup.app.service.impl.UserService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 移除片姐的线程
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class RemoveScamFollowerRunnable extends ToolRunnable<FollowerService, Void> {
    private final Random random;

    private FollowerService followerService;
    private UserService userService;
    private User currUser;

    public RemoveScamFollowerRunnable(OkHttpClient client, SavedUser user, ToolBuCallback<Void> buCallback) {
        super(client, user, buCallback);
        this.random = new Random();
    }

    @Override
    protected void newServices(LinkedHashSet<FollowerService> services) {
        currUser = new User(user.getCookie());
        followerService = new FollowerService(client, currUser, "");
        userService = new UserService(client, currUser);
        services.add(followerService);
    }

    @Override
    protected Void runTool() throws BusinessException {
//        if (test()) {
//            return null;
//        }
        log.info("正在获取粉丝...");
        List<Relation> followers = followerService.getFollowers();
        log.info("已获取{}个粉丝", followers.size());
        String logNoFormat = StringUtils.getLogNoFormat(followers.size());
        int removedCount = 0;
        for (int i = 0; i < followers.size(); i++) {
            log.info("-------------------------------------------");
            this.handleInterrupt();
            Relation fans = followers.get(i);
            log.info("{} {}  uid:{}", String.format(logNoFormat, i + 1), fans.getUname(), fans.getMid());
            if (fans.getAttribute() == 6) {
                log.info("跳过  已互相关注");
                continue;
            }
            log.info("获取并解析该用户信息...");
            sleep();
            UserCard fansCard = userService.getCard(String.valueOf(fans.getMid()), true, false);
            int scamScore = this.calcScamScore(fansCard);
            if (scamScore != 0) {
                log.info("可疑度：{}", scamScore);
            }
            if (scamScore > 10) {
                log.info("正在移除该粉丝...");
                followerService.removeFollower(fans);
                removedCount++;
            }
        }
        log.info("-------------------------------------------");
        log.info("已移除{}个粉丝", removedCount);
        return null;
    }

    private boolean test() throws BusinessException {
        String[] ids = new String[]{"3546864823568760"};
        int score;
        for (String id : ids) {
            log.info("======================");
            score = calcScamScore(userService.getCard(id, true, false));
            log.info("可疑度: {}", score);
        }
        return true;
    }

    public void sleep() {
        try {
            Thread.sleep(1000 + random.nextInt(2000));
        } catch (InterruptedException ignored) {
        }
    }

    private static int calcAttentionScamScore(int count) {
        double b = 9.3024;
        double c = 1179.05;
        if (count <= 100) return 0;
        return (int) Math.floor(b * (Math.exp((count - 100) / c) - 1));
    }

    private int calcScamScore(UserCard user) throws BusinessException {
        UserCard.CardDTO card = user.getCard();
        Integer level = card.getLevelInfo().getCurrentLevel();
        if (level > 2) {
            log.info("跳过  等级>2");
            return 0;
        }
        int score = 0;
        String msg = String.format("%s级  %s关注  %s粉丝", level, card.getFriend(), card.getFans());
        score += (2 - level) * 5;
        score += calcAttentionScamScore(card.getAttention());
        score -= card.getFans() / 100;

        if (card.getVip().getVipType() != 0) {
            msg += "  大会员";
            score -= 10;
        } else {
            msg += "  非会员";
        }

        if (card.getPendant().getPid() != 0) {
            msg += "  有挂件";
            score -= 5;
        } else {
            msg += "  无挂件";
        }

        if (card.getPendant().getPid() != 0) {
            msg += "  有挂件";
            score -= 5;
        } else {
            msg += "  无挂件";
        }

        if (card.getNameplate().getNid() != 0) {
            msg += "  有勋章";
            score -= 5;
        } else {
            msg += "  无勋章";
        }

        if (card.getSpacesta() != 0) {
            msg += "  已被封禁";
            score -= card.getSpacesta() * 10;
        }

        ApiResult<NavNum> apiResult = new BaseApi<NavNum>(client, currUser,
                "https://api.bilibili.com/x/space/navnum",
                new AddQueryParams() {
                    @Override
                    public void addQueryParams(Map<String, String> queryParams) {
                        queryParams.put("mid", card.getMid());
                        queryParams.put("web_location", "333.1387");
                    }
                }, true, NavNum.class).apiGet();

        if (apiResult.isSuccess()) {
            NavNum navNum = apiResult.getData();
            int posNum = navNum.getVideo() + navNum.getArticle() + navNum.getAlbum() + navNum.getAudio() + navNum.getPugv();
            msg += String.format("  %s投稿  %s追番追剧  %s视频列表  %s收藏夹  %s动态",
                    posNum, navNum.getBangumi() + navNum.getCinema(), navNum.getChannel().getGuest(),
                    navNum.getFavourite().getGuest(), navNum.getOpus()
            );

            score -= posNum * 5;

            score -= navNum.getBangumi() * 2;
            score -= navNum.getCinema() * 2;

            score -= navNum.getChannel().getGuest() * 5;

            score -= navNum.getFavourite().getGuest() * 5;

            score -= navNum.getOpus() * 5;
        }

        log.info(msg);
        return score;
    }
}
