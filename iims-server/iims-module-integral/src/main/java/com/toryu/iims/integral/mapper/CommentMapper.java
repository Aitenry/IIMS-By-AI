package com.toryu.iims.integral.mapper;

import com.toryu.iims.common.annotation.AutoFill;
import com.toryu.iims.common.enums.OperationType;
import com.toryu.iims.common.model.entity.integral.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommentMapper {

    @AutoFill(value = OperationType.INSERT)
    Boolean insert(Comment comment);

    @Select("SELECT * FROM iims_integral_comment WHERE router_url = #{routerUrl} AND status = #{status} ORDER BY create_time DESC;")
    List<Comment> selectByRouterUrlAndStatus(String routerUrl, Integer status);
}
