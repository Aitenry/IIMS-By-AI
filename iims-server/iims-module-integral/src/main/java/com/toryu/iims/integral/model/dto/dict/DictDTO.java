package com.toryu.iims.integral.model.dto.dict;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DictDTO {

    private Long id;

    private String name;

    private Boolean isCanChange;

    private String remark;

}
