package com.gdczhl.saas.utils;

import com.gdczhl.saas.entity.SignInTask;
import com.gdczhl.saas.enums.EResultCode;
import com.gdczhl.saas.vo.ResponseVo;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

//签到任务工具类
public class SignTasks {

//    /**
//     * 解析任务名称
//     *
//     * @param taskNameBo
//     * @return
//     */
//    public static SignInTask parseTaskNameResult(String taskNameBo) {
//        if (Objects.isNull(taskNameBo)) {
//            return null;
//        }
//        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
//        SignInTask signInTask = new SignInTask();
//        if (Objects.nonNull(dateTimeFormatter)) {
//            TimeUtil.setLocalTimeUtil(dateTimeFormatter);
//        }
//        String[] names = taskNameBo.split("\\(");
//        signInTask.setName(names[0]);
//        signInTask.setTaskName(names[1]);
//        String[] times = names[2].split("\\)")[0].split("-");
//        signInTask.setTaskStartTime(TimeUtil.parseLocalTime(times[0]));
//        signInTask.setTaskEndTime(TimeUtil.parseLocalTime(times[1]));
//        return signInTask;
//    }


    /**
     * 检查响应信息
     *
     * @param responseVo
     * @param <T>
     * @return
     */
    public static <T> T checkHttpResponse(ResponseVo<T> responseVo) {
        if (responseVo.getCode() == EResultCode.SUCCESS.getCode()) {
            return responseVo.getData();
        }
        throw new RuntimeException("连接超时");
    }

    /**
     * 格式化 任务名称
     *
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

    public static String getTaskNameResult(SignInTask signInTask) {
        String startTime = TimeUtil.format(signInTask.getTaskStartTime());
        String endTime = TimeUtil.format(signInTask.getTaskEndTime());
        StringBuilder builder = new StringBuilder();
        return builder.append(signInTask.getName()).append("(").append(signInTask.getTaskName()).append("(").append(startTime).append("-").append(endTime).append(")").append(")").toString();
    }

    public static String getPeriodNameResult(SignInTask signInTask) {
        TimeUtil.setLocalTimeUtil(DateTimeFormatter.ofPattern("HH:mm"));
        String startTime = TimeUtil.format(signInTask.getTaskStartTime());
        String endTime = TimeUtil.format(signInTask.getTaskEndTime());
        StringBuilder builder = new StringBuilder();
        return builder.append(signInTask.getTaskName()).append("(").append(startTime).append("-").append(endTime).append(")").toString();
    }

}
