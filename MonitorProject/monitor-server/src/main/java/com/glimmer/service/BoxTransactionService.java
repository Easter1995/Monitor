package com.glimmer.service;

import com.glimmer.dto.AddBoxDTO;
import com.glimmer.dto.BoxDTO;

//检测框service层接口,在这里定义相关的业务接口
public interface BoxTransactionService {
    /**
     * 添加检测框
     * @param addBoxDTO
     */
    void addBox(AddBoxDTO addBoxDTO);

    /**
     * 根据caId删除检测框
     * @param boxDTO
     */
    void deleteBox(BoxDTO boxDTO);

    /**
     * 根据caId查询检测框
     * @param boxDTO
     * @return
     */
    String getBox(BoxDTO boxDTO);

}
