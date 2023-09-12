package com.glimmer.controller.interactFront;

import com.glimmer.constant.BoxStatusConstant;
import com.glimmer.constant.MessageConstant;
import com.glimmer.dto.AddBoxDTO;
import com.glimmer.dto.BoxDTO;
import com.glimmer.result.Result;
import com.glimmer.service.BoxTransactionService;
import com.glimmer.vo.GetBoxVO;
import com.glimmer.vo.StatusVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/*
Controller 控制层
BoxTransactionController
这个部分是对于检测框的操作，用到的数据库表是box,对应的结构体是Box
功能上有：
	1、检测框增加: url为: /box/ 对应的handler函数为: AddBox
	2、检测框删除: url为: /box/ 对应的handler函数为: DeleteBox
	3、检测框获取: url为: /box/ 对应的handler函数为: GetBox
*/
@RestController
@RequestMapping("/box")
@Slf4j
public class BoxTransactionController {
    //依赖注入BoxTransactionService，可调用其相关接口方法
    @Autowired
    private BoxTransactionService boxTransactionService;

/*
AddBox 函数用于添加检测框：注册的路由url为: /box/
请求参数：

	{
	    "caId": int,
	    "leftUp": "string",
	    "rightDown": "string"
	}

响应部分：
只有status和message两个字段，其中status int:0表示成功，1表示失败 9表示系统内部错误
检测框可以有很多个，每次只是添加一个，所以数据库校验唯一性不重复的时候，是caId以及leftUp和rightDown的组合不能重复
*/
    @PostMapping //Controller这部分不需要你写，你只需要写service和mapper层
    public Result<StatusVO> AddBox(@RequestBody AddBoxDTO addBoxDTO) {
        log.info("添加检测框：{}",addBoxDTO);
        boxTransactionService.addBox(addBoxDTO);
        StatusVO statusVO = StatusVO.builder()
                .status(BoxStatusConstant.SUCCESS)
                .message(MessageConstant.ADD_SUCCESS)
                .build();
        return Result.success(statusVO,MessageConstant.ADD_SUCCESS);
    }

/*
DeleteBox 函数用于删除检测框：注册的路由url为: /box/
请求参数：
caId int用于指定删除哪个摄像头的检测框
返回参数：
status: int 0表示成功，1表示失败 9表示系统内部错误
message: string 用于返回错误信息
*/
    @DeleteMapping
    public Result<StatusVO> DeleteBox(@RequestBody BoxDTO boxDTO) {
        log.info("根据caId删除检测框：{}", boxDTO);
        boxTransactionService.deleteBox(boxDTO);
        StatusVO statusVO = StatusVO.builder()
                .status(BoxStatusConstant.SUCCESS)
                .message(MessageConstant.DELETE_SUCCESS)
                .build();
        return Result.success(statusVO,MessageConstant.DELETE_SUCCESS);
    }

/*
GetBox 函数用于获取检测框：注册的路由url为: /box/
请求参数：
caId int用于指定获取哪个摄像头的检测框，这里是返回caId对应的所有检测框,返回格式是"leftUp,rightDown|leftUp,rightDown|leftUp,rightDown"的形式
返回参数：
status: int 0表示成功，1表示失败 9表示系统内部错误
message: string 用于返回错误信息
box: string 用于返回检测框的信息返回的字符串是"leftUp,rightDown|leftUp,rightDown|leftUp,rightDown"的形式
*/
    //这部分需要你自己完成
    @GetMapping
    public Result<GetBoxVO> getBox(@RequestBody BoxDTO boxDTO) {
        log.info("根据caId接收检测框数据:{}",boxDTO);
        String box = boxTransactionService.getBox(boxDTO);
        //如果返回的box为空 说明caId对应的摄像头没有检测框
        if (box.isEmpty()) {
            GetBoxVO getBoxVO = GetBoxVO.builder()
                    .box(box)
                    .status(BoxStatusConstant.SUCCESS)
                    .message(MessageConstant.BOX_NOT_FOUND)
                    .build();
            return Result.success(getBoxVO,MessageConstant.GET_SUCCESS);
        }
        else {
            GetBoxVO getBoxVO = GetBoxVO.builder()
                    .box(box)
                    .status(BoxStatusConstant.SUCCESS)
                    .message(MessageConstant.GET_SUCCESS)
                    .build();
            return Result.success(getBoxVO,MessageConstant.GET_SUCCESS);
        }
    }
}
