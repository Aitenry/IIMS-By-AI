package com.toryu.iims.ai.chat.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendStatusData implements Serializable {

    private String task;

    private Integer total;

    private Integer progress;

}
