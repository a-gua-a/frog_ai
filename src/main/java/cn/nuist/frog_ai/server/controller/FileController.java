package cn.nuist.frog_ai.server.controller;

import cn.nuist.frog_ai.pojo.vo.FileUploadVO;
import cn.nuist.frog_ai.server.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    FileService fileService;

    /**
     * 上传文件
     */
    @PostMapping(value = "/upload",produces = "application/json;charset=UTF-8")
    public FileUploadVO upload(@RequestParam("file") MultipartFile file) {
        return fileService.upload(file);
    }
}
