package com.glimmer.service;

import com.glimmer.dto.AddCameraDTO;
import com.glimmer.dto.DeleteCameraDTO;
import com.glimmer.dto.GetCameraDTO;
import com.glimmer.dto.UpdateCameraDTO;
import com.glimmer.vo.GetCameraVO;

//摄像头service层接口,在这里定义相关的业务接口
public interface CameraTransactionService {

    /**
     * 添加摄像头
     * @param addCameraDTO
     */
    void AddCamera(AddCameraDTO addCameraDTO);

    /**
     * 根据提供的摄像头名称查询相关的数据
     * @param getCameraDTO
     * @return
     */
    GetCameraVO.Url[] getCamera(GetCameraDTO getCameraDTO);

    /**
     * 修改摄像头数据
     * @param updateCameraDTO
     */
    void updateCamera(UpdateCameraDTO updateCameraDTO);

    /**
     * 删除摄像头
     * @param deleteCameraDTO
     */
    void deleteCamera(DeleteCameraDTO deleteCameraDTO);
}
