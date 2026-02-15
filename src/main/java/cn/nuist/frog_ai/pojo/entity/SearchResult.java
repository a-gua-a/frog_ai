package cn.nuist.frog_ai.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchResult {

    private int ns;

    private String title;

    private int pageid;

    private int size;

    private int wordcount;

    private String snippet;

    private String timestamp;

}
