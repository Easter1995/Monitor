package com.glimmer.service;

import com.glimmer.dto.AcceptAlertDTO;
import com.glimmer.dto.DeleteAlertDTO;
import com.glimmer.dto.GetAlertDTO;
import com.glimmer.dto.GetSingleAlertDTO;
import com.glimmer.vo.GetAlertVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;

//报警信息service层接口,在这里定义相关的业务接口
public interface AlertTransactionService {


    /**
     * 图片文件上传
     * @param alertPhoto
     * @param caId
     * @param alertTime
     * @param alertType
     */
    void uploadPhoto(MultipartFile alertPhoto, Integer caId, Integer alertTime, String alertType) throws IOException;

    /**
     * 视频文件上传
     * @param alertVideo
     * @param caId
     * @param alertTime
     * @param alertType
     */
    void uploadVideo(MultipartFile alertVideo, Integer caId, Integer alertTime, String alertType) throws IOException;

    /**
     * 获取报警信息
     * @param getAlertDTO
     * @return
     */
    List<GetAlertVO.AlertVO> getAlert(GetAlertDTO getAlertDTO);

    /**
     * 获取单个报警信息
     */
    GetAlertVO getSingleAlert(GetSingleAlertDTO getSingleAlertDTO);

    /**
     * 删除报警信息
     * @param deleteAlertDTO
     */
    void deleteAlert(DeleteAlertDTO deleteAlertDTO);

    /**
     * 发送报警信息给前端
     * @param acceptAlertDTO
     */
    File acceptAlert(AcceptAlertDTO acceptAlertDTO);

}
