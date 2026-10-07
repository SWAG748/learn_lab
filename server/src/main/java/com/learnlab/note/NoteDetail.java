package com.learnlab.note;

import com.learnlab.attachment.AttachmentView;

import java.time.LocalDateTime;
import java.util.List;

public record NoteDetail(
    Long id,
    String title,
    String content,
    boolean pinned,
    Long courseId,
    String courseTitle,
    String authorName,
    List<String> tags,
    List<AttachmentView> attachments,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
