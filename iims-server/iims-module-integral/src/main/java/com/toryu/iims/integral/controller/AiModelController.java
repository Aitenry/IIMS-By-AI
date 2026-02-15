package com.toryu.iims.integral.controller;

import com.toryu.iims.ai.chat.model.dto.CreateModelDTO;
import com.toryu.iims.ai.chat.model.dto.ModelPageQueryDTO;
import com.toryu.iims.ai.chat.model.dto.UpdateModelDTO;
import com.toryu.iims.common.result.PageResult;
import com.toryu.iims.common.result.Result;
import com.toryu.iims.integral.service.AiModelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/del/{id}")
    public Result<T> deleteModel(@PathVariable Long id) {
        return Result.fromBoolean(aiModelService.deleteModel(id));
    }

    @PostMapping("/update")
    private Result<T> updateModel(@RequestBody UpdateModelDTO model) {
        return Result.fromBoolean(aiModelService.updateModel(model));
    }

    @PostMapping("/create")
    private Result<T> createModel(@RequestBody CreateModelDTO model) {
        return Result.fromBoolean(aiModelService.createModel(model));
    }

}
