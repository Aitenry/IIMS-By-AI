package com.toryu.iims.integral.controller;

import com.toryu.iims.integral.model.dto.dict.DictDTO;
import com.toryu.iims.integral.model.dto.dict.DictPageQueryDTO;
import com.toryu.iims.integral.model.dto.dict.DictValueDTO;
import com.toryu.iims.integral.model.dto.dict.DictValuePageQueryDTO;
import com.toryu.iims.integral.model.vo.dict.DictValueVO;
import com.toryu.iims.integral.service.DictService;
import com.toryu.iims.common.enums.IntegralDictType;
import com.toryu.iims.common.result.PageResult;
import com.toryu.iims.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/iims/dictionary")
@Api(tags = "文章")
@Slf4j
public class DictController {

    private final DictService dictService;

    public DictController(DictService dictService) {
        this.dictService = dictService;
    }

    @GetMapping("/category/list")
    @ApiOperation("获取分类列表")
    public Result<List<DictValueVO>> categoryList() {
        return Result.success(dictService.getDictValueListByType(IntegralDictType.CATEGORY));
    }

    @GetMapping("/tags/list")
    @ApiOperation("获取标签列表")
    public Result<List<DictValueVO>> tagList() {
        return Result.success(dictService.getDictValueListByType(IntegralDictType.TAG));
    }

    @PostMapping("/list")
    @ApiOperation("获取字典列表")
    public Result<PageResult> list(@RequestBody DictPageQueryDTO dictPageQueryDto) {
        return Result.success(dictService.dictPageQuery(dictPageQueryDto));
    }

    @GetMapping("/disable/{id}/{isDisable}")
    @ApiOperation("是否禁用字典")
    public Result<T> disableDict(@PathVariable Long id, @PathVariable Boolean isDisable) {
        return Result.fromBoolean(dictService.disableDict(id, isDisable));
    }

    @PostMapping("/add")
    @ApiOperation("添加字典")
    public Result<T> addDict(@RequestBody DictDTO dictDto) {
        return Result.fromBoolean(dictService.insertDict(dictDto));
    }

    @PostMapping("/delete")
    @ApiOperation("删除字典")
    public Result<T> deleteDict(@RequestBody List<Long> ids) {
        return Result.fromBoolean(dictService.delDict(ids));
    }

    @PostMapping("/update")
    @ApiOperation("修改字典")
    public Result<T> updateDict(@RequestBody DictDTO dictDto) {
        return Result.fromBoolean(dictService.updateDict(dictDto));
    }

    @PostMapping("/value/list/{dictId}")
    @ApiOperation("获取字典列表")
    public Result<PageResult> valueList(@PathVariable Long dictId, @RequestBody DictValuePageQueryDTO dictValuePageQueryDto) {
        return Result.success(dictService.dictValuePageQuery(dictId, dictValuePageQueryDto));
    }

    @PostMapping("/value/add")
    @ApiOperation("添加字典值")
    public Result<T> addDictValue(@RequestBody DictValueDTO dictValueDto) {
        return Result.fromBoolean(dictService.insertDictValue(dictValueDto));
    }

    @PostMapping("/value/update")
    @ApiOperation("修改字典值")
    public Result<T> updateDictValue(@RequestBody DictValueDTO dictValueDto) {
        return Result.fromBoolean(dictService.updateDictValue(dictValueDto));
    }

    @PostMapping("/value/delete")
    @ApiOperation("删除字典值")
    public Result<T> deleteDictValue(@RequestBody List<Long> ids) {
        return Result.fromBoolean(dictService.delDictValue(ids));
    }

}
