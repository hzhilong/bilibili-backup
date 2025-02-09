package io.github.hzhilong.bilibili.backup.gui.worker.tools;

import com.alibaba.fastjson.JSONObject;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.base.utils.FileUtil;
import io.github.hzhilong.base.utils.ListUtil;
import io.github.hzhilong.base.utils.StringUtils;
import io.github.hzhilong.baseapp.bean.NeedContext;
import io.github.hzhilong.bilibili.backup.api.bean.DM;
import io.github.hzhilong.bilibili.backup.api.bean.SimpleDM;
import io.github.hzhilong.bilibili.backup.api.bean.VideoPart;
import io.github.hzhilong.bilibili.backup.api.user.User;
import io.github.hzhilong.bilibili.backup.app.bean.SavedUser;
import io.github.hzhilong.bilibili.backup.app.service.BaseService;
import io.github.hzhilong.bilibili.backup.app.service.impl.VideoService;
import io.github.hzhilong.bilibili.backup.app.utils.Crc32Cracker;
import io.github.hzhilong.bilibili.backup.app.utils.DMBackupUtil;
import io.github.hzhilong.bilibili.backup.gui.frame.ViewDMFrame;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * 备份弹幕的线程
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class BackupDMRunnable extends ToolRunnable<BaseService, Void> implements NeedContext {

    @Setter
    private Window parentWindow;
    @Setter
    private String appIconPath;

    private VideoService videoService;

    private Crc32Cracker cracker = null;

    public BackupDMRunnable(OkHttpClient client, SavedUser user, ToolBuCallback<Void> buCallback) {
        super(client, user, buCallback);
    }

    @Override
    protected void newServices(LinkedHashSet<BaseService> services) {
        videoService = new VideoService(client, new User(user.getCookie()));
        services.add(videoService);
    }

    @Override
    protected Void runTool() throws BusinessException {
        String bvid = JOptionPane.showInputDialog(parentWindow, "请输入视频BV号：",
                "提示", JOptionPane.QUESTION_MESSAGE);
        if (StringUtils.isEmpty(bvid)) {
            throw new BusinessException("未输入视频BV号，取消操作");
        }
        long start = System.currentTimeMillis();
        log.info("正在获取视频分P信息...");
        List<VideoPart> parts = videoService.getParts(bvid);
        if (ListUtil.isEmpty(parts)) {
            throw new BusinessException("BV号错误，查询cid失败");
        }
        if (parts.size() > 1) {
            log.info("存在{}个分P视频", parts.size());
            log.info("开始获取各个分P视频的弹幕...");
        } else {
            log.info("该视频无分P");
        }
        Template template = DMBackupUtil.getHtmlTemplate();
        boolean isFirst = true;
        for (VideoPart part : parts) {
            if (isFirst) {
                log.info("正在初始化...");
                cracker = new Crc32Cracker();
                sleep(2);
            }
            handleInterrupt();
            part.setBvid(bvid);
            String videoPartName = part.getPart();
            log.info("正在获取[{}]弹幕...", videoPartName);
            List<DM.DanmakuElem> danmakuElemList = videoService.getDM(part);
            log.info("已获取{}条弹幕", ListUtil.getSize(danmakuElemList));
            String backupPath = DMBackupUtil.buildBackupPath(bvid, part.getPage(), videoPartName);
            DMBackupUtil.backup(template, part, danmakuElemList, backupPath, cracker);
            if (isFirst) {
                try {
                    Desktop.getDesktop().open(new File(backupPath));
                } catch (IOException ignored) {
                }
            }
            isFirst = false;
        }
        log.info("\n");
        log.info("备份视频弹幕成功，可直接打开【index.html】文件查看");
        log.info("如果需要反查弹幕发送者，并快速过滤不存在的uid，请使用工具【{}】", ViewDMFrame.NAME);
        log.info("\n");
        long end = System.currentTimeMillis();
        log.info("操作耗时：{}秒", (end - start) / 1000f);
        return null;
    }

    public static void main(String[] args) throws Exception {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_34);
        cfg.setClassForTemplateLoading(BackupDMRunnable.class, "/");
        Template template = cfg.getTemplate("template/DM.ftl");
        String listJson = FileUtil.readJsonFile("bin/test/", DMBackupUtil.DM_PROCESSED_FILE_NAME);
        List<SimpleDM> list = JSONObject.parseArray(listJson, SimpleDM.class);
//        Crc32Cracker cracker = new Crc32Cracker();
//        for (SimpleDM dm : list) {
//            List<Long> uids = cracker.crack(Long.parseLong(dm.getMidHash(), 16));
//            dm.setUids(uids);
//        }
//        FileUtil.writeJsonFile("bin/test/", "dm.processed2.json", list);
        String partJson = FileUtil.readJsonFile("bin/test/", DMBackupUtil.VIDEO_PART_FILE_NAME);
        VideoPart part = JSONObject.parseObject(partJson, VideoPart.class);
        Map<String, Object> data = new HashMap<>();
        data.put("part", part);
        data.put("list", list);
        // 定义输出
        try {
            Writer writer = new FileWriter("bin/test/" + DMBackupUtil.INDEX_HTML_FILE_NAME);
            template.process(data, writer);
            writer.close();
        } catch (IOException | TemplateException e) {
            throw new BusinessException("备份失败，生成文件出错：" + e.getMessage());
        }
    }
}
