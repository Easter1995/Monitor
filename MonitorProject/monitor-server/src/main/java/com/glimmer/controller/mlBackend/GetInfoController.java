package com.glimmer.controller.mlBackend;

import com.glimmer.constant.GetStatusConstant;
import com.glimmer.constant.MessageConstant;
import com.glimmer.dto.GetCameraDTO;
import com.glimmer.result.Result;
import com.glimmer.service.CameraInfoService;
import com.glimmer.service.CameraTransactionService;
import com.glimmer.vo.GetCameraListVO;
import com.glimmer.vo.GetCameraVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.PushBuilder;
import java.util.HashMap;

/*
Controller控制层
getInfoController 这里是给后端返回所有摄像头的相关信息
当然检测框的信息不存在是正常的，因为用户可能没有划定检测框的范围
*/
@RestController
@RequestMapping("/backend/camera")
@Slf4j
public class GetInfoController {
    //依赖注入service接口
    @Autowired
    private CameraInfoService cameraInfoService;
    @Autowired
    private CameraTransactionService cameraTransactionService;

    /*
    GetInfo 这里是给后端返回所有摄像头的检测时间---startTime和endTime、摄像头的检测类型inferClass、摄像头的编号caId、摄像头caId对应的检测框的信息。
    每个信息都是data的一个键值对。
    status为0表示成功，为1表示失败，为9表示内部错误。
    message返回对应的错误描述。
    */

    @GetMapping("/info")
    public Result<GetCameraListVO> GetCameraInfo() {
        //调用业务层相关方法
        //返回回来的是一个HashMap
        HashMap<String, Object> info = cameraInfoService.getInfo();
        //日志
        log.info("获取摄像头检测区域、时间、类别:{}",info);
        GetCameraListVO getCameraListVO = GetCameraListVO.builder()
                .cameraInfo(info)
                .message(MessageConstant.SUCCESS)
                .status(GetStatusConstant.SUCCESS)
                .build();
        return Result.success(getCameraListVO,MessageConstant.SUCCESS);
    }


    /*
    根据cam_name返回指定摄像头的摄像头信息,不包含检测框信息
    */
    @GetMapping
    public Result<GetCameraVO> GetCamera(@RequestBody GetCameraDTO getCameraDTO) {
        //记录日志
        log.info("返回摄像头数据:{}", getCameraDTO);
        //调用业务层相关方法
        //业务层返回的是一个Url类的数组
        GetCameraVO.Url[] cameraInfo = cameraTransactionService.getCamera(getCameraDTO);
        //链式构造返回给前端的视图模型
        GetCameraVO getCameraVO = GetCameraVO.builder()
                .status(GetStatusConstant.SUCCESS)
                .message(MessageConstant.GET_SUCCESS)
                .urls(cameraInfo)
                .build();
        return Result.success(getCameraVO,MessageConstant.GET_SUCCESS);
    }
}
