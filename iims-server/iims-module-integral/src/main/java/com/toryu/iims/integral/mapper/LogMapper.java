package com.toryu.iims.integral.mapper;

import com.github.pagehelper.Page;
import com.toryu.iims.integral.model.dto.log.LogPageQueryDTO;
import com.toryu.iims.integral.model.vo.log.LogVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: Aitenry
 * @Date: 2025/11/23 15:28
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Mapper
public interface LogMapper {

    Page<LogVO> pageQuery(LogPageQueryDTO logPageQueryDTO);

}
