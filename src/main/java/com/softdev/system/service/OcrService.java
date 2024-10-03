package com.softdev.system.service;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.aliyun.ocr_api20210707.Client;
import com.aliyun.ocr_api20210707.models.RecognizeEduQuestionOcrRequest;
import com.aliyun.ocr_api20210707.models.RecognizeEduQuestionOcrResponse;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import com.softdev.system.config.AiAliyunOcrConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;


@Slf4j
@Service
public class OcrService {

    @Autowired
    AiAliyunOcrConfig aiAliyunOcrConfig;

    Client client=null;
    /**
     * <b>description</b> :
     * <p>使用AK&amp;SK初始化账号Client</p>
     * @return Client
     *
     * @throws Exception
     */
     Client createClient() throws Exception {
        // 工程代码泄露可能会导致 AccessKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考。
        // 建议使用更安全的 STS 方式，更多鉴权访问方式请参见：https://help.aliyun.com/document_detail/378657.html。
        Config config = new Config()
                // 必填，请确保代码运行环境设置了环境变量 ALIBABA_CLOUD_ACCESS_KEY_ID。
                .setAccessKeyId(aiAliyunOcrConfig.getAccessKeyId())
                // 必填，请确保代码运行环境设置了环境变量 ALIBABA_CLOUD_ACCESS_KEY_SECRET。
                .setAccessKeySecret(aiAliyunOcrConfig.getAccessKeySecret());
        // Endpoint 请参考 https://api.aliyun.com/product/ocr-api
        config.endpoint = "ocr-api.cn-hangzhou.aliyuncs.com";
        return new Client(config);
    }

    Client getClient() throws Exception {
         if(client==null) {client=createClient();}
         return client;
    }
    /**
     * 识别图片中的文字
     * @param imageFile 图片文件
     * @return 文字信息
     */
    public String recognizeText(MultipartFile imageFile) throws IOException {
        InputStream bodyStream = imageFile.getInputStream();
                // 需要安装额外的依赖库，直接点击下载完整工程即可看到所有依赖。
        RecognizeEduQuestionOcrRequest recognizeEduQuestionOcrRequest = new RecognizeEduQuestionOcrRequest()
                .setBody(bodyStream);
        RuntimeOptions runtime = new RuntimeOptions();
        try {
            // 复制代码运行请自行打印 API 的返回值
            RecognizeEduQuestionOcrResponse recognizeEduQuestionOcrResponse = getClient().recognizeEduQuestionOcrWithOptions(recognizeEduQuestionOcrRequest, runtime);
            //String result = recognizeEduQuestionOcrResponse.getBody();
            String resultJsonStr=recognizeEduQuestionOcrResponse.getBody().getData();
            JSONObject jsonObject = JSONUtil.parseObj(resultJsonStr);
            String resultStr=jsonObject.getStr("content");
            log.info("OCR result:{}",resultStr );
            return resultStr;
        } catch (TeaException error) {
            // 此处仅做打印展示，请谨慎对待异常处理，在工程项目中切勿直接忽略异常。
            // 错误 message
            log.error(error.getMessage());
            // 诊断地址
            log.error((String) error.getData().get("Recommend"));
            com.aliyun.teautil.Common.assertAsString(error.message);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            // 此处仅做打印展示，请谨慎对待异常处理，在工程项目中切勿直接忽略异常。
            // 错误 message
            log.error(error.getMessage());
            // 诊断地址
            log.error((String) error.getData().get("Recommend"));
            com.aliyun.teautil.Common.assertAsString(error.message);
        }
        return "";
    }
}