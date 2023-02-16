package com.gdczhl.saas.utils;

import com.gdczhl.saas.entity.SignInTask;
import com.gdczhl.saas.enums.EResultCode;
import com.gdczhl.saas.vo.ResponseVo;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

//签到任务工具类
public class SignTasks {

    /**
     * 检查响应信息
     * @param responseVo
     * @param <T> 返回响应data
     * @return
     */
    public static <T> T checkHttpResponse(ResponseVo<T> responseVo) {
        if (responseVo.getCode() == EResultCode.SUCCESS.getCode()) {
            return responseVo.getData();
        }
        throw new RuntimeException("响应状态:[Fail]");
    }

    /**
     * 格式化 任务名称
     * @param dateTimeFormatter
     * @return
     */
    public static String getTaskNameResult(SignInTask signInTask,
                                           DateTimeFormatter dateTimeFormatter) {

        TimeUtil.setLocalTimeUtil(dateTimeFormatter);
        String startTime = TimeUtil.format(signInTask.getTaskStartTime());
        String endTime = TimeUtil.format(signInTask.getTaskEndTime());
        StringBuilder builder = new StringBuilder();
        return builder.append(signInTask.getName()).append("(").append(signInTask.getTaskName()).append("(").append(startTime).append("-").append(endTime).append(")").append(")").toString();
    }

    /**
     * 获取任务全名称
     * @param signInTask
     * @return
     */
    public static String getTaskNameResult(SignInTask signInTask) {
        String startTime = TimeUtil.format(signInTask.getTaskStartTime());
        String endTime = TimeUtil.format(signInTask.getTaskEndTime());
        StringBuilder builder = new StringBuilder();
        return builder.append(signInTask.getName()).append("(").append(signInTask.getTaskName()).append("(").append(startTime).append("-").append(endTime).append(")").append(")").toString();
    }

    /**
     * 获取时段全名称
     * @param signInTask
     * @return
     */
    public static String getPeriodNameResult(SignInTask signInTask) {
        TimeUtil.setLocalTimeUtil(DateTimeFormatter.ofPattern("HH:mm"));
        String startTime = TimeUtil.format(signInTask.getTaskStartTime());
        String endTime = TimeUtil.format(signInTask.getTaskEndTime());
        StringBuilder builder = new StringBuilder();
        return builder.append(signInTask.getTaskName()).append("(").append(startTime).append("-").append(endTime).append(")").toString();
    }

}
