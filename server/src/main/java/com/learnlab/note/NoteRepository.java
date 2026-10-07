package com.learnlab.note;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {

  @Query(
      value = """
          select distinct n from Note n
          left join n.tags t
          where (:courseId is null or n.course.id = :courseId)
            and (:authorId is null or n.author.id = :authorId)
            and (:tag is null or t.name = :tag)
            and (
              :keyword is null
              or lower(n.title) like lower(concat('%', :keyword, '%'))
              or lower(n.content) like lower(concat('%', :keyword, '%'))
            )
          order by n.pinned desc, n.updatedAt desc
          """,
      countQuery = """
          select count(distinct n.id) from Note n
          left join n.tags t
          where (:courseId is null or n.course.id = :courseId)
            and (:authorId is null or n.author.id = :authorId)
            and (:tag is null or t.name = :tag)
            and (
              :keyword is null
              or lower(n.title) like lower(concat('%', :keyword, '%'))
              or lower(n.content) like lower(concat('%', :keyword, '%'))
            )
          """)
  Page<Note> search(
      @Param("courseId") Long courseId,
      @Param("authorId") Long authorId,
      @Param("tag") String tag,
      @Param("keyword") String keyword,
      Pageable pageable);

  long countByAuthorId(Long authorId);

  long countByCourseId(Long courseId);

  @Query("select n.course.id, count(n) from Note n where n.course.id in :ids group by n.course.id")
  List<Object[]> countGroupedByCourseIds(@Param("ids") Collection<Long> ids);

  List<Note> findTop5ByAuthorIdOrderByUpdatedAtDesc(Long authorId);

  List<Note> findTop5ByOrderByUpdatedAtDesc();
}
