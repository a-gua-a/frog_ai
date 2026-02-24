package cn.nuist.frog_ai.pojo.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class Sentence {

    public static final String USER = "user";

    public static final String BOT = "bot";

    public static final String ASSISTANT = "assistant";

    @Getter
    String role;

    @Setter
    @Getter
    String text;

    public void setRoleAsUser() {
        this.role = USER;
    }

    public void setRoleAsBot() {
        this.role = BOT;
    }

}
