package com.toryu.iims.common.mapper;

import com.toryu.iims.common.model.entity.log.Logs;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LogsMapper {

    void insert(Logs log);

}
