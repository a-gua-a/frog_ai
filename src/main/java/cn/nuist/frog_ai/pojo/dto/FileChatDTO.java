package cn.nuist.frog_ai.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileChatDTO {

    List<String> fileNames;
    String conversationId;
    String message;

}
