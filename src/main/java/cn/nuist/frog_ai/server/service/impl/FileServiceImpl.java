package cn.nuist.frog_ai.server.service.impl;

import cn.nuist.frog_ai.pojo.vo.FileUploadVO;
import cn.nuist.frog_ai.server.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    private File getStorageDir() {
        String projectPath = System.getProperty("user.dir");
        File storageDir = new File(projectPath, "src/main/resources/file");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        log.info("storageDir: {}", storageDir);
        return storageDir;
    }

    @Override
    public FileUploadVO upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return FileUploadVO.builder()
                    .uniqueFileName(null)
                    .isSuccess(false)
                    .id(null)
                    .build();
        }

        File storageDir = getStorageDir();
        String fileName = file.getOriginalFilename();
        
        String newFileName = generateUniqueFileName(fileName);
        File destFile = new File(storageDir, newFileName);

        try {
            file.transferTo(destFile);
            return FileUploadVO.builder()
                    .uniqueFileName(newFileName)
                    .isSuccess(true)
                    .id(null)
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
            return FileUploadVO.builder()
                    .uniqueFileName(null)
                    .isSuccess(false)
                    .id(null)
                    .build();
        }
    }

    private String generateUniqueFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return String.valueOf(System.currentTimeMillis());
        }
        
        int dotIndex = fileName.lastIndexOf('.');
        String name = fileName;
        String extension = "";
        
        if (dotIndex > 0) {
            name = fileName.substring(0, dotIndex);
            extension = fileName.substring(dotIndex);
        }
        
        return name + "_" + System.currentTimeMillis() + extension;
    }

    @Override
    public Boolean delete(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return false;
        }

        File storageDir = getStorageDir();
        File file = new File(storageDir, fileName);

        if (file.exists() && file.isFile()) {
            return file.delete();
        }
        return false;
    }

    @Override
    public List<String> listFiles() {
        List<String> fileNames = new ArrayList<>();
        File storageDir = getStorageDir();

        if (storageDir.exists() && storageDir.isDirectory()) {
            File[] files = storageDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        fileNames.add(file.getName());
                    }
                }
            }
        }
        return fileNames;
    }

    @Override
    public Boolean exists(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return false;
        }

        File storageDir = getStorageDir();
        File file = new File(storageDir, fileName);
        return file.exists() && file.isFile();
    }

    @Override
    public File getFile(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return null;
        }

        File storageDir = getStorageDir();
        File file = new File(storageDir, fileName);
        return file.exists() && file.isFile() ? file : null;
    }

    @Override
    public Long getFileSize(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return null;
        }

        File storageDir = getStorageDir();
        File file = new File(storageDir, fileName);
        return file.exists() && file.isFile() ? file.length() : null;
    }

    @Override
    public String readFileContent(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return null;
        }

        File storageDir = getStorageDir();
        File file = new File(storageDir, fileName);

        if (!file.exists() || !file.isFile()) {
            return null;
        }

        try {
            return Files.readString(file.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}