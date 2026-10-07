package com.learnlab.comment;

import com.learnlab.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CommentController {

  private final CommentService commentService;

  public CommentController(CommentService commentService) {
    this.commentService = commentService;
  }

  @GetMapping("/api/notes/{noteId}/comments")
  public ApiResponse<List<CommentView>> list(@PathVariable Long noteId) {
    return ApiResponse.ok(commentService.list(noteId));
  }

  @PostMapping("/api/notes/{noteId}/comments")
  @ResponseStatus(HttpStatus.CREATED)
  public ApiResponse<CommentView> create(@PathVariable Long noteId, @Valid @RequestBody CommentRequest request) {
    return ApiResponse.ok(commentService.create(noteId, request));
  }

  @DeleteMapping("/api/comments/{id}")
  public ApiResponse<Void> delete(@PathVariable Long id) {
    commentService.delete(id);
    return ApiResponse.ok(null);
  }
}
