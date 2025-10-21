package com.toryu.iims.integral.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsEntity {

    private String statisticalData;

    private LocalDateTime statisticalTime;

}