package com.softdev.system.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class AiChatGptConfig {
    @Value("${ai.chatgpt.hao.token}")
    String token;
}
