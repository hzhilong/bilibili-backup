package top.ybgnb.bilibili.backup.ui.utils;

import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @ClassName ResourceUtil
 * @Description 资源工具
 * @Author hzhilong
 * @Time 2024/11/27
 * @Version 1.0
 */
public class ResourceUtil {

    public static URL getResource(String path) {
        return ResourceUtil.class.getClassLoader().getResource(path);
    }

    public static InputStream getResourceAsStream(String path) {
        return ResourceUtil.class.getClassLoader().getResourceAsStream(path);
    }

    public static BufferedImage getImage(String fileName) throws BusinessException {
        URL resource = getResource(fileName);
        try {
            return ImageIO.read(resource);
        } catch (IOException e) {
            throw new BusinessException("内部错误");
        }
    }

}
