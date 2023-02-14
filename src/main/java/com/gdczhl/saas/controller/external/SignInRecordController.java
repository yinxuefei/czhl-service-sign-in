package com.gdczhl.saas.controller.external;

import com.gdczhl.saas.controller.external.vo.record.SignInRecordPageVo;
import com.gdczhl.saas.service.ISignInRecordService;
import com.gdczhl.saas.service.IUserService;
import com.gdczhl.saas.vo.PageVo;
import com.gdczhl.saas.vo.ResponseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author hkx
 * @since 2023-01-05
 */
@Api(tags = "签到记录查询")
@RestController
@RequestMapping("external/signInRecord")
public class SignInRecordController {

    @Autowired
    private ISignInRecordService signInRecordService;
    @Autowired
    private IUserService userService;


    @GetMapping("page")
    @ApiOperation("分页查询")
    public ResponseVo<PageVo<SignInRecordPageVo>> getSignRecordPage(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                                                    @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                                    @ApiParam("任务uuid") String taskUuid,
                                                                    @ApiParam("用户姓名") String username,
                                                                    @ApiParam("场地code") String areaCode,
                                                                    @ApiParam("分页") @RequestParam(defaultValue = "1") Integer pageNo,
                                                                    @ApiParam("分页大小") @RequestParam(defaultValue = "20") Integer pageSize) {

        PageVo<SignInRecordPageVo> signRecordPage = signInRecordService.getSignRecordPage(startDate, endDate, taskUuid
                , username, areaCode, pageNo, pageSize);
        return ResponseVo.success(signRecordPage);
    }

//
//    @GetMapping("exportExcel")
//    @ApiOperation("表格导出")
//    public void ExportExcel() {
////        String fileName = "";
////
////        File file = FileUtil.file(fileName);
////
////        System.out.println(fileName);
////        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为用户表 然后文件流会自动关闭
////        EasyExcel.write(file, User.class).sheet("用户表").doWrite(data());
//
//        // 方法1 使用已有的策略 推荐
//        // HorizontalCellStyleStrategy 每一行的样式都一样 或者隔行一样
//        // AbstractVerticalCellStyleStrategy 每一列的样式都一样 需要自己回调每一页
//        String fileName = TestFileUtil.getPath() + "handlerStyleWrite" + System.currentTimeMillis() + ".xlsx";
//        // 头的策略
//        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
//        // 背景设置为红色
//        headWriteCellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
//        WriteFont headWriteFont = new WriteFont();
//        headWriteFont.setFontHeightInPoints((short)20);
//        headWriteCellStyle.setWriteFont(headWriteFont);
//        // 内容的策略
//        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
//        // 这里需要指定 FillPatternType 为FillPatternType.SOLID_FOREGROUND 不然无法显示背景颜色.头默认了 FillPatternType所以可以不指定
//        contentWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
//        // 背景绿色
//        contentWriteCellStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
//        WriteFont contentWriteFont = new WriteFont();
//        // 字体大小
//        contentWriteFont.setFontHeightInPoints((short)20);
//        contentWriteCellStyle.setWriteFont(contentWriteFont);
//        // 这个策略是 头是头的样式 内容是内容的样式 其他的策略可以自己实现
//        HorizontalCellStyleStrategy horizontalCellStyleStrategy =
//        new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
//
////        fileName = TestFileUtil.getPath() + "simpleWrite" + System.currentTimeMillis() + ".xlsx";
////        // 这里 需要指定写用哪个class去写
//        ExcelWriter build = EasyExcel.write(fileName, User.class).build();
//        WriteSheet sheet = EasyExcel.writerSheet("模板").build();
//        build.write(data(),sheet);
//        }
//
//
//    public List<User> data() {
//        //查询用户表,具体service层实现就不写了
//        ArrayList<User> users = new ArrayList<>();
//        String[] mes = "一二三四五六七八九十".split("");
//        String[] nas = "王、李、张、刘、陈、杨、黄、赵、吴、周".split("、");
//        System.out.println(mes);
//        System.out.println(nas);
//        for (int i = 0; i < 10; i++) {
//            User user = new User();
//            user.setName(nas[i]+mes[i]);
//            user.setUserType(i);
//            user.setGender("男");
//            users.add(user);
//        }
//        return users;
//    }

}
