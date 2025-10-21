package com.toryu.iims.integral.model.dto.comment;

import com.toryu.iims.common.model.entity.base.BasePageQuery;
import io.swagger.annotations.ApiModel;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "查询评论分页数据入参")
@EqualsAndHashCode(callSuper = false)
public class FindCommentPageListDTO extends BasePageQuery {

    /**
     * 路由地址
     */
    private String routerUrl;

    /**
     * 发布的起始日期
     */
    private LocalDateTime startDate;

    /**
     * 发布的结束日期
     */
    private LocalDateTime endDate;

    /**
     * 状态
     */
    private Integer status;
}