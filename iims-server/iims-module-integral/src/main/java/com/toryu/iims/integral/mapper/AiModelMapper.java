package com.toryu.iims.integral.mapper;

import com.github.pagehelper.Page;
import com.toryu.iims.integral.model.dto.llm.ModelPageQueryDTO;
import com.toryu.iims.integral.model.vo.llm.ModelVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: Aitenry
 * @Date: 2025/11/23 14:22
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Mapper
public interface AiModelMapper {

    Page<ModelVO> pageQuery(ModelPageQueryDTO modelPageQueryDTO);

}
