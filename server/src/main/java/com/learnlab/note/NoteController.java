package com.learnlab.note;

import com.learnlab.common.ApiResponse;
import com.learnlab.common.PageResult;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

  private final NoteService noteService;

  public NoteController(NoteService noteService) {
    this.noteService = noteService;
  }

  @GetMapping
  public ApiResponse<PageResult<NoteSummary>> search(
      @RequestParam(required = false) Long courseId,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) String tag,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ApiResponse.ok(noteService.search(courseId, keyword, tag, page, size));
  }

  @GetMapping("/{id}")
  public ApiResponse<NoteDetail> get(@PathVariable Long id) {
    return ApiResponse.ok(noteService.get(id));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ApiResponse<NoteDetail> create(@Valid @RequestBody NoteRequest request) {
    return ApiResponse.ok(noteService.create(request));
  }

  @PutMapping("/{id}")
  public ApiResponse<NoteDetail> update(@PathVariable Long id, @Valid @RequestBody NoteRequest request) {
    return ApiResponse.ok(noteService.update(id, request));
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Void> delete(@PathVariable Long id) {
    noteService.delete(id);
    return ApiResponse.ok(null);
  }
}
