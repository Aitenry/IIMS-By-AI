package com.toryu.iims.integral.controller;

import com.toryu.iims.common.result.PageResult;
import com.toryu.iims.common.result.Result;
import com.toryu.iims.integral.model.dto.llm.ModelPageQueryDTO;
import com.toryu.iims.integral.service.AiModelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Aitenry
 * @Date: 2025/11/23 16:57
 * @Version: v1.0.0
 * @Description: TODO
 **/
@RestController
@RequestMapping("/iims/model")
@Api(tags = "模型")
@Slf4j
public class AiModelController {

    private final AiModelService aiModelService;

    public AiModelController(AiModelService aiModelService) {
        this.aiModelService = aiModelService;
    }

    @PostMapping("/page")
    @ApiOperation("菜单分页查询")
    public Result<PageResult> page(@RequestBody ModelPageQueryDTO modelPageQueryDTO) {
        PageResult pageResult = aiModelService.pageQuery(modelPageQueryDTO);
        return Result.success(pageResult);
    }

}
