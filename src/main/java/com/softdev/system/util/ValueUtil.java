package com.softdev.system.util;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Get Value From Application.yml
 * @author zhengkai.blog.csdn.net
 */
@Data
@Component
public class ValueUtil {
    @Value("${OEM.title}")
    public String title;

    @Value("${OEM.version}")
    public String version;

    @Value("${OEM.author}")
    public String author;

    @Value("${OEM.keywords}")
    public String keywords;

    @Value("${OEM.slogan}")
    public String slogan;

    @Value("${OEM.copyright}")
    public String copyright;

    @Value("${OEM.description}")
    public String description;

    @Value("${OEM.outputStr}")
    public String outputStr;

    @Value("${OEM.inputStr}")
    public String inputStr;
}

