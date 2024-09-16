package com.softdev.system.service;

import io.github.briqt.spark4j.SparkClient;
import io.github.briqt.spark4j.constant.SparkApiVersion;
import io.github.briqt.spark4j.listener.SparkConsoleListener;
import io.github.briqt.spark4j.model.SparkMessage;
import io.github.briqt.spark4j.model.SparkSyncChatResponse;
import io.github.briqt.spark4j.model.request.SparkRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
/**
 * SparkService
 * 星火请求服务接口
 * @Author zhengkai.blog.csdn.net
 * **/
public class SparkService {
    private SparkClient sparkClient=null;
    {
        sparkClient=new SparkClient();
        // 设置认证信息
        // 如果你得到401 Unauthorized 则请留意apiSecret和apiKey设置是否正确！！！！！！
        sparkClient.appid="";
        sparkClient.apiSecret="";
        sparkClient.apiKey="";
    }
    /*** AI生成问题的预设条件*/
    public static final String PRECONDITION = "你作为一名学生，你想要查询学习相关问题。" ;

    public SparkRequest setSparkRequest(String content) {
        // 消息列表，可以在此列表添加历史对话记录
        List<SparkMessage> messages = new ArrayList<>();
        messages.add(SparkMessage.systemContent(PRECONDITION));
        messages.add(SparkMessage.userContent(content));
        // 构造请求
        return SparkRequest.builder()
                // 消息列表
                .messages(messages)
                // 模型回答的tokens的最大长度，非必传，默认为2048
                .maxTokens(2048)
                // 结果随机性，取值越高随机性越强，即相同的问题得到的不同答案的可能性越高，非必传，取值为[0,1]，默认为0.5
                .temperature(0.2)
                // 指定请求版本
                .apiVersion(SparkApiVersion.V3_5)
                .build();
    }
    public String sendMesToAI_XingHuo(String content) {
        // 同步调用
        SparkSyncChatResponse chatResponse = sparkClient.chatSync(setSparkRequest(content));
        String responseContent = chatResponse.getContent();
        log.info("星火AI返回的结果{}", responseContent);
        return responseContent;
    }

    public void sendStreamtoAI_Xinghuo(String content){
        // 消息列表，可以在此列表添加历史对话记录
        List<SparkMessage> messages = new ArrayList<>();
        messages.add(SparkMessage.systemContent(PRECONDITION));
        messages.add(SparkMessage.userContent(content));

        // 构造请求
        SparkRequest sparkRequest = SparkRequest.builder()
                // 消息列表
                .messages(messages)
                // 模型回答的tokens的最大长度,非必传，默认为2048。
                // V1.5取值为[1,4096]
                // V2.0取值为[1,8192]
                // V3.0取值为[1,8192]
                .maxTokens(2048)
                // 核采样阈值。用于决定结果随机性,取值越高随机性越强即相同的问题得到的不同答案的可能性越高 非必传,取值为[0,1],默认为0.5
                .temperature(0.2)
                // 指定请求版本，默认使用3.0版本
                .apiVersion(SparkApiVersion.V4_0)
                .build();

        // 使用默认的控制台监听器，流式调用；
        // 实际使用时请继承SparkBaseListener自定义监听器实现
        sparkClient.chatStream(sparkRequest, new SparkConsoleListener());
    }
}
