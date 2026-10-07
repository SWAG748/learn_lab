package com.learnlab.task;

import com.learnlab.common.BusinessException;
import com.learnlab.common.ErrorCode;
import com.learnlab.course.Course;
import com.learnlab.course.CourseService;
import com.learnlab.user.AuthService;
import com.learnlab.user.UserAccount;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class TaskService {

  private final StudyTaskRepository tasks;
  private final CourseService courseService;
  private final AuthService authService;

  public TaskService(StudyTaskRepository tasks, CourseService courseService, AuthService authService) {
    this.tasks = tasks;
    this.courseService = courseService;
    this.authService = authService;
  }

  @Transactional(readOnly = true)
  public List<TaskView> list(Long courseId, TaskStatus status) {
    UserAccount user = authService.requireCurrent();
    return tasks.search(user.getId(), courseId, status).stream().map(TaskView::from).toList();
  }

  @Transactional
  public TaskView create(TaskRequest request) {
    StudyTask task = new StudyTask();
    task.setAssignee(authService.requireCurrent());
    apply(task, request);
    tasks.save(task);
    return TaskView.from(task);
  }

  @Transactional
  public TaskView update(Long id, TaskRequest request) {
    StudyTask task = requireOwned(id);
    apply(task, request);
    return TaskView.from(task);
  }

  @Transactional
  public TaskView changeStatus(Long id, TaskStatus status) {
    StudyTask task = requireOwned(id);
    task.setStatus(status);
    return TaskView.from(task);
  }

  @Transactional
  public void delete(Long id) {
    tasks.delete(requireOwned(id));
  }

  @Transactional(readOnly = true)
  public long countAllOverdue() {
    return tasks.countOverdue(null, TaskStatus.DONE, LocalDate.now());
  }

  private void apply(StudyTask task, TaskRequest request) {
    task.setTitle(request.title().trim());
    task.setDetail(request.detail() == null ? null : request.detail().trim());
    task.setStatus(request.status());
    task.setPriority(request.priority());
    task.setDueDate(request.dueDate());
    if (request.courseId() == null) {
      task.setCourse(null);
      return;
    }
    Course course = courseService.requireOwned(request.courseId());
    task.setCourse(course);
  }

  private StudyTask requireOwned(Long id) {
    StudyTask task = tasks.findById(id)
        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "任务不存在"));
    UserAccount user = authService.requireCurrent();
    if (!task.getAssignee().getId().equals(user.getId())) {
      throw new BusinessException(ErrorCode.FORBIDDEN, "这不是你的任务");
    }
    return task;
  }
}
