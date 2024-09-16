package com.softdev.system.controller;

import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONUtil;
import com.softdev.system.DAO.RequestInfo;
import com.softdev.system.service.SparkService;
import com.softdev.system.util.ReturnUtil;
import com.softdev.system.util.ValueUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class AiController {

    @Autowired
    private ValueUtil valueUtil;
    @Autowired
    private SparkService sparkService;

    private Map<String,String> uploadMap= new HashMap<>(16);

    @GetMapping("/index")
    public ModelAndView indexPage() {
        return new ModelAndView("index").addObject("value",valueUtil);
    }
    @PostMapping("/generate")
    public Object generate(@RequestBody RequestInfo requestInfo) throws IOException {
        log.info(JSONUtil.toJsonStr(requestInfo));
//        sparkService.sendStreamtoAI_Xinghuo(requestInfo.getQuestion());
        return ReturnUtil.DATA(sparkService.sendMesToAI_XingHuo(requestInfo.getQuestion()));
//        return ReturnUtil.SUCCESS();
    }
    @PostMapping("/upload")
    public Object upload(@RequestParam("file") MultipartFile file) throws IOException {
        String key = UUID.fastUUID().toString();
        uploadMap.put(key,new String(file.getBytes()));
        log.info("文件上传成功= {} Key={}",file.getOriginalFilename(),key);
        return ReturnUtil.DATA(key);
    }
}
