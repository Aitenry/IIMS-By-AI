package com.toryu.iims.integral.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.toryu.iims.integral.mapper.DictMapper;
import com.toryu.iims.integral.model.dto.dict.DictDTO;
import com.toryu.iims.integral.model.dto.dict.DictPageQueryDTO;
import com.toryu.iims.integral.model.dto.dict.DictValueDTO;
import com.toryu.iims.integral.model.dto.dict.DictValuePageQueryDTO;
import com.toryu.iims.integral.model.vo.dict.DictValueVO;
import com.toryu.iims.integral.model.vo.dict.DictVO;
import com.toryu.iims.integral.service.DictService;
import com.toryu.iims.common.enums.IntegralDictType;
import com.toryu.iims.common.model.entity.integral.Dict;
import com.toryu.iims.common.model.entity.integral.DictValue;
import com.toryu.iims.common.model.entity.status.DeletedStatus;
import com.toryu.iims.common.model.entity.status.DisableStatus;
import com.toryu.iims.common.result.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class DictServiceImpl implements DictService {

    private final DictMapper dictMapper;

    public DictServiceImpl(DictMapper dictMapper) {
        this.dictMapper = dictMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertDict(DictDTO dictDto) {
        Dict dict = Dict.builder().isCanChange(dictDto.getIsCanChange())
                .remark(dictDto.getRemark()).name(dictDto.getName())
                .isDeleted(false).isDisable(false).build();
        return dictMapper.insertDict(dict);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertDictValue(DictValueDTO dictValueDto) {
        DictValue dict = DictValue.builder().dictId(dictValueDto.getDictId())
                .remark(dictValueDto.getRemark()).value(dictValueDto.getValue())
                .isDeleted(false).build();
        return dictMapper.insertDictValue(dict);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delDict(List<Long> ids) {
        DeletedStatus build = DeletedStatus.builder().ids(ids).isDeleted(true).build();
        return dictMapper.updateDictDeleted(build);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delDictValue(List<Long> ids) {
        DeletedStatus build = DeletedStatus.builder().ids(ids).isDeleted(true).build();
        return dictMapper.updateDictValueDeleted(build);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateDict(DictDTO dictDto) {
        Dict dict = Dict.builder().id(dictDto.getId()).isCanChange(dictDto.getIsCanChange())
                .remark(dictDto.getRemark()).name(dictDto.getName()).build();
        return dictMapper.updateDict(dict);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateDictValue(DictValueDTO dictValueDto) {
        DictValue dict = DictValue.builder().id(dictValueDto.getId()).value(dictValueDto.getValue())
                .remark(dictValueDto.getRemark()).build();
        return dictMapper.updateDictValue(dict);
    }

    @Override
    public PageResult dictPageQuery(DictPageQueryDTO dictPageQueryDto) {
        int page = dictPageQueryDto.getPage();
        int pageSize = dictPageQueryDto.getPageSize();
        PageHelper.startPage(page, pageSize);
        Page<DictVO> dictVOS = dictMapper.dictPageQuery(dictPageQueryDto);
        long total = dictVOS.getTotal();
        List<DictVO> records = dictVOS.getResult();
        return new PageResult(total, records);
    }

    @Override
    public PageResult dictValuePageQuery(Long dictId, DictValuePageQueryDTO dictValuePageQueryDto) {
        int page = dictValuePageQueryDto.getPage();
        int pageSize = dictValuePageQueryDto.getPageSize();
        PageHelper.startPage(page, pageSize);
        Page<DictValueVO> dictVos = dictMapper.dictValuePageQuery(dictId, dictValuePageQueryDto);
        long total = dictVos.getTotal();
        List<DictValueVO> records = dictVos.getResult();
        return new PageResult(total, records);
    }

    @Override
    public DictValue getDictValueById(Long id) {
        return dictMapper.getDictValueById(id);
    }

    @Override
    public List<DictValue> getDictValueByIds(List<Long> ids) {
        return dictMapper.getDictValueByIds(ids);
    }

    @Override
    public List<DictValueVO> getDictValueListByType(IntegralDictType integralDictType) {
        return dictMapper.getDictValueListByDictId(integralDictType.getDictId());
    }

    @Override
    public Boolean disableDict(Long id, Boolean isDisable) {
        return dictMapper.disableDict(DisableStatus.builder().id(id).isDisable(isDisable).build());
    }
}
