package com.toryu.iims.integral.model.dto.comment;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "删除评论")
public class DeleteCommentDTO {

    @NotNull(message = "评论 ID 不能为空")
    private Long id;

}