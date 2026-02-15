package cn.nuist.frog_ai.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Query {

    private SearchInfo searchinfo;

    private List<SearchResult> search;
}
