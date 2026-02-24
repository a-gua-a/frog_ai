package cn.nuist.frog_ai.server.config;

import cn.nuist.frog_ai.common.constant.AiBaseConstant;
import cn.nuist.frog_ai.server.tools.WeatherInquiryTools;
import cn.nuist.frog_ai.server.tools.WikiTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AIConfiguration {

    @Autowired
    private VectorStore vectorStore;
    @Autowired
    private WeatherInquiryTools weatherInquiryTools;
    @Autowired
    private WikiTools wikiTools;
    @Autowired
    private JdbcChatMemoryRepository jdbcChatMemoryRepository;


    @Bean
    public ChatClient chatClient(OpenAiChatModel openAiChatModel) {

        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(jdbcChatMemoryRepository)
                .maxMessages(20)
                .build();
        
        return ChatClient.builder(openAiChatModel)
                .defaultSystem(AiBaseConstant.baseRoleDescription)
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        QuestionAnswerAdvisor.builder(vectorStore)
                                .searchRequest(SearchRequest.builder()
                                        .similarityThreshold(0.5)
                                        .topK(1)
                                        .build())
                                .build(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .defaultTools(weatherInquiryTools, wikiTools)
                .build();
    }
}
