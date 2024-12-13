package io.github.hzhilong.bilibili.backup.app.utils;

import io.github.hzhilong.base.error.BusinessException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * 资源工具
 *
 * @author hzhilong
 * @version 1.0
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
