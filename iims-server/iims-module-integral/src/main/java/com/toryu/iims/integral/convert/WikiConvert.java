package com.toryu.iims.integral.convert;

import com.toryu.iims.integral.model.vo.wiki.FindWikiPageListVO;
import com.toryu.iims.common.model.entity.integral.Wiki;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @description: 知识库实体类转换
 **/
@Mapper
public interface WikiConvert {
    /**
     * 初始化 convert 实例
     */
    WikiConvert INSTANCE = Mappers.getMapper(WikiConvert.class);

    /**
     * WikiDO -> FindWikiPageListDto
     */
    @Mapping(target = "isTop", expression = "java(bean.getWeight() > 0)")
    FindWikiPageListVO convertDO2VO(Wiki bean);

}
