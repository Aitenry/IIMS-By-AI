package com.toryu.iims.integral.model.vo.article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Aitenry
 * @Date: 2025/10/25 12:02
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindTagListVO {
    private Long id;
    private String name;
    private Integer articlesTotal;
}
