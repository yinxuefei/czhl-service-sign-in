package com.gdczhl.saas.service;

import com.gdczhl.saas.controller.external.pojo.vo.SignInRecordPageVo;
import com.gdczhl.saas.entity.SignInRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gdczhl.saas.vo.PageVo;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hkx
 * @since 2023-01-06
 */
public interface ISignInRecordService extends IService<SignInRecord> {

    PageVo<SignInRecordPageVo> getSignRecordPage(LocalDate date, String taskName, String name, String address,
                                                 Integer pageNo,
                                                Integer pageSize);
}
