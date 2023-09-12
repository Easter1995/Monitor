package com.glimmer.webSocket;

import com.alibaba.fastjson.JSON;
import com.glimmer.vo.GetAlertVO;

import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class WebSocketCustomEncoding implements Encoder.Text<GetAlertVO> {
    @Override
    public String encode(GetAlertVO getAlertVO) {
        assert getAlertVO!=null;
        return JSON.toJSONString(getAlertVO);
    }

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }
}
