package com.softdev.system.service;

import cn.hutool.json.JSONUtil;
import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.softdev.system.config.AiAliyunQianWenConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Slf4j
@Service
public class AliyunQianWenService {

//    https://help.aliyun.com/zh/model-studio/user-guide/text-generation?spm=0.0.0.i2#809cfd466dvb4
    @Autowired
    AiAliyunQianWenConfig aiAliyunQianWenConfig;
    public String callWithMessageReturnStrting(String text,String model) throws ApiException, NoApiKeyException, InputRequiredException {
        GenerationResult generationResult = callWithMessage (text,model);
        log.info("AliyunQianWenService-callWithMessageReturnStrting: {}", JSONUtil.toJsonStr(generationResult));
        return generationResult.getOutput().getChoices().get(0).getMessage().getContent();
    }
    public GenerationResult callWithMessage(String text,String model) throws ApiException, NoApiKeyException, InputRequiredException {
        Generation gen = new Generation();
        Message systemMsg = Message.builder()
                .role(Role.SYSTEM.getValue())
                .content("作为一个学生，你需要问一些作业题")
                .build();
        Message userMsg = Message.builder()
                .role(Role.USER.getValue())
                .content(text)
                .build();
        GenerationParam param = GenerationParam.builder()
                .apiKey(aiAliyunQianWenConfig.getApiKey())
                .model(model)
                //"qwen-max"
                .messages(Arrays.asList(systemMsg, userMsg))
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .build();
        return gen.call(param);
    }
}
