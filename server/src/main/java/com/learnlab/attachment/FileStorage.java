package com.learnlab.attachment;

import com.learnlab.common.BusinessException;
import com.learnlab.common.ErrorCode;
import com.learnlab.config.AppProperties;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Component
public class FileStorage {

  private final Path root;

  public FileStorage(AppProperties properties) {
    this.root = Path.of(properties.uploadDir()).toAbsolutePath().normalize();
  }

  public Stored store(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new BusinessException(ErrorCode.BAD_REQUEST, "请选择文件");
    }
    String original = cleanName(file.getOriginalFilename());
    String stored = UUID.randomUUID() + "_" + original;
    Path target = resolve(stored);
    try {
      Files.createDirectories(root);
      try (InputStream input = file.getInputStream()) {
        Files.copy(input, target, StandardCopyOption.REPLACE_EXISTING);
      }
      return new Stored(original, stored, file.getContentType(), file.getSize());
    } catch (IOException exception) {
      throw new BusinessException(ErrorCode.BAD_REQUEST, "保存文件失败");
    }
  }

  public Resource load(String storedName) {
    Path path = resolve(storedName);
    if (!Files.exists(path)) {
      throw new BusinessException(ErrorCode.NOT_FOUND, "文件不存在");
    }
    return new FileSystemResource(path);
  }

  public void delete(String storedName) {
    try {
      Files.deleteIfExists(resolve(storedName));
    } catch (IOException exception) {
      throw new BusinessException(ErrorCode.BAD_REQUEST, "删除文件失败");
    }
  }

  private Path resolve(String storedName) {
    Path path = root.resolve(storedName).normalize();
    if (!path.startsWith(root)) {
      throw new BusinessException(ErrorCode.BAD_REQUEST, "文件名不合法");
    }
    return path;
  }

  private static String cleanName(String original) {
    String name = (original == null || original.isBlank()) ? "file" : Path.of(original).getFileName().toString();
    String cleaned = name.replaceAll("[^a-zA-Z0-9._\\-]", "_");
    if (cleaned.isBlank() || cleaned.equals(".") || cleaned.equals("..")) {
      return "file";
    }
    return cleaned.length() > 80 ? cleaned.substring(0, 80) : cleaned;
  }

  public record Stored(String originalName, String storedName, String contentType, long sizeBytes) {
  }
}
