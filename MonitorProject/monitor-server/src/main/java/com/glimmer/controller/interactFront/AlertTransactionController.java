package com.glimmer.controller.interactFront;

import com.glimmer.constant.AlertStatusConstant;
import com.glimmer.constant.MessageConstant;
import com.glimmer.dto.AcceptAlertDTO;
import com.glimmer.dto.DeleteAlertDTO;
import com.glimmer.dto.GetAlertDTO;
import com.glimmer.dto.GetSingleAlertDTO;
import com.glimmer.result.Result;
import com.glimmer.service.AlertTransactionService;
import com.glimmer.vo.GetAlertVO;
import com.glimmer.vo.StatusVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/*
Controller 控制层
AlertTransactionController
报警信息交互，提供的功能是：
1. 报警信息获取	GET		/alert 	GetAlert
2. 报警信息删除	DELETE	/alert  DeleteAlert
3. 报警资源(视频or图片)获取	GET		/alert/src  GetAlertSrc
*/
@RestController("interactFrontAlertTransactionController")//括号里面表示SpringBean的名称
@Slf4j
@RequestMapping("/alert")
public class AlertTransactionController {
    @Autowired
    private AlertTransactionService alertTransactionService;

/*
GetAlert 函数用于获取报警信息：注册的路由url为: /alert
| 请求参数   |          |                                            |
| ---------- | -------- | ------------------------------------------ |
| 参数名     | 数据类型 | 说明                                       |
| startDate  | string   | 需要搜索的开始日期，全部搜索输入“”         |
| endDate    | string   | 需要搜索的结束日期，全部搜索输入“”         |
| cameraName | list     | 需要搜索的摄像头名称列表，全部搜索输入[“”] |
| type       | list     | 需要搜索的检测类型列表，全部搜索输入[“”]   |
返回参数：
  data里面除了标准的status和message外，使用infos字段返回报警信息列表，infos里面包含了报警信息的所有字段(主键id除外),这些字段来源于Alert结构体
有["caId"]:是alert.CaId，["type"]:是alert.Type，["alertTime"]:是alert.AlertTime，["videoPath"]:是alert.PathVideo，["photoPath"]:是alert.PathPhoto
status 0:成功 1:请求失败 9.系统错误
*/
    @GetMapping
    public Result<GetAlertVO> GetAlert(@RequestBody GetAlertDTO getAlertDTO) {
        log.info("获取报警信息:{}",getAlertDTO);
        //调用业务层方法获得要找的报警信息的列表
        //AlertVO类的列表 存了符合搜索情况的报警信息的列表
        List<GetAlertVO.AlertVO> infos = alertTransactionService.getAlert(getAlertDTO);
        GetAlertVO getAlertVO = GetAlertVO.builder()
                .infos(infos)
                .status(AlertStatusConstant.SUCCESS)
                .message(MessageConstant.SUCCESS)
                .build();
        return Result.success(getAlertVO,MessageConstant.SUCCESS);
    }


    /*
    获取单个报警信息 用于报警信息增加后主动推送给用户
     */
    public void GetSingleAlert(GetSingleAlertDTO getSingleAlertDTO) {
        log.info("获取单个报警信息:{}",getSingleAlertDTO);
    }



/*
DeleteAlert 删除报警信息、
参数：pathVideos、pathPhotos
pathVideos：视频路径列表,也就是既要删除数据库alert表里面的记录，也要删除视频文件
pathPhotos：图片路径列表，也就是既要删除数据库alert表里面的记录，也要删除图片文件
返回：status、message
status：0表示成功，1表示失败，9表示系统内部错误
message：返回的信息
*/
    @DeleteMapping
    public Result<StatusVO> DeleteAlert(@RequestBody DeleteAlertDTO deleteAlertDTO){
        log.info("报警信息删除：{}",deleteAlertDTO);
        alertTransactionService.deleteAlert(deleteAlertDTO);
        StatusVO statusVO = StatusVO.builder()
                .status(AlertStatusConstant.SUCCESS)
                .message(MessageConstant.DELETE_SUCCESS)
                .build();
        return Result.success(statusVO,MessageConstant.SUCCESS);
    }

/*
GetAlertSrc 获取报警视频
参数：pathVideo 视频路径、pathPhoto 图片路径，这两个只要不为空就会返还对应信息、但是请求只能有一个不为空，如果两个都不为空，那么就会返回视频路径的文件

返回:流式传递视频/图片、这里的响应就不再是用json了，而是直接响应返回相应的二进制文件
ResponseEntity<byte[]>规定返回一个字节数组作为响应
*/
    @GetMapping("/src")
    public void GetAlertSrc(HttpServletResponse response, @RequestBody AcceptAlertDTO acceptAlertDTO) throws IOException {
        log.info("返回报警信息文件:{}",acceptAlertDTO);
        //从服务层获取文件
        File sourceFile = alertTransactionService.acceptAlert(acceptAlertDTO);
        String name = sourceFile.getName().substring(0,sourceFile.getName().lastIndexOf(".")) + ".txt";
        response.setHeader("Content-Disposition",name);
        response.setContentType("application/octet-stream");

        BufferedInputStream bufferedInputStream = null;//缓冲流
        BufferedOutputStream bufferedOutputStream = null;
        try
        {
            FileInputStream inputStream = new FileInputStream(sourceFile);//节点流
            OutputStream outputStream = response.getOutputStream();
            bufferedInputStream = new BufferedInputStream(inputStream);//缓冲流
            bufferedOutputStream = new BufferedOutputStream(outputStream);

            byte[] buf = new byte[10240];
            int len;
            while ((len = bufferedInputStream.read(buf)) != -1) {
                bufferedOutputStream.write(buf,0,len);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedOutputStream != null)
                bufferedOutputStream.close();
            if (bufferedInputStream != null)
                bufferedInputStream.close();
        }
    }

}
