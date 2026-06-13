package cn.nuist.frog_ai.server.service;

import cn.nuist.frog_ai.pojo.vo.FileUploadVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface FileService {

    FileUploadVO upload(MultipartFile file);

    Boolean delete(String fileName);

    List<String> listFiles();

    Boolean exists(String fileName);

    File getFile(String fileName);

    Long getFileSize(String fileName);

    String readFileContent(String fileName);
}