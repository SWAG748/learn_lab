package com.learnlab.course;

import java.time.LocalDateTime;

public record CourseView(
    Long id,
    String title,
    String summary,
    Long ownerId,
    String ownerName,
    long noteCount,
    long taskCount,
    LocalDateTime createdAt
) {

  public static CourseView of(Course course, long noteCount, long taskCount) {
    return new CourseView(
        course.getId(),
        course.getTitle(),
        course.getSummary(),
        course.getOwner().getId(),
        course.getOwner().getUsername(),
        noteCount,
        taskCount,
        course.getCreatedAt());
  }
}
