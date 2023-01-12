package com.gdczhl.saas.megbox;


import java.util.HashMap;
import java.util.Map;

public enum MegError implements IErrorCode {
    ERROR_OK(0, "正常"),
    ERROR_GENERAL(1, "通用错误"),
    ERROR_INVALID_PARAM(2, "无效参数，协议或接口的参数无效"),
    ERROR_MEMORY_OUT(3, "存储空间不足"),
    ERROR_UNACCESS(4, "资源无法访问"),
    ERROR_DISCONNECT(5, "断线"),
    ERROR_MULTIPLIE(6, "重复操作，如重复打开，重复关闭"),
    ERROR_NOT_SUPPORT(7, "不支持的方法"),
    ERROR_INVALID_RES(8, "资源无效，即资源不存在或异常"),
    ERROR_TIME_OUT(9, "访问超时"),
    ERROR_RES_INADEQUATE(10, "资源不足"),
    ERROR_RES_IS_EXIST(11, "资源/设备已经存在"),
    ERROR_INVALID_USER_OR_PWD(12, "无效用户名或密码"),
    ERROR_DEVICE_LOCKED(13, "设备被锁定"),
    ERROR_DEVICE_OVER_LIMIT(14, "超出设备规格"),
    ERROR_PROCESS_BUSY(15, "系统忙碌（达到处理能力上限）"),
    ERROR_USR_OVER_LIMIT(16, "用户数量超出上限"),
    ERROR_USR_IS_EXIST(17, "用户已经存在"),
    ERROR_ONLINE_USR_OVER_LIMIT(18, "在线用户数量超出上限"),
    ERROR_REMOTE_DEVICE_OFFLINE(19, "远程设备已下线"),
    ERROR_REMOTE_DEVICE_IS_EXIST(20, "远程设备已存在"),
    ERROR_REMOTE_DEVICE_NOT_EXIST(21, "远程设备不存在"),
    ERROR_DEVICE_INACTIVE(22, "设备未激活"),
    ERROR_DATA_LINK_DISCONNECT(23, "用户数据链路断开"),
    ERROR_BUFF_FULL(24, "缓存满"),
    ERROR_USR_ACTIVE_TERMINATE(25, "用户主动中止操作"),
    ERROR_VERSION_NOT_MATCH(26, "版本不匹配"),
    ERROR_STREAM_UNSUBSCRIBING(27, "流退订中"),

    ERROR_CH_NOT_EXIST(500, "通道号不存在"),
    ERROR_IMAGE(501, "解码失败"),
    ERRO_IMAGE_DECODE_FAIL(502, "图片不存在"),
    ERROR_IMAGE_NOT_FOUND(503, "图片不存在"),
    ERROR_MD5_CHECK_FAIL(503, "md5 校验失败"),
    ERROR_READ_FILE_FAIL(504, "读取文件失败"),
    ERROR_INVALID_RESOLUTION(505, "无效分辨率"),
    EROOR_INVALID_TYPE(506, "无效类型"),
    ERROR_FEATURE_VERSION_INCONSISTENT(507, "工作模式不匹配"),
    ERROR_WORK_MODE_NOT_MATCH(508, "工作模式不匹配"),
    ERROR_FIREWARE(509, "升级包文件异常"),
    ERROR_FIREWARE_RECV(510, "升级包文件接收错误"),
    ERROR_NOT_READY(511, "资源未准备好"),
    ERROR_SESSION_IS_NOT_FOUND(512, "session未找到"),

    ERROR_FACE_ABNORMAL_SIZE(10000, "人脸图片尺寸异常"),
    ERROR_FACE_NOT_DETECT(10001, "没有检测到人脸"),
    ERROR_BAD_QUALITY(10002, "人脸未通过质量判断"),
    ERROR_FACE_NOT_EXIST(10003, "人脸不存在"),
    ERROR_FACE_IS_EXIST(10004, "人脸已经存在"),
    ERROR_GROUP_NOT_EXIST(10005, "人员分组不存在"),
    ERROR_GROUP_IS_EXIST(10006, "人员分组已存在"),
    ERROR_GROUP_OVER_LIMIT(10007, "人员分组超出上限"),
    ERROR_GROUP_BIND_BIND_NO_EXIST(10008, "人员分组绑定关系不存在"),
    ERROR_GROUP_USED(10009, "人员分组正在使用，不能删除"),
    ERROR_GROUP_BIND_NOT_EXIST(10010, "人员分组绑定关系已存在"),
    ERROR_LIB_SYNC(10011, "人脸库同步中，请稍等"),
    ERROR_LIB_BUSY(10012, "人脸库导入中，请稍等"),
    ERROR_LIB_IMAGE_OVER_LIMIT(10013, "人脸库图片超过上限"),
    ERROR_LIB_TRANS_IMG_RETRY(10014, "人脸库入库，设备端缓存图片上限，需要重传"),
    ERROR_LIB_BATCH_IS_STOP(10015, "人脸库导入已经停止"),
    ERROR_LIB_BATCH_TIMEOUT(10016, "人脸库操作超时，设备端自动停止入库"),
    ERROR_PERSON_CONFLICT(10017, "人员信息冲突"),
    ERROR_PERSON_ID_CONFLICT(10018, "人员uuid冲突"),
    ERROR_PERSON_NAME_CONFLICT(10019, "人员名冲突"),
    ERROR_PERSON_CODE_CONFLICT(10020, "人员编号冲突"),
    ERROR_PERSON_CARD_ID_CONFLICT(10021, "人员门禁卡号冲突"),
    ERROR_SCHEDULE_PLAN_USED(20001, "时间计划正在使用中"),
    ERROR_SCHEDULE_PLAN_IS_EXIST(20002, "时间计划已存在"),
    ERROR_SCHEDULE_PLAN_OVER_LIMIT(20003, "时间计划数量超出规格上限"),
    ERROR_SCHEDLUE_PLAN_NOT_EXIST(20004, "时间计划不存在");

    private int code;
    private String msg;
    private static Map<Integer, MegError> valueMap;

    private MegError(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private static MegError[] values = null;

    public static MegError fromInt(int i) {
        if (MegError.values == null) {
            MegError.valueMap = new HashMap<Integer, MegError>();

            for(var value : MegError.values()) {
                valueMap.put(new Integer(value.getCode()), value);
            }
        }

        if (valueMap.containsKey(i)) {
            return valueMap.get(i);
        }

        return MegError.ERROR_GENERAL;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }
}
