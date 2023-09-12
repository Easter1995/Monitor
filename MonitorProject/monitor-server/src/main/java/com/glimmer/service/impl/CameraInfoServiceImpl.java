package com.glimmer.service.impl;

import com.glimmer.constant.TimeConstant;
import com.glimmer.entity.Box;
import com.glimmer.entity.Camera;
import com.glimmer.mapper.BoxTransactionMapper;
import com.glimmer.mapper.CameraTransactionMapper;
import com.glimmer.service.CameraInfoService;
import com.glimmer.vo.InfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * service层摄像头监测信息接口的实现类
 * 在这里完成摄像头相关的具体的业务功能
 */
@Service
public class CameraInfoServiceImpl implements CameraInfoService {

    //依赖注入Mapper层相关接口
    @Autowired
    private CameraTransactionMapper cameraTransactionMapper;

    @Autowired
    private BoxTransactionMapper boxTransactionMapper;


    @Override
    public HashMap<String, Object> getInfo() {
        //初始化相关参数
        HashMap<String, Object> map = new HashMap<>();
        //先调用mapper查询所有的摄像头信息---startTime和endTime、摄像头的检测类型inferClass、摄像头的编号caId
        List<Camera> cameraList = cameraTransactionMapper.list();
        //将获取到的信息添加到map中,这里的cameraList是一个集合，所以需要遍历,并且遍历中，对于box信息，也需要遍历，是通过caId去查找box表然后填充到map中["box"]键值对里面
        for (Camera camera : cameraList) {
            InfoVO info = new InfoVO();
            //将camera的信息添加到info中去，注意日期格式转换以及inferClass转换
            String startTime = String.format("%02d:%02d",camera.getStartTime() / TimeConstant.NS_BETWEEN_S / TimeConstant.TIME_HOUR , camera.getStartTime() / TimeConstant.NS_BETWEEN_S % TimeConstant.TIME_HOUR / TimeConstant.TIME_MINUTE);
            String endTime = String.format("%02d:%02d",camera.getEndTime() / TimeConstant.NS_BETWEEN_S / TimeConstant.TIME_HOUR , camera.getEndTime() / TimeConstant.NS_BETWEEN_S % TimeConstant.TIME_HOUR / TimeConstant.TIME_MINUTE);
            //split方法可以将","作为分隔符,把字符串转换为字符串数组
            String[] inferClass = camera.getInferClass().split(",");
            info.setCaId(camera.getCaId());
            info.setInferClass(inferClass);
            info.setStartTime(startTime);
            info.setEndTime(endTime);

            //根据caId从数据库中获取对应的box信息
            List<Box> boxes = boxTransactionMapper.getByCaId(camera.getCaId());
            if(boxes == null || boxes.size() == 0){
            /*
            如果是没有找到记录的错误
            这里不需要返回错误，因为用户可能没有划定检测框的范围
            这里的box信息设置为空串
            */
                info.setBox("");
            }else {
                /*
                如果找到了记录，则处理box信息，
                从集合中将每个元素的leftUp和rightDown信息取出，拼接，
                得到的格式为"leftUp,rightDown|leftUp,rightDown|leftUp,rightDown"
                 */
                StringBuffer boxStr = new StringBuffer();
                for (int i = 0 ; i < boxes.size()-1 ; i++){
                    boxStr.append(boxes.get(i).getLeftUp() + "," + boxes.get(i).getRightDown() + "|");
                }
                boxStr.append(boxes.get(boxes.size()-1).getLeftUp() + "," + boxes.get(boxes.size()-1).getRightDown());
                //将boxStr添加到info中去
                String boxStr0 = boxStr.toString();
                info.setBox(boxStr0);
            }

            //将对应的info添加到map中去 一个cameraName对应一个info
            map.put(camera.getName(),info);
        }
        return map;
    }
}
