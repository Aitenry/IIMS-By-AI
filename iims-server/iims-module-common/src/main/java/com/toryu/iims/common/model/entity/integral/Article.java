package com.toryu.iims.common.model.entity.integral;

import com.toryu.iims.common.model.entity.base.BaseTable;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Article extends BaseTable implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private String title;

    private Long cover;

    private String summary;

    private Boolean isDeleted;

    private Long readNum;

    private Integer weight;

    private Integer type;

    private String dictTagIds;

    private Long dictCategoryId;

}
