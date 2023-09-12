package com.glimmer.controller.interactFront;

import com.glimmer.constant.*;
import com.glimmer.dto.AddCameraDTO;
import com.glimmer.dto.DeleteCameraDTO;
import com.glimmer.dto.GetCameraDTO;
import com.glimmer.dto.UpdateCameraDTO;
import com.glimmer.result.Result;
import com.glimmer.service.CameraTransactionService;
import com.glimmer.vo.GetCameraVO;
import com.glimmer.vo.StatusVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * CameraTransactionController 控制层
 * 这个部分是与摄像头交互的部分，数据库用到的表是Camera类所对应的表
 * 功能上有：
 * 	1、摄像头增加: url为: /camera(post) 对应的controller函数为: AddCamera
 * 		传入新增摄像头的必要参数，返回是否添加成功
 * 	2、摄像头删除: url为: /camera(delete) 对应的controller函数为: DeleteCamera
 * 		传入想要删除的摄像头名称列表，返回是否删除成功
 * 	3、摄像头获取: url为: /camera(get) 对应的controller函数为: GetCamera
 * 		传入想要获取的摄像头名称列表或者“all”，返回摄像头的url列
 * 	4、摄像头(时间)修改: url为: /camera(put) 对应的controller函数为: UpdateCamera
 * 		传入想要修改的摄像头名称，并且传入startTime和endTime，返回是否修改成功
 */
@RestController
@RequestMapping("/camera")//默认为GET方式的路由/camera
@Slf4j
public class CameraTransactionController {
    /**
     * 依赖注入CameraTransactionService，可用于调用其相关方法
     */
    @Autowired
    private CameraTransactionService cameraTransactionService;

/*
AddCamera 函数用于添加摄像头：注册的路由url为: /camera
| 请求参数   |          |                                  |
| ---------- | -------- | -------------------------------- |
| 参数名     | 数据类型 | 说明                             |
| name       | string   | 为摄像头起的名称                 |
| ip         | string   | 该字段为访问摄像头的网络ip       |
| port       | string   | 该字段为访问摄像头的网络port     |
| user       | string   | 该字段为登录该摄像头的账号字段   |
| passwd     | string   | 该字段为登录该摄像头的密码字段   |
| channel    | int      | 该字段为访问摄像头中画面的字段   |
| area       | string   | 区域，限定该摄像头来自哪个辖区   |
| startTime  | string   | 该摄像头开始检测时间             |
| endTime    | string   | 该摄像头结束检测时间             |
| inferClass | []string| 该字段为描述该摄像头检测类型字段 |
对于响应的dataResponse部分：
status int ： 0表示成功，1表示失败 9表示未知错误
message string ： 返回的信息
*/
    @PostMapping
    public Result<StatusVO> AddCamera(@RequestBody AddCameraDTO addCameraDTO) {
        //记录日志
        log.info("添加摄像头:{}",addCameraDTO);
        //调用业务层相关方法
        cameraTransactionService.AddCamera(addCameraDTO);
        //构造对象返回
        StatusVO statusVO = StatusVO.builder()
                .message(MessageConstant.ADD_SUCCESS)
                .status(GetStatusConstant.SUCCESS)
                .build();
        //return null;//这里是为了防止报错，先返回空
        return Result.success(statusVO,MessageConstant.ADD_SUCCESS);
    }

/*
DeleteCamera : 删除摄像头
method : DELETE
url : /camera
在请求体里面只有一个参数name，表示要删除的摄像头的名字
responseData有status和message两个字段
status为0表示成功，为1表示失败 9表示系统内部错误
message为提示信息
请注意，使用 DELETE 方法删除资源时需要谨慎，因为一旦删除后就无法恢复。
另外，删除资源时应该遵循安全和幂等的原则，即相同的请求多次执行应该产生相同的结果，并且不会对系统状态产生影响。
*/
    @DeleteMapping
    public Result<StatusVO> DeleteCamera(@RequestBody DeleteCameraDTO deleteCameraDTO) {
        //记录日志
        log.info("删除摄像头:{}",deleteCameraDTO);
        //调用业务层相关方法
        cameraTransactionService.deleteCamera(deleteCameraDTO);
        //构造对象返回
        StatusVO statusVO = StatusVO.builder()
                .status(DeleteStatusConstant.SUCCESS)
                .message(MessageConstant.DELETE_SUCCESS)
                .build();
        return Result.success(statusVO,MessageConstant.DELETE_SUCCESS);
    }

/*
GetCamera : 获取摄像头信息
传入想要获取的摄像头名称列表或者“all”，返回摄像头的url列,all代表返回所有摄像头的url,如果没有摄像头名称列表或者为空就相当于all
请求的names是一个[]string类型
method : GET
url : /camera
在请求体里面只有一个参数name，表示要获取的摄像头的名字列表
responseData有status、message还有urls字段，urls是一个对象数组类型，表示摄像头的url列表
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

/*
UpdateCamera : 更新摄像头信息
传入想要更新的摄像头信息
method : PUT
url : /camera
在请求体里面有一个参数name,是要更新的摄像头的名字,有startTime和endTime两个参数,分别是开始时间和结束时间,他们格式为"8:00"这种形式，需要在后端转换为时间戳格式
responseData有status、message字段,和前面的规范一样
status = 0表示更新成功 1表示更新失败 9表示系统内部错误
message表示更新成功或者失败的信息
*/

    @PutMapping
    public Result<StatusVO> UpdateCamera(@RequestBody UpdateCameraDTO updateCameraDTO) {
        //记录日志
        log.info("摄像头时间修改:{}",updateCameraDTO);
        //调用业务层相关方法
        cameraTransactionService.updateCamera(updateCameraDTO);
        //构造对象返回
        StatusVO statusVO = StatusVO.builder()
                .status(GetStatusConstant.SUCCESS)
                .message(MessageConstant.CHANGE_SUCCESS)
                .build();
        return Result.success(statusVO,MessageConstant.CHANGE_SUCCESS);
    }
}
