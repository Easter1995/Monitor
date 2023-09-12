package com.glimmer.service.impl;

import com.glimmer.constant.MessageConstant;
import com.glimmer.constant.TimeConstant;
import com.glimmer.exception.*;
import com.glimmer.dto.AddCameraDTO;
import com.glimmer.dto.DeleteCameraDTO;
import com.glimmer.dto.GetCameraDTO;
import com.glimmer.dto.UpdateCameraDTO;
import com.glimmer.entity.Camera;
import com.glimmer.mapper.CameraTransactionMapper;
import com.glimmer.service.CameraTransactionService;
import com.glimmer.vo.GetCameraVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
/**
 * service层摄像头接口的实现类
 * 在这里完成摄像头相关的具体的业务功能
 */
@Service
public class CameraTransactionServiceImpl implements CameraTransactionService {

    //依赖注入Mapper层相关接口
    @Autowired
    private CameraTransactionMapper cameraTransactionMapper;

    /**
     * 添加摄像头业务功能
     *
     * @param addCameraDTO
     */
    @Override
    public void AddCamera(AddCameraDTO addCameraDTO) {
        /*
        对addCameraDTO进行参数处理：
        1.这里的Channel字段需要从Integer类型转为Long类型
        2.这里对于参数的startTime和endTime需要特殊处理,因为他们传入的是如"8:00"这样的字符串,需要转为一个以纳秒为单位的时间戳对应的int64大小的值
        3.这里对于inferClass字段也需要进行特殊处理，将其String数组类型转为String类型的json字符串
         */
        //检查摄像头是否已经存在
        //调用数据层查询所有摄像头数据
        List<Camera> cameras = cameraTransactionMapper.list();
        String[] names = new String[cameras.size()];//根据摄像头数量开数组
        if (!cameras.isEmpty()){
            for (Camera camera : cameras) {
                String existedName = camera.getName();
                if (existedName.equals(addCameraDTO.getName())) {
                    throw new ExistException(MessageConstant.ALREADY_EXIST);
                }
            }
        }
        //1
        long channel = (long)addCameraDTO.getChannel();
        //2
        //从请求参数中获取时间,直接得到的时间是一个字符串
        String startTimeStr = addCameraDTO.getStartTime();
        String endTimeStr = addCameraDTO.getEndTime();
        //进行格式转换 SimpleDateFormat是DateFormat的子类
        DateFormat startTimeFormat = new SimpleDateFormat("HH:mm");
        DateFormat endTimeFormat = new SimpleDateFormat("HH:mm");
        //Date类实例 存储格式化后的用户输入时间 Date类的getTime会把日期变成毫秒值(1970-1-1开始)
        Date startTime0;
        Date endTime0;
        //这里捕获日期格式转换异常，并将业务异常抛给handler处理
        try {
            //parse可以把String类型的字符串转换为特定格式的Date类型,这里就是转换成SimpleDateFormat的格式
            startTime0 = startTimeFormat.parse(startTimeStr);
            endTime0 = endTimeFormat.parse(endTimeStr);
        } catch (ParseException e) {
            throw new FormatException(MessageConstant.FORMAT_ERROR);
        }
        //转化为unix时间戳 纳秒
        if (startTime0 != null && endTime0 != null){
            Long startTime = (startTime0.getHours()*TimeConstant.TIME_HOUR + startTime0.getMinutes()*TimeConstant.TIME_MINUTE)*TimeConstant.NS_BETWEEN_S;
            Long endTime = (endTime0.getHours()*TimeConstant.TIME_HOUR + endTime0.getMinutes()*TimeConstant.TIME_MINUTE)*TimeConstant.NS_BETWEEN_S;
            //把传过来的inferClass字符串数组变成一个字符串
            StringBuffer inferClass0 = new StringBuffer();//不知道具体长度,先用StringBuffer存着
            int i;
            for (i = 0 ; i < addCameraDTO.getInferClass().length-1 ; i++){
                //StringBuffer类里面的append方法可以拼接字符串
                inferClass0.append(addCameraDTO.getInferClass()[i]+",");
            }
            inferClass0.append(addCameraDTO.getInferClass()[i]);
            String inferClass1 = inferClass0.toString();//把StringBuffer转化为String
            //构造摄像头对象,注意检查要填入所有请求参数
            Camera camera = Camera.builder()
                    .name(addCameraDTO.getName())
                    .ip(addCameraDTO.getIp())
                    .port(addCameraDTO.getPort())
                    .channel(channel)
                    .user(addCameraDTO.getUser())
                    .passwd(addCameraDTO.getPasswd())
                    .area(addCameraDTO.getArea())
                    .startTime(startTime)
                    .endTime(endTime==0 ? (TimeConstant.TIME_DAY * TimeConstant.NS_BETWEEN_S) : endTime)
                    .inferClass(inferClass1)
                    .build();
            cameraTransactionMapper.add(camera);
        }
    }

    /**
     * 根据名称查询摄像头数据
     *
     * @param getCameraDTO
     * @return 返回摄像头数据的urls列
     */
    @Override
    public GetCameraVO.Url[] getCamera(GetCameraDTO getCameraDTO) {
        /*对参数进行处理，查询数据
        判定参数是否为空,不为空才进行处理(注意要将startTime、endTime转为响应的时间格式字符串)
        */
        //clone()方法可以直接实现一个数组给另一个数组赋值
        List<Camera> cameras = cameraTransactionMapper.list();
        if (!cameras.isEmpty()){
            String[] cameraNames = getCameraDTO.getCam_names().clone();
            GetCameraVO.Url[] cameraInfo;//一个Url类的数组 数组里面的元素还没有被分配内存
            cameraInfo = new GetCameraVO.Url[cameras.size()];
            for (int i =0 ; i < cameras.size() ; i++) {
                //创建数组时数组里面的元素都是null 需要给里面的元素分配内存空间 否则会空指针异常
                cameraInfo[i] = new GetCameraVO.Url();//给数组元素分配内存空间
                //先转换时间
                String startTimeStr = String.format("%02d:%02d" , cameras.get(i).getStartTime() / TimeConstant.NS_BETWEEN_S / TimeConstant.TIME_HOUR, cameras.get(i).getStartTime() / TimeConstant.NS_BETWEEN_S % TimeConstant.TIME_HOUR / TimeConstant.TIME_MINUTE);
                String endTimeStr = String.format("%02d:%02d" , cameras.get(i).getEndTime() / TimeConstant.NS_BETWEEN_S / TimeConstant.TIME_HOUR , cameras.get(i).getEndTime() / TimeConstant.NS_BETWEEN_S % TimeConstant.TIME_HOUR / TimeConstant.TIME_MINUTE);
                //把数据库中为Long类型的channel转换为Integer//再把摄像头信息装进cameraInfo
                int channel = cameras.get(i).getChannel().intValue();
                cameraInfo[i].setCaId(cameras.get(i).getCaId());
                cameraInfo[i].setChannel(channel);
                cameraInfo[i].setEndTime(endTimeStr);
                cameraInfo[i].setIp(cameras.get(i).getIp());
                cameraInfo[i].setName(cameras.get(i).getName());
                cameraInfo[i].setPasswd(cameras.get(i).getPasswd());
                cameraInfo[i].setPort(cameras.get(i).getPort());
                cameraInfo[i].setStartTime(startTimeStr);
                cameraInfo[i].setUser(cameras.get(i).getUser());
            }
            //判断是否是要返回全部的摄像头信息 前端过来的数组为空或者数组里面只有一个"all"
            //否则只返回特定的几个摄像头信息
            if (cameraNames.length == 0 || cameraNames[0].equals("all")){
                return cameraInfo;
            }
            else {
                //根据传过来的摄像头名字个数创建数组
                int length = cameraNames.length;
                GetCameraVO.Url[] speCameraInfo = new GetCameraVO.Url[length];
                //遍历cameraInfo 找到与查询名字对应的信息
                for (int i = 0 ; i < length ; i++){
                    for (GetCameraVO.Url url : cameraInfo) {
                        //如果要查询名字的第i个与cameraInfo的第j个匹配成功
                        if (cameraNames[i].equals(url.getName())) {
                            speCameraInfo[i] = url;//它们都是Url类的,可以直接赋值
                        }
                    }
                }
                return speCameraInfo;
            }
        }
        else
            return null;
    }

    /**
     * 修改摄像头数据
     *
     * @param updateCameraDTO
     */
    @Override
    public void updateCamera(UpdateCameraDTO updateCameraDTO) {
        String cameraName = updateCameraDTO.getName();
        boolean isExist = false;
        int counter = 0;
        //先根据传入名称判断是否存在该摄像头数据,不存在则抛出异常进行处理
        List<Camera> cameras = cameraTransactionMapper.list();
        for (int i = 0 ; i < cameras.size() ; i++){
            if (cameras.get(i).getName().equals(cameraName)) {
                counter = i;//表示要找的是第counter个摄像头
                isExist = true;
                break;
            }
        }
        //存在则处理相关参数，将时间转换为可存储的时间戳形式
        if (isExist){
            String startTimeStr = updateCameraDTO.getStartTime();
            String endTimeStr = updateCameraDTO.getEndTime();
            DateFormat startTimeFormat = new SimpleDateFormat("HH:mm");//规定时间格式
            DateFormat endTimeFormat = new SimpleDateFormat("HH:mm");//规定时间格式
            Date startTime0;//存储时间
            Date endTime0;//存储时间
            try {
                startTime0 = startTimeFormat.parse(startTimeStr);//把startTimeStr转换为规定格式的时间
                endTime0 = endTimeFormat.parse(endTimeStr);//把endTimeStr转换为规定格式的时间
            }catch (ParseException e){
                throw new FormatException(MessageConstant.FORMAT_ERROR);
            }
            //开始转换时间为unix时间戳 纳秒
            Long starTime = (startTime0.getHours()*TimeConstant.TIME_HOUR + startTime0.getMinutes()*TimeConstant.TIME_MINUTE)*TimeConstant.NS_BETWEEN_S;
            Long endTime = (endTime0.getHours()*TimeConstant.TIME_HOUR + endTime0.getMinutes()*TimeConstant.TIME_MINUTE)*TimeConstant.NS_BETWEEN_S;
            //修改更新Camera数据对象
            cameras.get(counter).setStartTime(starTime);
            cameras.get(counter).setEndTime(endTime);
            Camera camera = Camera.builder()//只修改一部分信息 不用把Camera类里面的所有属性都构建完
                    .name(cameraName)
                    .startTime(starTime)
                    .endTime(endTime)
                    .build();
            //调用mapper接口进行数据的更新
            cameraTransactionMapper.update(camera);
        }
        else//不存在表示有异常:不存在
            throw new ExistException(MessageConstant.NOT_EXIST);
    }

    /**
     * 删除摄像头
     *
     * @param deleteCameraDTO
     */
    @Override
    public void deleteCamera(DeleteCameraDTO deleteCameraDTO) {
        //先判断传入的参数是否为空
        String name = deleteCameraDTO.getName();
        boolean isExist = false;
        if (name == null)
            throw new FormatException(MessageConstant.FORMAT_ERROR);
        //判断要删除的摄像头是否存在
        List<Camera> cameras = cameraTransactionMapper.list();
        for (Camera camera : cameras){
            if (name.equals(camera.getName())) {
                isExist = true;
                break;
            }
        }
        //如果不存在 报错
        if (!isExist){
            throw new ExistException(MessageConstant.NOT_EXIST);
        }
        //调用mapper层接口进行删除
        cameraTransactionMapper.deleteByName(name);
    }
}
