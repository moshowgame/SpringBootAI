package com.softdev.system.controller;

import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONUtil;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.softdev.system.DAO.RequestInfo;
import com.softdev.system.config.AiChatGptConfig;
import com.softdev.system.service.AliyunQianWenService;
import com.softdev.system.service.ChatGptService;
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
    @Autowired
    private ChatGptService chatGptService;

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
        }else if("gpt-3.5-turbo".equals(requestInfo.getEngine())){
            //gpt-4o-mini
            //gpt-3.5-turbo
            return ReturnUtil.DATA(chatGptService.callWithMessage(requestInfo.getQuestion(),"gpt-3.5-turbo").replaceAll("frac","\\\\frac").replaceAll("\\$","\\$\\$"));
            //return ReturnUtil.ERROR("NOT SUPPORTED");
        }else if("gpt-4o-mini".equals(requestInfo.getEngine())){
            //gpt-4o-mini
            //gpt-3.5-turbo
            return ReturnUtil.DATA(chatGptService.callWithMessage(requestInfo.getQuestion(),"gpt-4o-mini").replaceAll("frac","\\\\frac").replaceAll("\\$","\\$\\$"));
            //return ReturnUtil.ERROR("NOT SUPPORTED");
        }else if("gpt-4o".equals(requestInfo.getEngine())){
            //gpt-4o-mini
            //gpt-3.5-turbo
            return ReturnUtil.DATA(chatGptService.callWithMessage(requestInfo.getQuestion(),"gpt-4o").replaceAll("frac","\\\\frac").replaceAll("\\$","\\$\\$"));
            //return ReturnUtil.ERROR("NOT SUPPORTED");
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
       return "根据已知条件，我们可以得到以下两个方程： a-b = 2 ------(1) $frac{(1 - a)^2}{b} - frac{(1 + b)^2}{a} = 4$ ------(2) 首先，我们来求解方程(1)。将a-b = 2两边同时平方，得到a² - 2ab + b² = 4。 接着，我们来求解方程(2)。将分数的除法转化为乘法，并移项得到 (1 - a)²a - (1 + b)²b = 4ab 。 将这两个方程相加，得到a² - 2ab + b² + (1 - a)²a - (1 + b)²b = 4 + 4ab 。 化简得到2a³ - 2b³ - 6ab = 4 + 4ab 。 接下来，我们将方程化为a³ - b³ 的形式。将方程两边同时加上4，得到 2a³ - 2b³ - 6ab + 4 = 4 + 4ab + 4 。 继续化简得到 2(a³ - b³) - 6ab + 4 = 8 + 4ab 。 再继续化简得到 2(a³ - b³) = 12 + 6ab 。 最后，将式子两边同时除以2，得到 a³ - b³ = 6 + 3ab 。 因此，a³ - b³ 的值为6 + 3ab。".replaceAll("frac","\\\\frac").replaceAll("\\$","\\$\\$");
//        return "解：已知$$a-b=2$$，\n" +
//                "\n" +
//                "$$\\therefore   \\dfrac{{(1-a)}^{2}}{b}-\\dfrac{{(1+b)}^{2}}{a}=\\dfrac{a{(1-a)}^{2}-b{(1+b)}^{2}}{ab}=\\dfrac{a-{a}^{3}-b+{b}^{3}}{ab}$$\n" +
//                "\n" +
//                "$$=\\dfrac{a-b-({a}^{3}-{b}^{3})}{ab}=\\dfrac{2-({a}^{3}-{b}^{3})}{ab}=4$$，\n" +
//                "\n" +
//                "$$\\therefore   {a}^{3}-{b}^{3}=2-4ab$$，\n" +
//                "\n" +
//                "又$$\\because   {(a-b)}^{2}=4$$，\n" +
//                "\n" +
//                "$$\\therefore   {(a+b)}^{2}=8ab$$，\n" +
//                "\n" +
//                "$$\\because   a>0,b>0$$，\n" +
//                "\n" +
//                "$$\\therefore   a+b=2\\sqrt{2ab}$$，\n" +
//                "\n" +
//                "$$\\therefore    {a}^{3}-{b}^{3}=2-4ab=(a-b)({a}^{2}+ab+{b}^{2})$$\n" +
//                "\n" +
//                "$$=(a-b)[{(a+b)}^{2}-3ab]$$\n" +
//                "\n" +
//                "$$=2[{(2\\sqrt{2ab})}^{2}-3ab]$$\n" +
//                "\n" +
//                "$$=2(8ab-3ab)=10ab$$，\n" +
//                "\n" +
//                "即$$10ab=2-4ab$$，解得$$ab=\\dfrac{1}{7}$$，\n" +
//                "$$\\therefore   {a}^{3}-{b}^{3}=2-4ab=2-4\\times \\dfrac{1}{7}=\\dfrac{10}{7}.$$";
    }
}
