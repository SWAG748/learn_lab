package com.learnlab.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface StudyTaskRepository extends JpaRepository<StudyTask, Long> {

  @Query("""
      select t from StudyTask t
      where (:assigneeId is null or t.assignee.id = :assigneeId)
        and (:courseId is null or t.course.id = :courseId)
        and (:status is null or t.status = :status)
      order by t.dueDate asc, t.id desc
      """)
  List<StudyTask> search(
      @Param("assigneeId") Long assigneeId,
      @Param("courseId") Long courseId,
      @Param("status") TaskStatus status);

  long countByAssigneeId(Long assigneeId);

  long countByAssigneeIdAndStatus(Long assigneeId, TaskStatus status);

  long countByStatus(TaskStatus status);

  long countByCourseId(Long courseId);

  @Query("select t.course.id, count(t) from StudyTask t where t.course.id in :ids group by t.course.id")
  List<Object[]> countGroupedByCourseIds(@Param("ids") Collection<Long> ids);

  @Query("""
      select t from StudyTask t
      where (:assigneeId is null or t.assignee.id = :assigneeId)
        and t.status <> :done
        and t.dueDate is not null
        and t.dueDate < :today
      order by t.dueDate asc
      """)
  List<StudyTask> findOverdue(
      @Param("assigneeId") Long assigneeId,
      @Param("done") TaskStatus done,
      @Param("today") LocalDate today);

  @Query("""
      select count(t) from StudyTask t
      where (:assigneeId is null or t.assignee.id = :assigneeId)
        and t.status <> :done
        and t.dueDate is not null
        and t.dueDate < :today
      """)
  long countOverdue(
      @Param("assigneeId") Long assigneeId,
      @Param("done") TaskStatus done,
      @Param("today") LocalDate today);
}
