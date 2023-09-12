package com.glimmer.service.impl;

import com.glimmer.constant.AlertStatusConstant;
import com.glimmer.constant.MessageConstant;
import com.glimmer.constant.TimeConstant;
import com.glimmer.dto.AcceptAlertDTO;
import com.glimmer.dto.DeleteAlertDTO;
import com.glimmer.dto.GetAlertDTO;
import com.glimmer.dto.GetSingleAlertDTO;
import com.glimmer.entity.Alert;
import com.glimmer.exception.AlertNotFoundException;
import com.glimmer.exception.BaseException;
import com.glimmer.exception.FormatException;
import com.glimmer.mapper.AlertTransactionMapper;
import com.glimmer.service.AlertTransactionService;
import com.glimmer.vo.GetAlertVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * service层报警信息接口的实现类
 * 在这里完成与报警信息相关的具体的业务功能
 */
@Service
public class AlertTransactionServiceImpl implements AlertTransactionService {

    @Autowired
    private AlertTransactionMapper alertTransactionMapper;

    /**
     * 上传图片文件
     *
     * @param file
     * @param caId
     * @param alertTime
     * @param alertType
     */
    @Override
    public void uploadPhoto(MultipartFile file, Integer caId, Integer alertTime, String alertType) throws IOException {
        //将file(报警照片)存储到本地,将caId alertTime(unix时间戳) alertType photoPath存储到数据库的alert表中
        //存到/photos目录下 文件名为传进来的文件名去掉后缀加.jpg
        String photoPath = "D:/Glimmer/JavaRecruit/photos/" + file.getOriginalFilename().substring(0,file.getOriginalFilename().lastIndexOf(".")) + ".jpg";
        File saveDir = new File(photoPath);
        file.transferTo(saveDir);//将传进来的文件保存到saveDir指定的位置
        Long alertTimeL = (long)alertTime;//将传进来的alertTime转换为Long储存在数据库内
        Alert alert = Alert.builder()
                .caId(caId)
                .alertTime(alertTimeL)
                .type(alertType)
                .pathPhoto(photoPath)
                .pathVideo("")
                .build();
        alertTransactionMapper.addAlert(alert);
    }

    /**
     * 上传视频文件
     *
     * @param file
     * @param caId
     * @param alertTime
     * @param alertType
     */
    @Override
    public void uploadVideo(MultipartFile file, Integer caId, Integer alertTime, String alertType) throws IOException {
        List<Alert> alerts = alertTransactionMapper.list();
        long alertTimeL = (long) alertTime;
        //在上传过的所有alerts中找到匹配的一项 上传video 而不是在创建一个新的alert
        for (Alert alert : alerts) {
            if (alert.getCaId().equals(caId) && alert.getAlertTime().equals(alertTimeL) && alert.getType().equals(alertType)) {
                String videoPath = "D:/Glimmer/JavaRecruit/videos/" + file.getOriginalFilename().substring(0,file.getOriginalFilename().lastIndexOf(".")) + ".avi";
                File saveDir = new File(videoPath);
                file.transferTo(saveDir);
                alert.setPathVideo(videoPath);
                Alert alert1 = Alert.builder()
                        .id(alert.getId())
                        .pathVideo(videoPath)
                        .build();
                alertTransactionMapper.updateAlert(alert1);
            }
        }
    }


    /**
     * 获取报警信息
     *
     * @param getAlertDTO
     */
    @Override
    public List<GetAlertVO.AlertVO> getAlert(GetAlertDTO getAlertDTO) {
        List<GetAlertVO.AlertVO> infos = new ArrayList<>();
        //转换时间
        //如果传进来的请求里面写了时间信息 就要把时间信息转换为unix时间戳 如果没写 则表示要查的时间区间是1970-1-1 0:00开始到当前
        String startDateStr = getAlertDTO.getStartDate();
        String endDateStr = getAlertDTO.getEndDate();
        //规定时间标准格式
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date startDateFormat;
        Date endDateFormat;
        //捕获日期格式转换异常
        try {
            if (!startDateStr.isEmpty())
                startDateFormat = dateFormat.parse(startDateStr);
            else
                startDateFormat = dateFormat.parse("1970-1-1 0:00");//如果请求里没写开始时间 就把1970-1-1 0:00设为开始时间
            if (!endDateStr.isEmpty())
                endDateFormat = dateFormat.parse(endDateStr);
            else
                endDateFormat = new Date();//如果请求里面没写结束时间 就把当前时间设为结束时间
        } catch (ParseException e) {
            throw new FormatException(MessageConstant.FORMAT_ERROR);
        }
        //转换成unix时间戳
        Long startDate = startDateFormat.getTime() / TimeConstant.MS_BETWEEN_S;
        Long endDate = endDateFormat.getTime() / TimeConstant.MS_BETWEEN_S;

        String[] caId = getAlertDTO.getCaId();
        String[] type = getAlertDTO.getType();
        //从所有报警信息中找到caId在String[] caId里面,type在String[] type里面,alert_time在startDate和endDate区间里的
        List<Alert> alerts = alertTransactionMapper.list();
        for (Alert alert : alerts) {
            //把alert的caId转换成str 方便后续查找
            String caIdStr = Integer.toString(alert.getCaId());
            //先判断这个alert的caId是否在查询的范围内
            if (Arrays.asList(caId).contains(caIdStr) || caId.length == 0) {
                //再判断这个alert的type是否在查询的范围内
                if (Arrays.asList(type).contains(alert.getType()) || type.length == 0) {
                    //最后判断这个alert的alert_time是否在查询的时间区间内
                    if ((alert.getAlertTime() >= startDate) && (alert.getAlertTime() <= endDate)) {
                        GetAlertVO.AlertVO alertVO = GetAlertVO.AlertVO.builder()
                                .alertTime(alert.getAlertTime())
                                .caId(alert.getCaId())
                                .photoPath(alert.getPathPhoto())
                                .type(alert.getType())
                                .videoPath(alert.getPathVideo())
                                .build();
                        infos.add(alertVO);//把这个新构建好的符合要求的alertVO加入到infos这个list里面
                    }
                }
            }
        }
        return infos;
    }


    @Override
    public GetAlertVO getSingleAlert(GetSingleAlertDTO getSingleAlertDTO){
        List<Alert> alerts = alertTransactionMapper.list();
        GetAlertVO.AlertVO alertVO = new GetAlertVO.AlertVO();
        for (Alert alert : alerts) {
            if (alert.getCaId().equals(getSingleAlertDTO.getCaId())) {
                if (alert.getType().equals(getSingleAlertDTO.getAlertType())) {
                    Long alertTime = getSingleAlertDTO.getAlertTime().longValue();
                    if (alertTime.equals(alert.getAlertTime())) {
                        alertVO = GetAlertVO.AlertVO.builder()
                                .alertTime(alert.getAlertTime())
                                .caId(alert.getCaId())
                                .photoPath(alert.getPathPhoto())
                                .type(alert.getType())
                                .videoPath(alert.getPathVideo())
                                .build();
                    }
                }
            }
        }
        List<GetAlertVO.AlertVO> info = new ArrayList<>();
        info.add(alertVO);
        return GetAlertVO.builder()
                .infos(info)
                .status(AlertStatusConstant.SUCCESS)
                .message(MessageConstant.SUCCESS)
                .build();
    }


    /**
     * 删除报警信息
     *
     * @param deleteAlertDTO
     */
    @Override
    public void deleteAlert(DeleteAlertDTO deleteAlertDTO) {
        //首先判断视频路径数组是否为空，如果不为空，那么就要删除视频文件和数据库中的记录
        String[] pathPhotos = deleteAlertDTO.getPathPhotos();
        String[] pathVideos = deleteAlertDTO.getPathVideos();
        if (pathVideos != null && pathVideos.length > 0) {
            //遍历视频路径数组，删除视频文件和数据库中的记录
            for (String pathVideo : pathVideos) {
                Alert alert = alertTransactionMapper.getByVideoPath(pathVideo);
                if (alert == null) throw new AlertNotFoundException("视频路径：" + MessageConstant.NOT_EXIST);
                alertTransactionMapper.deleteByVideoPath(pathVideo);
                File file = new File(pathVideo);
                if (!file.delete()) throw new BaseException(MessageConstant.UNKNOWN_ERROR);
            }
        }
        if (pathPhotos != null && pathPhotos.length > 0) {
            //遍历视频路径数组，删除视频文件和数据库中的记录
            for (String pathPhoto : pathPhotos) {
                Alert alert = alertTransactionMapper.getByPhotoPath(pathPhoto);
                if (alert == null)
                    throw new AlertNotFoundException("图片路径" + MessageConstant.NOT_EXIST + "或已删除");
                alertTransactionMapper.deleteByPhotoPath(pathPhoto);
                File file = new File(pathPhoto);
                if (!file.delete()) throw new BaseException(MessageConstant.UNKNOWN_ERROR);
            }
        }
    }


    /**
     * 发送报警信息给前端
     * 这一部分用来找到需要返回的文件
     * @param acceptAlertDTO
     */
    @Override
    public File acceptAlert(AcceptAlertDTO acceptAlertDTO) {
        String fileName = "";//存文件名
        String filePath = "";//存文件地址
        String photo = acceptAlertDTO.getPathPhoto();
        String video = acceptAlertDTO.getPathVideo();
        List<Alert> alerts = alertTransactionMapper.list();
        //两个都不为空或者视频地址不为空 均返回视频文件
        if (!video.isEmpty()) {
            boolean isExist = false;
            for (Alert alert : alerts) {
                //找到目标文件
                if (alert.getPathVideo().equals(video)) {
                    isExist = true;
                    filePath = alert.getPathVideo();
                }
            }
            //找不到
            if (!isExist)
                throw new AlertNotFoundException("视频文件不存在");
        }
        else if (!photo.isEmpty()) {
            //照片地址不为空视频地址为空 返回照片文件
            boolean isExist = false;
            for (Alert alert : alerts) {
                //找到目标文件
                if (alert.getPathPhoto().equals(photo)) {
                    isExist = true;
                    filePath = alert.getPathPhoto();
                }
            }
            //找不到
            if (!isExist)
                throw new AlertNotFoundException("照片文件不存在");
        }
        else {
            //文件不存在
            throw new AlertNotFoundException(MessageConstant.NOT_EXIST);
        }
        File file = new File(filePath);//目标文件
        return file;
    }

}
