package top.ybgnb.bilibili.backup.biliapi.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.biliapi.bean.ApiResult;
import top.ybgnb.bilibili.backup.biliapi.bean.Relation;
import top.ybgnb.bilibili.backup.biliapi.bean.RelationAct;
import top.ybgnb.bilibili.backup.biliapi.bean.RelationTag;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.biliapi.request.CreateApi;
import top.ybgnb.bilibili.backup.biliapi.request.ListApi;
import top.ybgnb.bilibili.backup.biliapi.request.ModifyApi;
import top.ybgnb.bilibili.backup.biliapi.request.PageApi;
import top.ybgnb.bilibili.backup.biliapi.service.RelationService;
import top.ybgnb.bilibili.backup.biliapi.user.User;
import top.ybgnb.bilibili.backup.biliapi.utils.ListUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @ClassName FollowingService
 * @Description 关注
 * @Author hzhilong
 * @Time 2024/9/26
 * @Version 1.0
 */
@Slf4j
public class FollowingService extends RelationService {

    public FollowingService(OkHttpClient client, User user, String path) {
        super(client, user, path);
    }

    @Override
    public void backup() throws BusinessException {
        try {
            backupData("关注分组", this::getRelationTags);
        } catch (BusinessException be) {
            if (!user.isCancelledAccount()) {
                throw be;
            }
        }
        backupData("关注", this::getRelations);
    }

    private List<RelationTag> getRelationTags() throws BusinessException {
        return new ListApi<List, RelationTag>(client, signUser(), "https://api.bilibili.com/x/relation/tags",
                List.class, RelationTag.class).getList();
    }

    private List<Relation> getRelations() throws BusinessException {
        return new PageApi<>(client, signUser(), "https://api.bilibili.com/x/relation/followings",
                queryParams -> {
                    queryParams.put("vmid", user.getUid());
                    queryParams.put("order", "desc");
                },
                Relation.class).getAllData();
    }

    @Override
    public void restore() throws BusinessException {
        log.info("正在还原[关注]...");
        List<RelationTag> oldTags = JSONObject.parseObject(readJsonFile(path, "", "关注分组"),
                new TypeReference<List<RelationTag>>() {
                });
        log.info("解析旧账号关注分组...");

        HashMap<Long, Long> oldIdMapNewId = new HashMap<>();
        if (oldTags != null && !oldTags.isEmpty()) {
            log.info("获取新账号关注分组...");
            List<RelationTag> relationTags = getRelationTags();

            HashMap<String, Long> newTags = new HashMap<>();
            for (RelationTag relationTag : relationTags) {
                newTags.put(relationTag.getName(), relationTag.getTagid());
            }
            List<RelationTag> needCreateTags = new ArrayList<>();
            for (RelationTag oldTag : oldTags) {
                if (newTags.containsKey(oldTag.getName())) {
                    oldIdMapNewId.put(oldTag.getTagid(), newTags.get(oldTag.getName()));
                } else {
                    needCreateTags.add(new RelationTag(oldTag.getTagid(), oldTag.getName()));
                }
            }
            Collections.reverse(needCreateTags);
            log.info("需要新建的关注分组：{}", JSON.toJSONString(needCreateTags));
            for (RelationTag needCreateTag : needCreateTags) {
                handleInterrupt();
                log.info("正在新建关注分组：{}", needCreateTag.getName());
                ApiResult<RelationTag> apiResult = new CreateApi<RelationTag>(client, user, "https://api.bilibili.com/x/relation/tag/create", RelationTag.class)
                        .create(new HashMap<String, String>() {{
                            put("tag", needCreateTag.getName());
                        }});
                if (apiResult._isSuccess()) {
                    oldIdMapNewId.put(needCreateTag.getTagid(), apiResult.getData().getTagid());
                } else {
                    throw new BusinessException("新建关注分组失败", apiResult);
                }
            }
        }

        log.info("获取新账号关注...");
        List<Relation> newFollowings = getRelations();
        HashSet<Long> newFollowingIds = new HashSet<>();
        HashMap<Long, Relation> mapNewFollowing = new HashMap<>();
        for (Relation newFollowing : newFollowings) {
            newFollowingIds.add(newFollowing.getMid());
            mapNewFollowing.put(newFollowing.getMid(), newFollowing);
        }

        List<Relation> oldFollowings = JSONObject.parseObject(readJsonFile(path, "", "关注"),
                new TypeReference<List<Relation>>() {
                });
        log.info("解析旧账号关注：{}", JSON.toJSONString(oldFollowings.size()));
        if (ListUtil.isEmpty(oldFollowings)) {
            log.info("关注为空，无需还原");
            return;
        }
        Map<String, CopyUser> copyUsers = new HashMap<>();
        Collections.reverse(oldFollowings);
        for (Relation oldFollowing : oldFollowings) {
            handleInterrupt();
            boolean isFollowed = false;
            if (newFollowingIds.contains(oldFollowing.getMid())) {
                log.info("已关注UP主：{}", oldFollowing.getUname());
                isFollowed = true;
            } else {
                try {
                    Thread.sleep(1111);
                } catch (InterruptedException e) {

                }
                modify(oldFollowing, RelationAct.FOLLOW);
            }
            // 处理该关注的关注分组
            List<Long> oldFollowingTag = oldFollowing.getTag();
            if (ListUtil.notEmpty(oldFollowingTag) && !oldIdMapNewId.isEmpty()) {
                boolean isNeedUpdateTags = true;
                if (isFollowed) {
                    // 之前已关注
                    Relation newFollowed = mapNewFollowing.get(oldFollowing.getMid());
                    if (ListUtil.notEmpty(newFollowed.getTag()) && newFollowed.getTag().size() == oldFollowingTag.size()) {
                        for (Long newT : newFollowed.getTag()) {
                            if (!oldFollowingTag.contains(newT)) {
                                break;
                            }
                        }
                        isNeedUpdateTags = false;
                    }
                } else {
                    // 现在才关注
                }
                if (isNeedUpdateTags) {
                    List<Long> newFollowingTag = new ArrayList<>(oldFollowingTag.size());
                    for (Long tag : oldFollowingTag) {
                        if (oldIdMapNewId.containsKey(tag)) {
                            newFollowingTag.add(oldIdMapNewId.get(tag));
                        }
                    }
                    if (!newFollowingTag.isEmpty()) {
                        String newFollowingTagString = newFollowingTag.toString();

                        if (copyUsers.containsKey(newFollowingTagString)) {
                            copyUsers.get(newFollowingTagString).addUser(oldFollowing);
                        } else {
                            CopyUser copyUser = new CopyUser();
                            copyUser.tags = newFollowingTag;
                            copyUser.addUser(oldFollowing);
                            copyUsers.put(newFollowingTagString, copyUser);
                        }
                    }
                }
            }
        }
        if (!copyUsers.isEmpty()) {
            for (Map.Entry<String, CopyUser> entry : copyUsers.entrySet()) {
                CopyUser copyUser = entry.getValue();
                for (List<Long> idList : copyUser.followingIdsList) {
                    handleInterrupt();
                    log.info("正在复制用户至分组[{}]:{}", copyUser.tags.toString(), idList.toString());
                    ApiResult<Void> apiResult = new ModifyApi<Void>(client, user, "https://api.bilibili.com/x/relation/tags/copyUsers",
                            Void.class).modify(
                            new HashMap<String, String>() {{
                                put("fids", ListUtil.listToString(idList, ","));
                                put("tagids", ListUtil.listToString(copyUser.tags, ","));
                            }});
                    if (apiResult._isFail()) {
                        log.info("复制用户至分组[{}]:{}失败", copyUser.tags.toString(), idList.toString());
                    }
                }
            }
        }
    }

    @Override
    public void initFileName(Map<String, String> fileNames) {
        fileNames.put("关注分组", "RelationTags");
        fileNames.put("关注", "Following");
    }

    @Override
    public int getBackupCount(File dir) throws BusinessException {
        return getBackupListSize(dir, "", "关注");
    }

    public static class CopyUser {
        public List<Long> tags;
        public List<List<Long>> followingIdsList;

        public boolean sameTags(List<Long> tempTags) {
            if (tempTags == null || tags == null || tags.size() != tempTags.size()) {
                return false;
            }
            for (Long tag : tempTags) {
                if (!tags.contains(tag)) {
                    return false;
                }
            }
            return true;
        }

        public void addUser(Relation following) {
            if (followingIdsList == null) {
                followingIdsList = new ArrayList<>();
                List<Long> followings = new ArrayList<>();
                followings.add(following.getMid());
                followingIdsList.add(followings);
            } else {
                List<Long> lastList = followingIdsList.get(followingIdsList.size() - 1);
                if (lastList.size() == 20) {
                    List<Long> followings = new ArrayList<>();
                    followings.add(following.getMid());
                    followingIdsList.add(followings);
                } else {
                    lastList.add(following.getMid());
                }
            }
        }
    }
}
