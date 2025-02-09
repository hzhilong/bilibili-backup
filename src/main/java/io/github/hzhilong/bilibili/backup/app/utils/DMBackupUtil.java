package io.github.hzhilong.bilibili.backup.app.utils;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.base.utils.FileUtil;
import io.github.hzhilong.bilibili.backup.api.bean.DM;
import io.github.hzhilong.bilibili.backup.api.bean.SimpleDM;
import io.github.hzhilong.bilibili.backup.api.bean.VideoPart;
import io.github.hzhilong.bilibili.backup.app.constant.AppConstant;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 弹幕备份工具
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class DMBackupUtil {

    public static final String VIDEO_PART_FILE_NAME = "video.part.json";
    public static final String DM_SOURCE_FILE_NAME = "dm.source.json";
    public static final String DM_PROCESSED_FILE_NAME = "dm.processed.json";
    public static final String INDEX_HTML_FILE_NAME = "index.html";

    public static String buildBackupPath(String bvid, int page, String videoPartName) {
        return String.format("%sdm/%s_%dP_%s_%s/", AppConstant.BACKUP_OTHER_PATH_PREFIX,
                bvid, page, videoPartName,
                new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()));
    }

    public static Template getHtmlTemplate() throws BusinessException {
        try {
            return TemplateUtil.getTemplate("template/DM.ftl");
        } catch (BusinessException e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("内部错误，读取index.html模板失败");
        }
    }

    public static String getCTimeStr(long progress) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(progress * 1000L);
    }

    public static String getProgressStr(int progress) {
        int sec = progress / 1000;
        if (sec < (59 * 60 + 59)) {
            return String.format("%02d:%02d", sec / 60, sec % 60);
        } else {
            return String.format("%02d:%02d:%02d", sec / 3600, (sec % 3600) / 60, (sec % 3600) % 60);
        }
    }

    public static void createHtmlFile(Template template, VideoPart part, List<SimpleDM> processedList, String backupPath) throws BusinessException {
        createHtmlFile(template, part, processedList, backupPath, false);
    }

    public static void createHtmlFile(Template template, VideoPart part, List<SimpleDM> processedList, String backupPath, boolean showLog) throws BusinessException {
        createHtmlFile(template, part, processedList, backupPath, INDEX_HTML_FILE_NAME, showLog);
    }

    public static void createHtmlFile(Template template, VideoPart part, List<SimpleDM> processedList, String backupPath, String fileName, boolean showLog) throws BusinessException {
        if (showLog) {
            log.info("正在生成html文件...");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("part", part);
        data.put("list", processedList);
        try {
            File file;
            if (!backupPath.endsWith(File.separator)) {
                file = new File(backupPath + File.separator + fileName);
            } else {
                file = new File(backupPath + fileName);
            }
            Writer writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(file.toPath()), "UTF-8"));
            template.process(data, writer);
            writer.close();
            if (showLog) {
                log.info("生成html文件成功");
            }
        } catch (IOException | TemplateException e) {
            throw new BusinessException("备份失败，生成文件出错：" + e.getMessage());
        }
    }

    public static List<SimpleDM> processList(List<DM.DanmakuElem> danmakuElemList, Crc32Cracker crc32Cracker) {
        log.info("正在处理弹幕数据...");
        danmakuElemList.sort(Comparator.comparingInt(DM.DanmakuElem::getProgress));
        List<SimpleDM> processedList = new ArrayList<>(danmakuElemList.size());
        log.info("正在反查弹幕发送者uid...");
        for (DM.DanmakuElem dm : danmakuElemList) {
            SimpleDM processedDM = new SimpleDM(getProgressStr(dm.getProgress()), dm.getContent(), getCTimeStr(dm.getCtime()), dm.getWeight(), dm.getMidHash());
            processedDM.setUids(crc32Cracker.crack(Long.parseLong(processedDM.getMidHash(), 16)));
            processedList.add(processedDM);
        }
        return processedList;
    }

    public static void backup(Template template, VideoPart part, List<DM.DanmakuElem> danmakuElemList, String backupPath, Crc32Cracker crc32Cracker) throws BusinessException {
        List<SimpleDM> processedList = processList(danmakuElemList, crc32Cracker);
        log.info("正在生成备份文件...");
        FileUtil.writeJsonFile(backupPath, VIDEO_PART_FILE_NAME, part);
        FileUtil.writeJsonFile(backupPath, DM_SOURCE_FILE_NAME, danmakuElemList);
        FileUtil.writeJsonFile(backupPath, DM_PROCESSED_FILE_NAME, processedList);
        createHtmlFile(template, part, processedList, backupPath);
        log.info("生成文件成功");
        log.info("生成目录 {}", new File(backupPath).getAbsolutePath());
        log.info("　【{}】：视频分P信息", VIDEO_PART_FILE_NAME);
        log.info("　【{}】：弹幕源数据", DM_SOURCE_FILE_NAME);
        log.info("　【{}】：已处理的弹幕数据", DM_PROCESSED_FILE_NAME);
        log.info("　【{}】：弹幕查看的页面（一般打开这个文件就够了）", INDEX_HTML_FILE_NAME);
    }

}
