package top.ybgnb.bilibili.backup.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.ybgnb.bilibili.backup.bean.Upper;
import top.ybgnb.bilibili.backup.business.BuType;
import top.ybgnb.bilibili.backup.error.BusinessException;

import java.io.File;

/**
 * @ClassName CookieUtil
 * @Description
 * @Author hzhilong
 * @Time 2024/9/29
 * @Version 1.0
 */
public class CookieUtil {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Cookie {
        BuType type;
        Upper user;
        String cookie;
    }


    public static void save(Cookie cookie) throws BusinessException {
        FileUtil.writeJsonFile("bin/cookies/", cookie.getType().toString(), cookie);
    }

    public static Cookie read(BuType type) throws BusinessException {
        return JSONObject.parseObject(FileUtil.readJsonFile("bin/cookies/", type.toString()), Cookie.class);
    }

    public static void delete(BuType type){
        new File("bin/cookies/"+type.toString()).delete();
    }
}
