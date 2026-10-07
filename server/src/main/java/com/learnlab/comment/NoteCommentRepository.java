package com.learnlab.comment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteCommentRepository extends JpaRepository<NoteComment, Long> {

  List<NoteComment> findByNoteIdOrderByCreatedAtAsc(Long noteId);
}
