package com.glimmer.service.impl;

import com.glimmer.constant.MessageConstant;
import com.glimmer.dto.AddBoxDTO;
import com.glimmer.dto.BoxDTO;
import com.glimmer.entity.Box;
import com.glimmer.entity.Camera;
import com.glimmer.exception.FormatException;
import com.glimmer.mapper.BoxTransactionMapper;
import com.glimmer.mapper.CameraTransactionMapper;
import com.glimmer.service.BoxTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * service层检测框接口的实现类
 * 在这里完成检测框相关的具体的业务功能
 */
@Service
public class BoxTransactionServiceImpl implements BoxTransactionService {

    @Autowired
    private BoxTransactionMapper boxTransactionMapper;
    @Autowired
    private CameraTransactionMapper cameraTransactionMapper;


    /**
     * 添加检测框
     * @param addBoxDTO
     */
    @Override
    public void addBox(AddBoxDTO addBoxDTO) {
        List<Box> boxes = boxTransactionMapper.list();
        //获取addBoxDTO相关参数(caId、rightDown、leftUp)
        Integer caId = addBoxDTO.getCaId();
        String rightDown = addBoxDTO.getRightDown();
        String leftUp = addBoxDTO.getLeftUp();
        //校验caId、leftUp、rightDown是否为空，为空的话返回参数格式错误
        if (caId == null || rightDown == null || leftUp == null)
            throw new FormatException(MessageConstant.FORMAT_ERROR);
        //校验caId与leftUP 和 rightDown的组合是否已经在数据库的box表里面，防止重复添加，这里应该是caId和leftUp和rightDown的组合不能重复
        for (Box box : boxes) {
            if (box.getCaId().equals(caId) && box.getRightDown().equals(rightDown) && box.getLeftUp().equals(leftUp))
                throw new FormatException(MessageConstant.ALREADY_EXIST);
        }
        //没有则向数据库添加检测框数据
        Box box0 = Box.builder()
                .caId(caId)
                .leftUp(leftUp)
                .rightDown(rightDown)
                .build();
        boxTransactionMapper.add(box0);
    }



    /**
     * 删除检测框业务
     * @param boxDTO
     */
    @Override
    public void deleteBox(BoxDTO boxDTO) {
        //获取相关参数，判空
        Integer caId = boxDTO.getCaId();
        if (caId == null) throw new FormatException(MessageConstant.FORMAT_ERROR);
        //删除box.CaId对应的box数据
        boxTransactionMapper.deleteByCaId(caId);
    }



    /**
     * 获取检测框业务
     * 返回一个字符串 表示检测框左上和右下的比例
     * @param boxDTO
     * @return
     */
    @Override
    public String getBox(BoxDTO boxDTO) {
        Integer caId = boxDTO.getCaId();
        boolean isExist = false;
        StringBuffer boxStr = new StringBuffer();
        //把所有跟这个caId匹配的box都列出来
        List<Box> boxes = boxTransactionMapper.getByCaId(caId);
        List<Camera> cameras = cameraTransactionMapper.getByCaId(caId);
        //如果不存在这个caId
        if (cameras.isEmpty())
            throw new FormatException(MessageConstant.NOT_EXIST);
        //如果caId对应的摄像头没有检测框
        if (boxes.isEmpty())
            return boxStr.toString();
        else {
            for (Box box : boxes) {
                String leftUp = box.getLeftUp();
                String rightDown = box.getRightDown();
                //如果已经存在了 前面需要加一个|
                if (isExist)
                    boxStr.append("|" + leftUp + "," + rightDown);
                else {
                    isExist = true;
                    boxStr.append(leftUp + "," + rightDown);
                }
            }
            return boxStr.toString();
        }
    }
}
