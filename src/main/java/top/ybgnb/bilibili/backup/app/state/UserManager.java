package top.ybgnb.bilibili.backup.app.state;

import com.alibaba.fastjson.JSONObject;
import top.ybgnb.bilibili.backup.app.bean.SavedUser;
import top.ybgnb.bilibili.backup.app.constant.AppConstant;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.biliapi.utils.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @ClassName UserManager
 * @Description 用户管理器(已登录)
 * @Author hzhilong
 * @Time 2024/11/22
 * @Version 1.0
 */
public class UserManager {

    /**
     * 保存用户
     */
    public static void save(SavedUser user) throws BusinessException {
        FileUtil.writeJsonFile(AppConstant.COOKIE_PATH_PREFIX, String.valueOf(user.getMid()), user);
    }

    /**
     * 删除用户
     */
    public static void delete(String uid) {
        new File(AppConstant.COOKIE_PATH_PREFIX + uid).delete();
    }

    /**
     * 获取所有保存的用户
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

        Arrays.sort(userFiles, Comparator.comparingLong(File::lastModified));
        List<SavedUser> savedUsers = new ArrayList<>(userFiles.length);
        for (File userFile : userFiles) {
            savedUsers.add(JSONObject.parseObject(FileUtil.readJsonFile(userFile), SavedUser.class));
        }
        return savedUsers;
    }
}
