package com.learnlab.dashboard;

import com.learnlab.course.CourseRepository;
import com.learnlab.note.NoteRepository;
import com.learnlab.note.NoteSummary;
import com.learnlab.task.StudyTaskRepository;
import com.learnlab.task.TaskStatus;
import com.learnlab.task.TaskView;
import com.learnlab.user.AuthService;
import com.learnlab.user.UserAccount;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class DashboardService {

  private final CourseRepository courses;
  private final NoteRepository notes;
  private final StudyTaskRepository tasks;
  private final AuthService authService;

  public DashboardService(
      CourseRepository courses,
      NoteRepository notes,
      StudyTaskRepository tasks,
      AuthService authService) {
    this.courses = courses;
    this.notes = notes;
    this.tasks = tasks;
    this.authService = authService;
  }

  @Transactional(readOnly = true)
  public DashboardView current() {
    UserAccount user = authService.requireCurrent();
    Long userId = user.getId();
    LocalDate today = LocalDate.now();
    List<NoteSummary> recent = notes.findTop5ByAuthorIdOrderByUpdatedAtDesc(userId).stream()
        .map(NoteSummary::from)
        .toList();
    List<TaskView> overdue = tasks.findOverdue(userId, TaskStatus.DONE, today).stream()
        .limit(5)
        .map(TaskView::from)
        .toList();
    return new DashboardView(
        courses.countByOwnerId(userId),
        notes.countByAuthorId(userId),
        tasks.countByAssigneeIdAndStatus(userId, TaskStatus.TODO),
        tasks.countByAssigneeIdAndStatus(userId, TaskStatus.DOING),
        tasks.countByAssigneeIdAndStatus(userId, TaskStatus.DONE),
        tasks.countOverdue(userId, TaskStatus.DONE, today),
        recent,
        overdue);
  }
}
