package com.toryu.iims.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
public enum WikiTypeEnum {

    /* 企业知识库 */
    ENTERPRISE,

    /* 个人知识库 */
    INDIVIDUAL

}
