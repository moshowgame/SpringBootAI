package com.softdev.system.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softdev.system.config.AiChatGptConfig;
import com.theokanning.openai.client.OpenAiApi;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import retrofit2.Retrofit;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.theokanning.openai.service.OpenAiService.*;

@Slf4j
@Service
public class ChatGptSteamService {

    @Autowired
    AiChatGptConfig config;

    /**
     * 流式对话
     * 注：必须使用异步处理（否则发送消息不会及时返回前端）
     *
     * @param prompt     输入消息
     * @param sseEmitter SSE对象
     */
    @Async
    public void streamChatCompletion(String prompt, SseEmitter sseEmitter) {
        log.info("发送消息：" + prompt);
        final List<ChatMessage> messages = new ArrayList<>();
        final ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), prompt);
        messages.add(systemMessage);
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                .builder()
                .model("gpt-3.5-turbo")
                .messages(messages)
                .n(1)
//                .maxTokens(500)
                .logitBias(new HashMap<>())
                .build();

        //流式对话（逐Token返回）
        StringBuilder receiveMsgBuilder = new StringBuilder();

        String proxyHost="https://api.wlai.vip/v1/chat/completions";
        int proxyPort=445;
        OpenAiService service = buildOpenAiService(config.getTokenNew(), proxyHost, proxyPort);
        service.streamChatCompletion(chatCompletionRequest)
                //正常结束
                .doOnComplete(() -> {
                    log.info("连接结束");

                    //发送连接关闭事件，让客户端主动断开连接避免重连
                    sendStopEvent(sseEmitter);

                    //完成请求处理
                    sseEmitter.complete();
                })
                //异常结束
                .doOnError(throwable -> {
                    log.error("连接异常", throwable);

                    //发送连接关闭事件，让客户端主动断开连接避免重连
                    sendStopEvent(sseEmitter);

                    //完成请求处理携带异常
                    sseEmitter.completeWithError(throwable);
                })
                //收到消息后转发到浏览器
                .blockingForEach(x -> {
                    ChatCompletionChoice choice = x.getChoices().get(0);
                    log.debug("收到消息：" + choice);
                    if (StringUtils.isEmpty(choice.getFinishReason())) {
                        //未结束时才可以发送消息（结束后，先调用doOnComplete然后还会收到一条结束消息，因连接关闭导致发送消息失败:ResponseBodyEmitter has already completed）
                        sseEmitter.send(choice.getMessage());
                    }
                    String content = choice.getMessage().getContent();
                    content = content == null ? StringUtils.EMPTY : content;
                    receiveMsgBuilder.append(content);
                });
        log.info("收到的完整消息：" + receiveMsgBuilder);
    }

    /**
     * 发送连接关闭事件，让客户端主动断开连接避免重连
     *
     * @param sseEmitter
     * @throws IOException
     */
    private static void sendStopEvent(SseEmitter sseEmitter) throws IOException {
        sseEmitter.send(SseEmitter.event().name("stop").data(""));
    }


    /**
     * 构建OpenAiService
     *
     * @param token     API_KEY
     * @param proxyHost 代理域名
     * @param proxyPort 代理端口号
     * @return OpenAiService
     */
    private OpenAiService buildOpenAiService(String token, String proxyHost, int proxyPort) {
        //构建HTTP代理
        Proxy proxy = null;
        if (StringUtils.isNotBlank(proxyHost)) {
            proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
        }
        //构建HTTP客户端
        OkHttpClient client = defaultClient(token, Duration.of(60, ChronoUnit.SECONDS))
                .newBuilder()
                .proxy(proxy)
                .build();
        ObjectMapper mapper = defaultObjectMapper();
        Retrofit retrofit = defaultRetrofit(client, mapper);
        OpenAiApi api = retrofit.create(OpenAiApi.class);
        return new OpenAiService(api, client.dispatcher().executorService());
    }
}
