package com.gdczhl.saas.controller.external;

import com.alibaba.excel.EasyExcel;
import com.gdczhl.saas.controller.external.pojo.bo.User;
import com.gdczhl.saas.controller.external.pojo.vo.SignInRecordPageVo;
import com.gdczhl.saas.service.ISignInRecordService;
import com.gdczhl.saas.service.IUserService;
import com.gdczhl.saas.vo.PageVo;
import com.gdczhl.saas.vo.ResponseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  前端控制器
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
    public ResponseVo<PageVo<SignInRecordPageVo>>getSignRecordPage(@ApiParam("日期") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                                                   @ApiParam("签到任务") String taskName,
                                                                   @ApiParam("用户姓名") String name,
                                                                   @ApiParam("地点") String address,
                                                                   @ApiParam("分页") @RequestParam(defaultValue = "1") Integer pageNo,
                                                                   @ApiParam("分页大小") @RequestParam(defaultValue = "20")Integer pageSize) {
        signInRecordService.getSignRecordPage(date,taskName,name,address,pageNo,pageSize);
    return ResponseVo.success();
    }


    @GetMapping("exportExcel")
    @ApiOperation("表格导出")
    public void ExportExcel() {
        String fileName = "C:\\Users\\CH\\Desktop\\" + "User" + System.currentTimeMillis() + ".xlsx";
        System.out.println(fileName);
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为用户表 然后文件流会自动关闭
        EasyExcel.write(fileName, User.class).sheet("用户表").doWrite(data());
    }

    public List<User> data() {
        //查询用户表,具体service层实现就不写了
        List<com.gdczhl.saas.entity.User> list = userService.list();
        ArrayList<User> users = new ArrayList<>();
        BeanUtils.copyProperties(list,users);
        return users;
    }

}
