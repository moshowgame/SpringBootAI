package com.softdev.system.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class AiSparkConfig {
    @Value("${ai.spark.appid}")
    String appId;
    @Value("${ai.spark.apiSecret}")
    String apiSecret;
    @Value("${ai.spark.apiKey}")
    String apiKey;
}
