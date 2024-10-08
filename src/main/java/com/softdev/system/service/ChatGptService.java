package com.softdev.system.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import com.softdev.system.config.AiChatGptConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ChatGptService {
    @Autowired
    AiChatGptConfig aiChatGptConfig;

    public String callWithMessage(String text, String model){
        try{
            HttpResponse response = HttpRequest.post("https://api.001hao.com/v1/chat/completions")
                    // 设置请求头
                    .header("Accept", "application/json")
                    .header("Authorization", "Bearer " + aiChatGptConfig.getToken())
                    .header("Content-Type", "application/json")
                    // 设置请求体
                    .body("{\"model\": \"" + model + "\",\"messages\": [{\"role\": \"user\",\"content\": \"+" + text + "+\"}]}")
                    // 执行请求
                    .execute();
            String result=response.body();
            log.error(result);
            JSONObject jsonObject=new JSONObject(result);
            JSONObject jsonObject2 = jsonObject.getJSONArray("choices").getJSONObject(0);
            result=jsonObject2.getJSONObject("message").get("content",String.class);
            log.error(result);
            // 获取响应内容
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
