package com.toryu.iims.integral.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.toryu.iims.common.result.PageResult;
import com.toryu.iims.integral.mapper.LogMapper;
import com.toryu.iims.integral.model.dto.log.LogPageQueryDTO;
import com.toryu.iims.integral.model.vo.log.LogVO;
import com.toryu.iims.integral.service.LogService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2025/11/23 15:06
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Service
public class LogServiceImpl implements LogService {

    public final LogMapper logMapper;

    public LogServiceImpl(LogMapper logMapper) {
        this.logMapper = logMapper;
    }

    @Override
    public PageResult pageQuery(LogPageQueryDTO logPageQueryDTO) {
        int page = logPageQueryDTO.getPage();
        int pageSize = logPageQueryDTO.getPageSize();
        PageHelper.startPage(page, pageSize);
        try (Page<LogVO> modelVOS = logMapper.pageQuery(logPageQueryDTO)) {
            long total = modelVOS.getTotal();
            List<LogVO> results = modelVOS.getResult();
            return new PageResult(total, results);
        }
    }
}
