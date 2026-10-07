package com.learnlab.attachment;

import com.learnlab.common.ApiResponse;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

@RestController
public class AttachmentController {

  private final AttachmentService attachmentService;

  public AttachmentController(AttachmentService attachmentService) {
    this.attachmentService = attachmentService;
  }

  @PostMapping("/api/notes/{noteId}/attachments")
  public ApiResponse<AttachmentView> upload(@PathVariable Long noteId, @RequestParam("file") MultipartFile file) {
    return ApiResponse.ok(attachmentService.upload(noteId, file));
  }

  @GetMapping("/api/attachments/{id}/download")
  public ResponseEntity<org.springframework.core.io.Resource> download(@PathVariable Long id) {
    AttachmentService.Download file = attachmentService.download(id);
    MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
    if (file.contentType() != null && !file.contentType().isBlank()) {
      try {
        mediaType = MediaType.parseMediaType(file.contentType());
      } catch (Exception ignored) {
        mediaType = MediaType.APPLICATION_OCTET_STREAM;
      }
    }
    ContentDisposition disposition = ContentDisposition.attachment()
        .filename(file.filename(), StandardCharsets.UTF_8)
        .build();
    return ResponseEntity.ok()
        .contentType(mediaType)
        .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
        .body(file.resource());
  }

  @DeleteMapping("/api/attachments/{id}")
  public ApiResponse<Void> delete(@PathVariable Long id) {
    attachmentService.delete(id);
    return ApiResponse.ok(null);
  }
}
