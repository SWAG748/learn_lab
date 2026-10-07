package com.learnlab.comment;

import java.time.LocalDateTime;

public record CommentView(Long id, String content, String authorName, LocalDateTime createdAt) {

  public static CommentView from(NoteComment comment) {
    return new CommentView(
        comment.getId(),
        comment.getContent(),
        comment.getAuthor().getUsername(),
        comment.getCreatedAt());
  }
}
