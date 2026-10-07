package com.learnlab.course;

import com.learnlab.common.BusinessException;
import com.learnlab.common.ErrorCode;
import com.learnlab.common.PageResult;
import com.learnlab.note.NoteRepository;
import com.learnlab.task.StudyTaskRepository;
import com.learnlab.user.AuthService;
import com.learnlab.user.UserAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CourseService {

  private final CourseRepository courses;
  private final NoteRepository notes;
  private final StudyTaskRepository tasks;
  private final AuthService authService;

  public CourseService(
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
  public PageResult<CourseView> list(int page, int size) {
    UserAccount user = authService.requireCurrent();
    Page<Course> result = courses.findByOwnerId(user.getId(), PageRequest.of(
        Math.max(page, 0),
        clampSize(size, 9),
        Sort.by(Sort.Direction.DESC, "id")));
    List<Long> ids = result.getContent().stream().map(Course::getId).toList();
    Map<Long, Long> noteCounts = ids.isEmpty() ? Map.of() : toCountMap(notes.countGroupedByCourseIds(ids));
    Map<Long, Long> taskCounts = ids.isEmpty() ? Map.of() : toCountMap(tasks.countGroupedByCourseIds(ids));
    List<CourseView> items = result.getContent().stream()
        .map(course -> CourseView.of(
            course,
            noteCounts.getOrDefault(course.getId(), 0L),
            taskCounts.getOrDefault(course.getId(), 0L)))
        .toList();
    return new PageResult<>(items, result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
  }

  @Transactional(readOnly = true)
  public CourseView get(Long id) {
    Course course = requireOwned(id);
    return CourseView.of(course, notes.countByCourseId(id), tasks.countByCourseId(id));
  }

  @Transactional
  public CourseView create(CourseRequest request) {
    Course course = new Course();
    course.setOwner(authService.requireCurrent());
    apply(course, request);
    courses.save(course);
    return CourseView.of(course, 0, 0);
  }

  @Transactional
  public CourseView update(Long id, CourseRequest request) {
    Course course = requireOwned(id);
    apply(course, request);
    return CourseView.of(course, notes.countByCourseId(id), tasks.countByCourseId(id));
  }

  @Transactional
  public void delete(Long id) {
    Course course = requireOwned(id);
    if (notes.countByCourseId(id) > 0 || tasks.countByCourseId(id) > 0) {
      throw new BusinessException(ErrorCode.CONFLICT, "课程下还有笔记或任务，先清空再删除");
    }
    courses.delete(course);
  }

  @Transactional(readOnly = true)
  public Course requireOwned(Long id) {
    Course course = courses.findById(id)
        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "课程不存在"));
    UserAccount user = authService.requireCurrent();
    if (!course.getOwner().getId().equals(user.getId())) {
      throw new BusinessException(ErrorCode.FORBIDDEN, "这不是你的课程");
    }
    return course;
  }

  private static void apply(Course course, CourseRequest request) {
    course.setTitle(request.title().trim());
    course.setSummary(request.summary() == null ? null : request.summary().trim());
  }

  private static int clampSize(int size, int fallback) {
    if (size < 1) {
      return fallback;
    }
    return Math.min(size, 50);
  }

  private static Map<Long, Long> toCountMap(List<Object[]> rows) {
    Map<Long, Long> map = new HashMap<>();
    for (Object[] row : rows) {
      map.put(((Number) row[0]).longValue(), ((Number) row[1]).longValue());
    }
    return map;
  }
}
