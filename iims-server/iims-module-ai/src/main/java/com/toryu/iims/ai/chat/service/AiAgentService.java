package com.toryu.iims.ai.chat.service;

import com.toryu.iims.ai.chat.model.vo.SelectAgentVO;

import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2026/2/9 19:58
 * @Version: v1.0.0
 * @Description: TODO
 **/
public interface AiAgentService {

    List<SelectAgentVO> selectAgentList();

}
