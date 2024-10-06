package top.ybgnb.bilibili.backup.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import top.ybgnb.bilibili.backup.error.BusinessException;

import java.io.File;
import java.io.IOException;

/**
 * @ClassName FileUtil
 * @Description
 * @Author hzhilong
 * @Time 2024/9/29
 * @Version 1.0
 */
@Slf4j
public class FileUtil {

    public static void writeJsonFile(String path, String name, Object obj) throws BusinessException {
        File dir = new File(path);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new BusinessException("创建文件夹失败：" + path);
            }
        }
        String filePath = path + name;
        try {
            FileUtils.writeStringToFile(new File(filePath),
                    JSON.toJSONString(obj, true), "UTF-8");
        } catch (IOException e) {
            throw new BusinessException("写入文件失败：" + filePath);
        }
    }

    public static String readJsonFile(String path, String name) throws BusinessException {
        try {
            return FileUtils.readFileToString(new File(path, name), "UTF-8");
        } catch (IOException e) {
            throw new BusinessException("读取文件失败");
        }
    }
}
