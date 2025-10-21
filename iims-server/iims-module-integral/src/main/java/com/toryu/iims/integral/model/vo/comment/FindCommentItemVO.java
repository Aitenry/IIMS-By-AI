package com.toryu.iims.integral.model.vo.comment;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindCommentItemVO {

    /**
     * 主键
     */
    @ApiModelProperty("菜单id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long id;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 发布时间
     */
    private LocalDateTime createTime;

    /**
     * 回复用户的昵称
     */
    private String replyNickname;

    /**
     * 子评论集合
     */
    private List<FindCommentItemVO> childComments;

    /**
     * 是否展示回复表单（默认 false）
     */
    private Boolean isShowReplyForm;

    private Long createBy;

}