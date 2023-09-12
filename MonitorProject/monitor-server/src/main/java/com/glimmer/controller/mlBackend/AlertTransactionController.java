package com.glimmer.controller.mlBackend;

import com.glimmer.constant.GetStatusConstant;
import com.glimmer.constant.MessageConstant;
import com.glimmer.dto.DeleteAlertDTO;
import com.glimmer.result.Result;
import com.glimmer.service.AlertTransactionService;
import com.glimmer.vo.StatusVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/*
alertTransactionController
这个部分是对于报警的操作，用到的数据库表是alert,对应的结构体是Alert,是和机器学习视频处理后端交互的部分
功能上有：
	1、报警信息上传:url为: /backend/alert/info 对应的handler函数为: AlertInfo
	**方法为POST**
	上传报警信息，包括的参数为 图片的.jpg文件、 摄像头的编号也就是caId、报警时间戳（为unix时间戳）、报警类型
	其中图片.jpg是二进制文件，存储到本地，然后将路径存储到alert表中，caId、alertTime（报警时间戳）、报警类型（alertType）都存储到alert表中
	这里因为包含二进制信息，我们采用form-data的格式定义接口，避免二进制转为base64会产生将近30%的额外空间
	2、 报警视频上传: url为: /backend/alert/video 对应的handler函数为: AlertVideo
	**方法为POST**
	上传报警视频，包括的参数为 视频的.avi文件、 摄像头的编号也就是caId、报警时间戳（为unix时间戳）、报警类型
	其中视频.avi是二进制文件，存储到本地，然后将路径存储到alert表中，caId、alertTime（报警时间戳）、报警类型（alertType）都存储到alert表中
	这里因为包含二进制信息，我们采用form-data的格式定义接口，避免二进制转为base64会产生将近30%的额外空间
请求的参数基本一致两个功能：
alertPhoto/Video: 二进制文件
caId: 摄像头的编号
alertTime: 报警时间戳
alertType: 报警类型
我们的响应还是一样的，使用dataResponse来存储响应的数据，然后使用Response函数来响应请求
status为0表示成功，为1表示失败，为9表示内部错误。

message返回对应的错误描述。
*/
@RestController("backendAlertTransactionController")
@Slf4j
@RequestMapping("/backend/alert")
public class AlertTransactionController {
    @Autowired
    private AlertTransactionService alertTransactionService;

    /*
    AlertInfo 上传报警信息，包括的参数为 图片的.jpg文件、 摄像头的编号也就是caId、报警时间戳（为unix时间戳）、报警类型
    */
    @PostMapping("/info")
    public Result<StatusVO> AlertInfo(MultipartFile alertPhoto, Integer caId, Integer alertTime, String alertType) throws IOException {
        log.info("文件上传：{},{},{},{}", alertPhoto, caId, alertTime, alertType);
        alertTransactionService.uploadPhoto(alertPhoto, caId, alertTime, alertType);
        StatusVO statusVO = StatusVO.builder()
                .status(GetStatusConstant.SUCCESS)
                .message("报警信息存储" + MessageConstant.SUCCESS)
                .build();
        return Result.success(statusVO, MessageConstant.SUCCESS);
    }

    /*
    AlertVideo 用于接收报警视频
    请求方式：POST
    请求参数：
    alertVideo: 报警视频
    caId: 摄像头编号
    alertTime: 报警时间戳（为unix时间戳）
    alertType: 报警类型
    返回参数：
    status: 状态码
    message: 信息
    和上面的AlertPhoto差不多，就不再赘述了，实现方式也是几乎一模一样，只是图片换成了视频
    不过有个点需要注意，这里传入的caId、alertTime、alertType不是用来在alert表中创建一个新的行，而是用来在alert表中找到对应的行，然后将这行的path_video字段更新为视频的存储路径
    */
    @PostMapping("/video")
    public Result<StatusVO> AlertVideo(MultipartFile alertVideo, Integer caId, Integer alertTime, String alertType) throws IOException {
        log.info("文件上传：{},{},{},{}", alertVideo, caId, alertTime, alertType);
        alertTransactionService.uploadVideo(alertVideo, caId, alertTime, alertType);
        StatusVO statusVO = StatusVO.builder()
                .status(GetStatusConstant.SUCCESS)
                .message("报警信息存储" + MessageConstant.SUCCESS)
                .build();
        return Result.success(statusVO, MessageConstant.SUCCESS);
    }

}
