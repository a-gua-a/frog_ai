package cn.nuist.frog_ai.pojo.vo;

import cn.nuist.frog_ai.pojo.entity.Sentence;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ConversationDetailVO {

    List<Sentence> sentences;

}
