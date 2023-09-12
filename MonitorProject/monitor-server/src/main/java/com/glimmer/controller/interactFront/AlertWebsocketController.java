package com.glimmer.controller.interactFront;

import com.glimmer.constant.GetStatusConstant;
import com.glimmer.constant.MessageConstant;
import com.glimmer.dto.GetSingleAlertDTO;
import com.glimmer.result.Result;
import com.glimmer.service.AlertTransactionService;
import com.glimmer.vo.GetAlertVO;
import com.glimmer.vo.StatusVO;
import com.glimmer.webSocket.AlertWebsocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.multipart.MultipartFile;

import javax.websocket.EncodeException;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/ws/alert")
public class AlertWebsocketController {
    //依赖注入AlertWebsocketServer,可以调用其相关方法
    @Autowired
    private AlertWebsocketServer alertWebsocketServer;

    //依赖注入AlertTransactionService,通过AlertTransactionServiceImpl获取报警信息
    @Autowired
    private AlertTransactionService alertTransactionService;


    //后端上传视频后 产生了一个新的报警信息 给客户端推送新的报警信息
    @PostMapping("/video")
    public Result<StatusVO> AlertVideo(MultipartFile alertVideo, Integer caId, Integer alertTime, String alertType) throws IOException, EncodeException {
        log.info("文件上传：{},{},{},{}", alertVideo, caId, alertTime, alertType);
        alertTransactionService.uploadVideo(alertVideo, caId, alertTime, alertType);
        StatusVO statusVO = StatusVO.builder()
                .status(GetStatusConstant.SUCCESS)
                .message("报警信息存储" + MessageConstant.SUCCESS)
                .build();

        //在这里获取单个报警信息info 作为后端主动推送到前端的内容
        GetSingleAlertDTO getSingleAlertDTO = GetSingleAlertDTO.builder()
                .alertType(alertType)
                .alertTime(alertTime)
                .caId(caId)
                .build();
        //getAlertVO就是要推送到前端的内容
        GetAlertVO getAlertVO = alertTransactionService.getSingleAlert(getSingleAlertDTO);
        alertWebsocketServer.sendInfo(getAlertVO);
        return Result.success(statusVO, MessageConstant.SUCCESS);
    }

}
