package com.toryu.iims.integral.convert;

import com.toryu.iims.common.model.entity.integral.Comment;
import com.toryu.iims.integral.model.vo.comment.FindCommentItemVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CommentConvert {
    /**
     * 初始化 convert 实例
     */
    CommentConvert INSTANCE = Mappers.getMapper(CommentConvert.class);

    FindCommentItemVO convertDO2VO(Comment bean);

}
