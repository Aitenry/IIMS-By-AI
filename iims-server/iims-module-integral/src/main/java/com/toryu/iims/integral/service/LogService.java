package com.toryu.iims.integral.service;

import com.toryu.iims.common.result.PageResult;
import com.toryu.iims.integral.model.dto.log.LogPageQueryDTO;

/**
 * @Author: Aitenry
 * @Date: 2025/11/23 15:06
 * @Version: v1.0.0
 * @Description: TODO
 **/
public interface LogService {

    PageResult pageQuery(LogPageQueryDTO logPageQueryDTO);

}
