package io.github.hzhilong.bilibili.backup.app.utils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import io.github.hzhilong.base.error.BusinessException;

import java.io.IOException;

/**
 * freemarker Template工具
 *
 * @author hzhilong
 * @version 1.0
 */
public class TemplateUtil {

    public static Template getTemplate(String path) throws BusinessException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_34);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setClassForTemplateLoading(TemplateUtil.class, "/");
        try {
            return cfg.getTemplate(path);
        } catch (IOException e) {
            throw new BusinessException("获取模板文件出错：" + e.getMessage());
        }
    }

}
