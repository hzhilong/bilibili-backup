package io.github.hzhilong.bilibili.backup.gui.worker.tools;

import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.base.utils.StringUtils;
import io.github.hzhilong.baseapp.bean.NeedContext;
import io.github.hzhilong.bilibili.backup.api.bean.AnswerStatus;
import io.github.hzhilong.bilibili.backup.api.bean.Question;
import io.github.hzhilong.bilibili.backup.api.bean.QuestionCheckResult;
import io.github.hzhilong.bilibili.backup.api.user.User;
import io.github.hzhilong.bilibili.backup.app.bean.SavedUser;
import io.github.hzhilong.bilibili.backup.app.service.impl.AnswerService;
import io.github.hzhilong.bilibili.backup.app.state.setting.AppSettingItems;
import io.github.hzhilong.bilibili.backup.app.utils.AesCbcDecrypt;
import io.github.hzhilong.bilibili.backup.app.utils.OpenAIUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;

/**
 * 自动答题
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class AutoAnsweringRunnable extends ToolRunnable<AnswerService, Void> implements NeedContext {

    private AnswerService answerService;

    @Setter
    private Window parentWindow;
    @Setter
    private String appIconPath;

    public AutoAnsweringRunnable(OkHttpClient client, SavedUser user, ToolBuCallback<Void> buCallback) {
        super(client, user, buCallback);
    }

    @Override
    protected void newServices(LinkedHashSet<AnswerService> services) {
        answerService = new AnswerService(client, new User(user.getCookie()));
        services.add(answerService);
    }

    @Override
    protected Void runTool() throws BusinessException {
        int result = JOptionPane.showConfirmDialog(parentWindow,
                "当前功能开发中，未经过多个账号测试。\n可能会因为出现多次验证码导致账号高危（需要实名解除）。\n是否继续？", "提示",
                JOptionPane.YES_NO_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            throw new BusinessException("取消操作");
        }

        log.info("测试AI接口...【请回答我1（不要有其他内容）】");
        String testResult = OpenAIUtils.chat(client, "请回答我1（不要有其他内容）");
        log.info("模型测试结果：{}", testResult);
        if (!"1".equals(testResult)) {
            throw new BusinessException("该模型可能无法正常回答问题，请更换其他模型。");
        }

        int totalNum = 0;
        int successNum = 0;
        int oneTimeSuccessNum = 0;

        while (true) {
            log.info("\n------------------------");
            log.info("正在查询答题状态...");
            if (totalNum > 0) {
                randomSleep(3000, 5000);
            }
            handleInterrupt();
            AnswerStatus answerStatus = answerService.getAnswerStatus();
            if (!"v4".equals(answerStatus.getVersion())) {
                throw new BusinessException("软件只支持v4版本的API，当前接口版本：" + answerStatus.getVersion());
            }

            log.info("当前答题状态：{}", answerStatus.getText());
            log.info("当前答题进度：{}%", answerStatus.getProgress());
            log.info("当前答题得分：{}", answerStatus.getScore());
            log.info("当前答题题号：{}", answerStatus.getNumber() + 1);
            log.info("当前答题阶段：{}", answerStatus._getStageDesc());

            if (answerStatus.getStatus() == 3) {
                log.info("结束任务：当前答题已通过");
                break;
            } else if ("complete".equals(answerStatus.getStage())) {
                log.info("结束任务：当前答题已完成");
                break;
            } else if ("pro_type".equals(answerStatus.getStage()) || "pro".equals(answerStatus.getStage())) {
                log.info("结束任务：当前答题正处于【{}】状态", answerStatus._getStageDesc());
                break;
            } else if (AppSettingItems.AUTO_SUBMIT_ANSWER.getValue() && answerStatus.getScore() >= 60) {
                log.info("已达到60分，提交试卷中...");
                randomSleep(1000, 1000);
                handleInterrupt();
                answerService.submit();
                log.info("提交成功");
                break;
            }
            log.info("拉取题目中...");
            randomSleep(2000, 3000);
            handleInterrupt();
            Question question = answerService.pullQuestion(answerStatus);
            log.info("解密题目中...");
            String title;
            java.util.List<String> answers = new ArrayList<>();
            try {
                title = AesCbcDecrypt.decryptBiliAnswer(question.getQuestion().getTitle());
                answers = AesCbcDecrypt.decryptBiliAnswers(question.getQuestion().getAns());
            } catch (Exception e) {
                throw new BusinessException("解密题目出错：" + e.getMessage());
            }

            log.info("题目：{}", title);
            log.info("选项：{}", answers);

            log.info("调用AI回答...");
            handleInterrupt();
            int answerIndex = OpenAIUtils.askForAnswers(client, title, answers);
            log.info("AI选择的答案：{}", answers.get(answerIndex));
            log.info("答题中...");

            totalNum++;
            randomSleep(3000, 6000);
            handleInterrupt();
            QuestionCheckResult checkResult = answerService.checkQuestion(answerStatus.getStage(), question.getId(), question.getOptions().get(answerIndex).getHash());
            if (checkResult.isPassed()) {
                log.info("答题正确");
                successNum++;
                oneTimeSuccessNum++;
            } else {
                log.info("答题错误");
                if (StringUtils.notEmpty(checkResult.getAns_right())) {
                    log.info("平台已给出正确答题，重新提交中...");
                    randomSleep(2000, 4000);
                    handleInterrupt();
                    checkResult = answerService.checkQuestion(answerStatus.getStage(), question.getId(), checkResult.getAns_right());
                    if (checkResult.isPassed()) {
                        log.info("答题正确");
                        successNum++;
                    } else {
                        log.info("答题错误，未知原因");
                    }
                }
            }
        }

        log.info("任务已结束，本次答题次数：{}，成功次数：{}，一次成功的次数：{}", totalNum, successNum, oneTimeSuccessNum);
        return null;
    }

}
