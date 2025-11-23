package com.toryu.iims.integral.controller;

import com.toryu.iims.common.result.PageResult;
import com.toryu.iims.common.result.Result;
import com.toryu.iims.integral.model.dto.log.LogPageQueryDTO;
import com.toryu.iims.integral.service.LogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Aitenry
 * @Date: 2025/11/23 16:02
 * @Version: v1.0.0
 * @Description: TODO
 **/
@RestController
@RequestMapping("/iims/log")
@Api(tags = "日志")
@Slf4j
public class LogController {

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @PostMapping("/page")
    @ApiOperation("菜单分页查询")
    public Result<PageResult> page(@RequestBody LogPageQueryDTO logPageQueryDTO) {
        PageResult pageResult = logService.pageQuery(logPageQueryDTO);
        return Result.success(pageResult);
    }

}
