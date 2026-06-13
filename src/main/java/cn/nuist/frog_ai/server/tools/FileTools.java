package cn.nuist.frog_ai.server.tools;

import cn.nuist.frog_ai.server.service.FileService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FileTools {

    @Autowired
    private FileService fileService;

    /**
     * 读取文件内容
     */
    @Tool(description = "根据文件名称读取文件内容")
    public String readContent(@ToolParam(description = "文件名称") String uniqueFileName) {
        return fileService.readFileContent(uniqueFileName);
    }
}
