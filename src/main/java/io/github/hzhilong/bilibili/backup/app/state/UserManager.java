package io.github.hzhilong.bilibili.backup.app.state;

import com.alibaba.fastjson.JSONObject;
import io.github.hzhilong.bilibili.backup.app.bean.SavedUser;
import io.github.hzhilong.bilibili.backup.app.constant.AppConstant;
import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.base.utils.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * 账号管理器(已登录)
 *
 * @author hzhilong
 * @version 1.0
 */
public class UserManager {

    /**
     * 保存账号
     */
    public static void save(SavedUser user) throws BusinessException {
        FileUtil.writeJsonFile(AppConstant.COOKIE_PATH_PREFIX, String.valueOf(user.getMid()), user);
    }

    /**
     * 删除账号
     */
    public static void delete(String uid) {
        new File(AppConstant.COOKIE_PATH_PREFIX + uid).delete();
    }

    /**
     * 删除账号
     */
    public static void delete(Long uid) {
        delete(String.valueOf(uid));
    }


    /**
     * 获取所有保存的账号
     */
    public static List<SavedUser> readAllUser() throws BusinessException {
        File backupDir = new File(AppConstant.COOKIE_PATH_PREFIX);
        if (!backupDir.exists() || !backupDir.isDirectory()) {
            //  throw new BusinessException("未登录");
            return null;
        }
        File[] userFiles = backupDir.listFiles();
        if (userFiles == null || userFiles.length == 0) {
            //  throw new BusinessException("未登录");
            return null;
        }

        Arrays.sort(userFiles, Comparator.comparingLong(File::lastModified).reversed());
        List<SavedUser> savedUsers = new ArrayList<>(userFiles.length);
        for (File userFile : userFiles) {
            savedUsers.add(JSONObject.parseObject(FileUtil.readJsonFile(userFile), SavedUser.class));
        }
        return savedUsers;
    }
}
