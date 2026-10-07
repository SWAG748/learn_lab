package com.learnlab.attachment;

import java.time.LocalDateTime;

public record AttachmentView(
    Long id,
    String originalName,
    String contentType,
    long sizeBytes,
    LocalDateTime createdAt
) {

  public static AttachmentView from(Attachment attachment) {
    return new AttachmentView(
        attachment.getId(),
        attachment.getOriginalName(),
        attachment.getContentType(),
        attachment.getSizeBytes(),
        attachment.getCreatedAt());
  }
}
