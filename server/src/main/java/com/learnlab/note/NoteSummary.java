package com.learnlab.note;

import java.time.LocalDateTime;
import java.util.List;

public record NoteSummary(
    Long id,
    String title,
    boolean pinned,
    Long courseId,
    String courseTitle,
    String authorName,
    List<String> tags,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

  public static NoteSummary from(Note note) {
    return new NoteSummary(
        note.getId(),
        note.getTitle(),
        note.isPinned(),
        note.getCourse().getId(),
        note.getCourse().getTitle(),
        note.getAuthor().getUsername(),
        note.getTags().stream().map(tag -> tag.getName()).sorted().toList(),
        note.getCreatedAt(),
        note.getUpdatedAt());
  }
}
