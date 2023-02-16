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

}
