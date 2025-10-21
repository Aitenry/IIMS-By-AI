package com.toryu.iims.common.model.vo;

import lombok.Data;

/**
 * 节假日Vo
 */
@Data
public class HolidayVO {

    private String data;//日期

    private String status;//状态：0工作日/1周末/2法定节假日/3节假日调休补班

    private String msg;//描述

}