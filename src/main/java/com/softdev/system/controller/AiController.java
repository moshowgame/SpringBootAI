package com.softdev.system.controller;

import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONUtil;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.softdev.system.DAO.RequestInfo;
import com.softdev.system.service.AliyunQianWenService;
import com.softdev.system.service.OcrService;
import com.softdev.system.service.SparkService;
import com.softdev.system.util.ReturnUtil;
import com.softdev.system.util.ValueUtil;
import io.github.briqt.spark4j.constant.SparkApiVersion;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    @Autowired
    private OcrService ocrService;
    @Autowired
    private AliyunQianWenService aliyunQianWenService;

    private Map<String,String> uploadMap= new HashMap<>(16);

    @GetMapping("/index")
    public ModelAndView indexPage() {
        return new ModelAndView("index").addObject("value",valueUtil);
    }

    @PostMapping("/generate")
    public Object generate(@RequestBody RequestInfo requestInfo) throws IOException, NoApiKeyException, InputRequiredException {
        log.info(JSONUtil.toJsonStr(requestInfo));
        //by https://hengkai.blog.csdn.net/
        //根据模型获取结果
        if(StringUtils.isEmpty(requestInfo.getEngine())||"spark3max".equals(requestInfo.getEngine())){
            //星火MAX
            return ReturnUtil.DATA(sparkService.sendMesToAI_XingHuo(requestInfo.getQuestion(), SparkApiVersion.V3_5));
        }else if("spark4ultra".equals(requestInfo.getEngine())){
            //星火Ultra
            return ReturnUtil.DATA(sparkService.sendMesToAI_XingHuo(requestInfo.getQuestion(), SparkApiVersion.V4_0));
        }else if("test".equals(requestInfo.getEngine())){
            //For TEST only
            return ReturnUtil.DATA(testStr());
        }else if("gpt4o".equals(requestInfo.getEngine())){
            return ReturnUtil.ERROR("NOT SUPPORTED");
        }else if ("aliyunQianWenMax".equals(requestInfo.getEngine())) {
            //通义千问MAX
            return ReturnUtil.DATA(aliyunQianWenService.callWithMessageReturnStrting(requestInfo.getQuestion(),"qwen-max"));
        }else if ("aliyunQianWenPlus".equals(requestInfo.getEngine())) {
            //通义千问PLUS
            return ReturnUtil.DATA(aliyunQianWenService.callWithMessageReturnStrting(requestInfo.getQuestion(),"qwen-plus"));
        } else {
            return ReturnUtil.ERROR("UNKNOWN ENGINE");
        }
//        return ReturnUtil.SUCCESS();
    }

    @PostMapping("/upload")
    public Object upload(@RequestParam("file") MultipartFile file) throws IOException {
        String key = UUID.fastUUID().toString();
        uploadMap.put(key,new String(file.getBytes()));
        log.info("文件上传成功= {} Key={}",file.getOriginalFilename(),key);
        return ReturnUtil.DATA(key);
    }
    @PostMapping("/ocr")
    public Object ocr(@RequestParam("file") MultipartFile file) throws IOException {
        String result = ocrService.recognizeText(file);
        log.info("文件识别成功= {} TEXT={}",file.getOriginalFilename(),result);
        return ReturnUtil.DATA(result);
    }
    private String testStr(){
       return "解：已知$$a-b=2$$，\n" +
                "\n" +
                "$$\\therefore   \\dfrac{{(1-a)}^{2}}{b}-\\dfrac{{(1+b)}^{2}}{a}=\\dfrac{a{(1-a)}^{2}-b{(1+b)}^{2}}{ab}=\\dfrac{a-{a}^{3}-b+{b}^{3}}{ab}$$\n" +
                "\n" +
                "$$=\\dfrac{a-b-({a}^{3}-{b}^{3})}{ab}=\\dfrac{2-({a}^{3}-{b}^{3})}{ab}=4$$，\n" +
                "\n" +
                "$$\\therefore   {a}^{3}-{b}^{3}=2-4ab$$，\n" +
                "\n" +
                "又$$\\because   {(a-b)}^{2}=4$$，\n" +
                "\n" +
                "$$\\therefore   {(a+b)}^{2}=8ab$$，\n" +
                "\n" +
                "$$\\because   a>0,b>0$$，\n" +
                "\n" +
                "$$\\therefore   a+b=2\\sqrt{2ab}$$，\n" +
                "\n" +
                "$$\\therefore    {a}^{3}-{b}^{3}=2-4ab=(a-b)({a}^{2}+ab+{b}^{2})$$\n" +
                "\n" +
                "$$=(a-b)[{(a+b)}^{2}-3ab]$$\n" +
                "\n" +
                "$$=2[{(2\\sqrt{2ab})}^{2}-3ab]$$\n" +
                "\n" +
                "$$=2(8ab-3ab)=10ab$$，\n" +
                "\n" +
                "即$$10ab=2-4ab$$，解得$$ab=\\dfrac{1}{7}$$，\n" +
                "$$\\therefore   {a}^{3}-{b}^{3}=2-4ab=2-4\\times \\dfrac{1}{7}=\\dfrac{10}{7}.$$";
    }
}
