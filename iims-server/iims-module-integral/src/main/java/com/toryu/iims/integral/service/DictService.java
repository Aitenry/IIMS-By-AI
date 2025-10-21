package com.toryu.iims.integral.service;

import com.toryu.iims.integral.model.dto.dict.DictDTO;
import com.toryu.iims.integral.model.dto.dict.DictPageQueryDTO;
import com.toryu.iims.integral.model.dto.dict.DictValueDTO;
import com.toryu.iims.integral.model.dto.dict.DictValuePageQueryDTO;
import com.toryu.iims.integral.model.vo.dict.DictValueVO;
import com.toryu.iims.common.enums.IntegralDictType;
import com.toryu.iims.common.model.entity.integral.DictValue;
import com.toryu.iims.common.result.PageResult;

import java.util.List;

public interface DictService {

    Boolean insertDict(DictDTO dictDto);

    Boolean insertDictValue(DictValueDTO dictValueDto);

    Boolean delDict(List<Long> ids);

    Boolean delDictValue(List<Long> ids);

    Boolean updateDict(DictDTO dictDto);

    Boolean updateDictValue(DictValueDTO dictValueDto);

    PageResult dictPageQuery(DictPageQueryDTO dictPageQueryDto);

    PageResult dictValuePageQuery(Long dictId, DictValuePageQueryDTO dictValuePageQueryDto);

    DictValue getDictValueById(Long id);

    List<DictValue> getDictValueByIds(List<Long> ids);

    List<DictValueVO> getDictValueListByType(IntegralDictType integralDictType);

    Boolean disableDict(Long id, Boolean isDisable);
}
