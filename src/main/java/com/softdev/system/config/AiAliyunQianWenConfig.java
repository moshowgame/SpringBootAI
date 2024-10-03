package com.softdev.system.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class AiAliyunQianWenConfig {
    @Value("${ai.aliyun.qianwen.ApiKey}")
    String apiKey;
}
