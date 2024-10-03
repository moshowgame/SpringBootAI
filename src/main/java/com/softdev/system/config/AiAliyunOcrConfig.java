package com.softdev.system.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class AiAliyunOcrConfig {
    @Value("${ai.aliyun.ocr.AccessKeyId}")
    String accessKeyId;
    @Value("${ai.aliyun.ocr.AccessKeySecret}")
    String accessKeySecret;
}
