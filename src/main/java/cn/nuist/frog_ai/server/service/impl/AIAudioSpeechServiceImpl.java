package cn.nuist.frog_ai.server.service.impl;

import cn.nuist.frog_ai.server.service.AIAudioSpeechService;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

@Service
@Slf4j
public class AIAudioSpeechServiceImpl implements AIAudioSpeechService {

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Value("${frog.ai.speech.model}")
    private String TARGET_MODEL;

    private static final String AUDIO_FILE = "D:\\javaProject\\frog_ai\\src\\main\\resources\\audio\\voice.mp3";
    private static final String AUDIO_MIME_TYPE = "audio/mpeg";
    private static final String PREFERRED_NAME = "voice";

    private String voiceId;

    // 生成 data URI
    @Override
    public String toDataUrl(String filePath) throws Exception {
        byte[] bytes = Files.readAllBytes(Paths.get(filePath));
        String encoded = Base64.getEncoder().encodeToString(bytes);
        return "data:" + AUDIO_MIME_TYPE + ";base64," + encoded;
    }

    // 调用 API 创建 voice
    @Override
    public void createVoice() throws Exception {

        String jsonPayload =
                "{"
                        + "\"model\": \"qwen-voice-enrollment\"," // 不要修改该值
                        + "\"input\": {"
                        +     "\"action\": \"create\","
                        +     "\"target_model\": \"" + TARGET_MODEL + "\","
                        +     "\"preferred_name\": \"" + PREFERRED_NAME + "\","
                        +     "\"audio\": {"
                        +         "\"data\": \"" + toDataUrl(AUDIO_FILE) + "\""
                        +     "}"
                        + "}"
                        + "}";

        // 以下为北京地域url，若使用新加坡地域的模型，需将url替换为：https://dashscope-intl.aliyuncs.com/api/v1/services/audio/tts/customization
        String url = "https://dashscope.aliyuncs.com/api/v1/services/audio/tts/customization";
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Authorization", "Bearer " + apiKey);
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);

        try (OutputStream os = con.getOutputStream()) {
            os.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
        }

        int status = con.getResponseCode();
        log.info("HTTP 状态码: {}", status);

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(status >= 200 && status < 300 ? con.getInputStream() : con.getErrorStream(),
                        StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            log.info("返回内容: {}", response);

            if (status == 200) {
                JsonObject jsonObj = new Gson().fromJson(response.toString(), JsonObject.class);
                voiceId =  jsonObj.getAsJsonObject("output").get("voice").getAsString();
                log.info("复刻生成的专属音色: {}", voiceId);
                return;
            }
            throw new IOException("创建语音失败: " + status + " - " + response);
        }
    }

    @Override
    public String text2Audio(String text) throws Exception {
        createVoice();
        MultiModalConversation conv = new MultiModalConversation();
        MultiModalConversationParam param = MultiModalConversationParam.builder()
                // 新加坡和北京地域的API Key不同。获取API Key：https://help.aliyun.com/zh/model-studio/get-api-key
                // 若没有配置环境变量，请用百炼API Key将下行替换为：.apiKey("sk-xxx")
                .apiKey(apiKey)
                .model(TARGET_MODEL)
                .text(text)
                .parameter("voice", voiceId)
                .build();
        MultiModalConversationResult result = conv.call(param);
        String audioUrl = result.getOutput().getAudio().getUrl();
        log.info("音频URL: {}", audioUrl);
        return audioUrl;
    }

    @Override
    public void call(String text) throws Exception {
        String audioUrl = text2Audio(text);

        // 下载音频文件到本地
        try (InputStream in = new URL(audioUrl).openStream();
             FileOutputStream out = new FileOutputStream("D:\\javaProject\\frog_ai\\src\\main\\resources\\audio\\downloaded_audio.wav")) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            log.info("音频文件已下载到本地: downloaded_audio.wav");
        } catch (Exception e) {
            log.error("下载音频文件时出错: {}", e.getMessage());
        }
    }

}
