package top.ybgnb.bilibili.backup;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;
import top.ybgnb.bilibili.backup.app.BilibiliBackup;
import top.ybgnb.bilibili.backup.app.BilibiliReadAllMsg;
import top.ybgnb.bilibili.backup.app.BilibiliRestore;
import top.ybgnb.bilibili.backup.bean.QRCode;
import top.ybgnb.bilibili.backup.bean.Upper;
import top.ybgnb.bilibili.backup.business.BuType;
import top.ybgnb.bilibili.backup.error.BusinessException;
import top.ybgnb.bilibili.backup.request.ThrottlingInterceptor;
import top.ybgnb.bilibili.backup.service.BangumiService;
import top.ybgnb.bilibili.backup.service.BlackService;
import top.ybgnb.bilibili.backup.service.FavCollectedService;
import top.ybgnb.bilibili.backup.service.FavOpusesService;
import top.ybgnb.bilibili.backup.service.FavoritesService;
import top.ybgnb.bilibili.backup.service.FollowingService;
import top.ybgnb.bilibili.backup.service.LoginService;
import top.ybgnb.bilibili.backup.service.ServiceBuilder;
import top.ybgnb.bilibili.backup.service.ToViewService;
import top.ybgnb.bilibili.backup.service.UserInfoCallback;
import top.ybgnb.bilibili.backup.user.User;
import top.ybgnb.bilibili.backup.utils.CookieUtil;
import top.ybgnb.bilibili.backup.utils.QRUtil;
import top.ybgnb.bilibili.backup.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/**
 * @ClassName CliApp
 * @Description
 * @Author hzhilong
 * @Time 2024/9/28
 * @Version 1.0
 */
@Slf4j
public class CLIApp {

    public static void main(String[] args) {
        String type;
        if (args != null && args.length == 1) {
            type = args[0];
        } else {
            type = "backup";
            log.info("参数错误，重置为默认");
        }
        try {
            startBusiness(BuType.parse(type));
        } catch (BusinessException e) {
            log.error(e.getMessage());
        }
    }

    public static void startBusiness(BuType buType) throws BusinessException {
        log.info("================【" + buType._getLogName() + "工具】================");
        String readJsonDir = null;
        Scanner sc = null;
        try {
            sc = new Scanner(System.in);
            if (BuType.RESTORE.equals(buType)) {
                File backupDir = new File(BilibiliBackup.PATH_PREFIX);
                if (backupDir.exists() && backupDir.isDirectory()) {
                    File[] userFiles = backupDir.listFiles();
                    if (userFiles != null && userFiles.length > 0) {
                        Arrays.sort(userFiles, Comparator.comparingLong(File::lastModified));
                        log.info(String.format("总共有%s个备份文件，请输入前面的数字选择对应的备份", userFiles.length));
                        for (int i = 0; i < userFiles.length; i++) {
                            log.info(String.format("[%s]-[%s]", i, userFiles[i].getName()));
                        }
                        int pos;
                        while (true) {
                            pos = Integer.parseInt(sc.nextLine());
                            if (pos < 0 || pos > userFiles.length - 1) {
                                log.info("输入错误，请重新输入");
                            } else {
                                break;
                            }
                        }
                        readJsonDir = BilibiliBackup.PATH_PREFIX + userFiles[pos].getName() + File.separator;
                    }
                }
                if (StringUtils.isEmpty(readJsonDir)) {
                    throw new BusinessException("备份文件为空");
                }
            }

            String cookie = getCookie(buType, sc);
            // 开始处理
            List<ServiceBuilder> services = getServices(buType, sc);
            startBusiness(buType, cookie, readJsonDir, services);
        } finally {
            if (sc != null) {
                sc.close();
            }
        }
    }

    private static List<ServiceBuilder> getServices(BuType buType, Scanner sc) {
        List<ServiceBuilder> builders = new ArrayList<>();
        if (BuType.BACKUP.equals(buType) || BuType.RESTORE.equals(buType)) {
            chooseService(sc, builders, "关注", FollowingService::new);
            chooseService(sc, builders, "我的追番/追剧", BangumiService::new);
            chooseService(sc, builders, "收藏的专栏", FavOpusesService::new);
            chooseService(sc, builders, "收藏的视频合集", FavCollectedService::new);
            chooseService(sc, builders, "稍后再看", ToViewService::new);
            chooseService(sc, builders, "收藏夹", FavoritesService::new);
            chooseService(sc, builders, "黑名单", BlackService::new);
        }
        return builders;
    }

    private static void chooseService(Scanner sc, List<ServiceBuilder> builders, String name, ServiceBuilder builder) {
        log.info("是否包括[{}]？", name);
        log.info("输入 y:是\t其他:否");
        String nextLine = sc.nextLine();
        if ("Y".equals(nextLine) || "y".equals(nextLine)) {
            builders.add(builder);
        }
    }

    private static String getCookie(BuType type, Scanner sc) throws BusinessException {
        try {
            CookieUtil.Cookie cookie = CookieUtil.read(type);
            log.info("是否使用上次登录的用户：" + cookie.getUser().getName());
            log.info("输入Y：使用该用户；  输入其他：不使用");
            String nextLine = sc.nextLine();
            if ("Y".equals(nextLine) || "y".equals(nextLine)) {
                return cookie.getCookie();
            }
        } catch (BusinessException e) {
        }
        return loginNewUser();
    }

    private static String loginNewUser() throws BusinessException {
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(
                new ThrottlingInterceptor(1000)).build();
        LoginService loginService = new LoginService(client);
        log.info("正在获取登录二维码（长时间不显示的话，可按一下方向键下刷新屏幕）...");
        log.info("可尝试按住【Ctrl+鼠标滚轮键】调整字体为较小状态（方便展示二维码）");
        QRCode qrCode = loginService.generateQRCode();
        log.info("请使用手机哔哩哔哩扫码登录：");
        QRUtil.printQR(qrCode.getUrl());
        log.info("请使用手机哔哩哔哩扫码登录：");
        long startTime = System.currentTimeMillis();
        String cookie = null;
        while (System.currentTimeMillis() - startTime < 180000 && StringUtils.isEmpty(cookie)) {
            cookie = loginService.login(qrCode);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
        }
        if (StringUtils.isEmpty(cookie)) {
            throw new BusinessException("扫码超时");
        }
        log.info("登录成功");
        return cookie;
    }

    @NotNull
    private static void startBusiness(BuType buType, String cookie, String readJsonDir, List<ServiceBuilder> builders) throws BusinessException {
        String buName = buType._getLogName();
        log.info(String.format("开始%s", buName));
        Upper upper = null;

        UserInfoCallback userInfoCallback = new UserInfoCallback() {
            @Override
            public void success(Upper currUser) throws BusinessException {
                CookieUtil.save(new CookieUtil.Cookie(buType, currUser, cookie));
            }

            @Override
            public void fail(User user) {
                CookieUtil.delete(buType);
            }
        };
        if (BuType.BACKUP.equals(buType)) {
            upper = new BilibiliBackup(builders,new User(cookie), userInfoCallback).start();
        } else if (BuType.RESTORE.equals(buType)) {
            if (StringUtils.isEmpty(readJsonDir)) {
                throw new BusinessException("备份文件为空");
            }
            upper = new BilibiliRestore(builders,readJsonDir, new User(cookie), userInfoCallback).start();
        } else if (BuType.READ_ALL_MSG.equals(buType)) {
            upper = new BilibiliReadAllMsg(new User(cookie), userInfoCallback).start();
        }
        log.info(String.format("成功%s[%s]", buName, upper.getName()));
    }


}
