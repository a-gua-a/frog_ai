package cn.nuist.frog_ai.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FileUploadVO {

    String uniqueFileName;

    Boolean isSuccess;

    Integer id;
}
