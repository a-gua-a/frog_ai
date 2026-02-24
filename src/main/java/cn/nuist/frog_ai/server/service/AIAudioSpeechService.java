package cn.nuist.frog_ai.server.service;

public interface AIAudioSpeechService {

    String toDataUrl(String filePath) throws Exception;

    void createVoice() throws Exception;

    void call(String text) throws Exception;

    String text2Audio(String text) throws Exception;
}
