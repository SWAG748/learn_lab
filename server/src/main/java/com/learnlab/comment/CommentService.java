package com.learnlab.comment;

import com.learnlab.common.BusinessException;
import com.learnlab.common.ErrorCode;
import com.learnlab.note.Note;
import com.learnlab.note.NoteService;
import com.learnlab.user.AuthService;
import com.learnlab.user.UserAccount;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {

  private final NoteCommentRepository comments;
  private final NoteService noteService;
  private final AuthService authService;

  public CommentService(NoteCommentRepository comments, NoteService noteService, AuthService authService) {
    this.comments = comments;
    this.noteService = noteService;
    this.authService = authService;
  }

  @Transactional(readOnly = true)
  public List<CommentView> list(Long noteId) {
    noteService.requireOwned(noteId);
    return comments.findByNoteIdOrderByCreatedAtAsc(noteId).stream().map(CommentView::from).toList();
  }

  @Transactional
  public CommentView create(Long noteId, CommentRequest request) {
    Note note = noteService.requireOwned(noteId);
    NoteComment comment = new NoteComment();
    comment.setNote(note);
    comment.setAuthor(authService.requireCurrent());
    comment.setContent(request.content().trim());
    comments.save(comment);
    return CommentView.from(comment);
  }

  @Transactional
  public void delete(Long id) {
    NoteComment comment = comments.findById(id)
        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "评论不存在"));
    UserAccount user = authService.requireCurrent();
    if (!comment.getAuthor().getId().equals(user.getId())) {
      throw new BusinessException(ErrorCode.FORBIDDEN, "只能删除自己的评论");
    }
    noteService.requireOwned(comment.getNote().getId());
    comments.delete(comment);
  }
}
