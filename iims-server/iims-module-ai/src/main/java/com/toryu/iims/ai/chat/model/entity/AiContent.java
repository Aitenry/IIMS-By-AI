package com.toryu.iims.ai.chat.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2026/1/16 21:05
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiContent {

    private StringBuffer thinking;

    private StringBuffer content;

    private List<ChatTool> tools;

}
