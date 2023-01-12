package com.gdczhl.saas.controller.external;

import com.gdczhl.saas.service.ISignStatisticsService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author hkx
 * @since 2023-01-05
 */
@Api(tags = "签到统计")
@RestController
@RequestMapping("external/signStatistics")
public class SignStatisticsController {

    @Autowired
    private ISignStatisticsService signStatisticsService;
}
