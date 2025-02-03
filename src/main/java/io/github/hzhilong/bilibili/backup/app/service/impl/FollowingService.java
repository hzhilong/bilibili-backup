package io.github.hzhilong.bilibili.backup.app.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.base.utils.ListUtil;
import io.github.hzhilong.base.utils.StringUtils;
import io.github.hzhilong.baseapp.business.IBusinessType;
import io.github.hzhilong.bilibili.backup.api.bean.ApiResult;
import io.github.hzhilong.bilibili.backup.api.bean.Relation;
import io.github.hzhilong.bilibili.backup.api.bean.RelationAct;
import io.github.hzhilong.bilibili.backup.api.bean.RelationTag;
import io.github.hzhilong.bilibili.backup.api.bean.page.PageData;
import io.github.hzhilong.bilibili.backup.api.request.CreateApi;
import io.github.hzhilong.bilibili.backup.api.request.ListApi;
import io.github.hzhilong.bilibili.backup.api.request.ModifyApi;
import io.github.hzhilong.bilibili.backup.api.request.PageApi;
import io.github.hzhilong.bilibili.backup.api.user.User;
import io.github.hzhilong.bilibili.backup.app.bean.BusinessResult;
import io.github.hzhilong.bilibili.backup.app.business.BusinessType;
import io.github.hzhilong.bilibili.backup.app.error.ApiException;
import io.github.hzhilong.bilibili.backup.app.service.RelationService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 关注
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class FollowingService extends RelationService {

    private final PageApi<PageData<Relation>, Relation> pageApi;

    public FollowingService(OkHttpClient client, User user, String path) {
        super(client, user, path);
        pageApi = new PageApi<>(client, signUser(), "https://api.bilibili.com/x/relation/followings",
                queryParams -> {
                    queryParams.put("vmid", user.getUid());
                    queryParams.put("order", "desc");
                },
                Relation.class);
    }

    @Override
    public List<BusinessResult<List<Relation>>> backup() throws BusinessException {
        try {
            backupData("关注分组", this::getRelationTags);
        } catch (BusinessException be) {
            if (!user.isCancelledAccount()) {
                throw be;
            }
        }
        return createResults(backupData("关注", new BackupCallback<List<Relation>>() {
            @Override
            public List<Relation> getData() throws BusinessException {
                return getRelations(BusinessType.BACKUP);
            }

            @Override
            public void finished(List<Relation> data) {
                callbackBackupSegment(pageApi, data);
            }
        }, getSegmentPageNo() > 1));
    }

    private List<RelationTag> getRelationTags() throws BusinessException {
        return new ListApi<List, RelationTag>(client, signUser(), "https://api.bilibili.com/x/relation/tags",
                List.class, RelationTag.class).getList();
    }

    private List<Relation> getRelations(IBusinessType businessType) throws BusinessException {
        if (BusinessType.BACKUP.equals(businessType)) {
            return pageApi.getAllData(getSegmentPageNo(), getSegmentMaxSize());
        }
        pageApi.setPageSize(50);
        return pageApi.getAllData();
    }

    @Override
    public List<BusinessResult<List<Relation>>> restore() throws BusinessException {
        log.info("正在还原[关注]...");
        List<RelationTag> oldTags = JSONObject.parseObject(
                readJsonFile(path, "", "关注分组"), new TypeReference<List<RelationTag>>() {
                });
        log.info("解析旧账号关注分组...");

        HashMap<Long, Long> oldIdMapNewId = new HashMap<>();
        if (oldTags != null && !oldTags.isEmpty()) {
            log.info("获取新账号关注分组...");
            List<RelationTag> relationTags = getRelationTags();

            HashMap<String, Long> newTags = new HashMap<>();
            for (RelationTag relationTag : relationTags) {
                newTags.put(relationTag.getName(), relationTag.getTagId());
            }
            List<RelationTag> needCreateTags = new ArrayList<>();
            for (RelationTag oldTag : oldTags) {
                if (newTags.containsKey(oldTag.getName())) {
                    oldIdMapNewId.put(oldTag.getTagId(), newTags.get(oldTag.getName()));
                } else {
                    needCreateTags.add(new RelationTag(oldTag.getTagId(), oldTag.getName()));
                }
            }
            Collections.reverse(needCreateTags);
            log.info("需要新建的关注分组：{}", JSON.toJSONString(needCreateTags));
            for (RelationTag needCreateTag : needCreateTags) {
                handleInterrupt();
                log.info("正在新建关注分组：{}", needCreateTag.getName());
                ApiResult<RelationTag> apiResult = new CreateApi<RelationTag>(client, user,
                        "https://api.bilibili.com/x/relation/tag/create", RelationTag.class)
                        .create(new HashMap<String, String>() {{
                            put("tag", needCreateTag.getName());
                        }});
                if (apiResult.isSuccess()) {
                    oldIdMapNewId.put(needCreateTag.getTagId(), apiResult.getData().getTagId());
                } else {
                    throw new ApiException("新建关注分组失败", apiResult);
                }
            }
        }

        HashSet<Long> newFollowingIds = new HashSet<>();
        HashMap<Long, Relation> mapNewFollowing = new HashMap<>();
        if (isDirectRestore()) {
            log.info("还原时忽略新账号现有的数据，直接还原...");
        } else {
            log.info("获取新账号关注...");
            List<Relation> newFollowings = getRelations(BusinessType.RESTORE);
            for (Relation newFollowing : newFollowings) {
                newFollowingIds.add(newFollowing.getMid());
                mapNewFollowing.put(newFollowing.getMid(), newFollowing);
            }
        }

        List<Relation> oldFollowings = JSONObject.parseObject(readJsonFile(path, "", "关注"),
                new TypeReference<List<Relation>>() {
                });
        log.info("解析旧账号关注：{}", JSON.toJSONString(oldFollowings.size()));
        if (ListUtil.isEmpty(oldFollowings)) {
            log.info("关注为空，无需还原");
            return null;
        }
        Map<String, CopyUser> copyUsers = new HashMap<>();
        Collections.reverse(oldFollowings);

        int page = getSegmentPageNo();
        int pageSize = getSegmentMaxSize();
        // 截取旧数据
        if (pageSize > 0 && page > 0) {
            log.info("正在还原第{}页，分页大小：{}", page, pageSize);
            int start = (page - 1) * pageSize;
            oldFollowings = oldFollowings.subList(start, Math.min(start + pageSize, oldFollowings.size()));
        }

        Set<Long> failIds = new HashSet<>();
        String logNoFormat = StringUtils.getLogNoFormat(oldFollowings.size());
        for (int i = 0; i < oldFollowings.size(); i++) {
            Relation oldFollowing = oldFollowings.get(i);
            handleInterrupt();
            log.info("{} UP主：{}", String.format(logNoFormat, i + 1), oldFollowing.getUname());
            boolean isFollowed = false;
            boolean modifySuccess = true;
            if (newFollowingIds.contains(oldFollowing.getMid())) {
                log.info("已关注UP主：{}", oldFollowing.getUname());
                isFollowed = true;
            } else {
                try {
                    Thread.sleep(1111);
                } catch (InterruptedException ignored) {

                }
                try {
                    modify(oldFollowing, RelationAct.FOLLOW);
                } catch (Exception e) {
                    modifySuccess = false;
                    failIds.add(oldFollowing.getMid());
                    if (!isAllowFailure()) {
                        throw e;
                    }
                }
            }
            // 处理该关注的关注分组
            List<Long> oldFollowingTag = oldFollowing.getTag();
            if (modifySuccess && ListUtil.notEmpty(oldFollowingTag) && !oldIdMapNewId.isEmpty()) {
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
                    if (apiResult.isFail()) {
                        log.info("复制用户至分组[{}]:{}失败", copyUser.tags.toString(), idList);
                    }
                }
            }
        }
        callbackRestoreSegment(oldFollowings);
        List<BusinessResult<List<Relation>>> results = new ArrayList<>();
        BusinessResult<List<Relation>> result = getListBackupRestoreResult(oldFollowings, failIds);
        results.add(result);
        return results;
    }

    @NotNull
    private static BusinessResult<List<Relation>> getListBackupRestoreResult(List<Relation> oldFollowings, Set<Long> failIds) {
        BusinessResult<List<Relation>> result = new BusinessResult<>();
        result.setBusinessType(BusinessType.RESTORE);
        result.setItemName("关注");
        int oldSize = oldFollowings.size();
        int newSize = oldSize - failIds.size();
        String msg = String.format("关注还原%s：已还原%s个，原%s个",
                oldSize == newSize ? "成功" : "失败", newSize, oldSize);
        if (oldSize == newSize) {
            result.setSuccess(msg);
        } else {
            result.setFailed(msg);
        }
        return result;
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

    @Override
    public void setInterrupt(boolean interrupt) {
        if (pageApi != null) {
            pageApi.setInterrupt(interrupt);
        }
        super.setInterrupt(interrupt);
    }

    @Override
    public List<BusinessResult<List<Relation>>> clear() throws BusinessException {
        return createResults(clearList("关注",
                new ClearListCallback<Relation>() {
                    @Override
                    public List<Relation> getList() throws BusinessException {
                        return FollowingService.this.getRelations(BusinessType.CLEAR);
                    }

                    @Override
                    public void delData(Relation data) throws BusinessException {
                        FollowingService.this.modify(data, RelationAct.UNFOLLOW, false);
                    }

                    @Override
                    public String dataName(Relation data) {
                        return String.format("关注[%s]", data.getUname());
                    }
                }));
    }
}
