package com.learnlab.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TaskView(
    Long id,
    String title,
    String detail,
    TaskStatus status,
    Priority priority,
    LocalDate dueDate,
    Long courseId,
    String courseTitle,
    String assigneeName,
    boolean overdue,
    LocalDateTime createdAt
) {

  public static TaskView from(StudyTask task) {
    boolean overdue = task.getStatus() != TaskStatus.DONE
        && task.getDueDate() != null
        && task.getDueDate().isBefore(LocalDate.now());
    return new TaskView(
        task.getId(),
        task.getTitle(),
        task.getDetail(),
        task.getStatus(),
        task.getPriority(),
        task.getDueDate(),
        task.getCourse() == null ? null : task.getCourse().getId(),
        task.getCourse() == null ? null : task.getCourse().getTitle(),
        task.getAssignee().getUsername(),
        overdue,
        task.getCreatedAt());
  }
}
