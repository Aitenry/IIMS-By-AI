package com.toryu.iims.integral.model.dto.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PublishCommentDTO {

    @NotBlank(message = "路由地址不能为空")
    private String routerUrl;

    @NotBlank(message = "评论内容不能为空")
    @Length(min = 1, max = 120, message = "评论内容需大于 1 小于 120 字符")
    private String content;

    /**
     * 回复的评论 ID
     */
    private Long replyCommentId;

    /**
     * 父评论 ID
     */
    private Long parentCommentId;

}