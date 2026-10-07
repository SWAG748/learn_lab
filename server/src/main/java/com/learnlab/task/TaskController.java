package com.learnlab.task;

import com.learnlab.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

  private final TaskService taskService;

  public TaskController(TaskService taskService) {
    this.taskService = taskService;
  }

  @GetMapping
  public ApiResponse<List<TaskView>> list(
      @RequestParam(required = false) Long courseId,
      @RequestParam(required = false) TaskStatus status) {
    return ApiResponse.ok(taskService.list(courseId, status));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ApiResponse<TaskView> create(@Valid @RequestBody TaskRequest request) {
    return ApiResponse.ok(taskService.create(request));
  }

  @PutMapping("/{id}")
  public ApiResponse<TaskView> update(@PathVariable Long id, @Valid @RequestBody TaskRequest request) {
    return ApiResponse.ok(taskService.update(id, request));
  }

  @PatchMapping("/{id}/status")
  public ApiResponse<TaskView> changeStatus(@PathVariable Long id, @Valid @RequestBody TaskStatusRequest request) {
    return ApiResponse.ok(taskService.changeStatus(id, request.status()));
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Void> delete(@PathVariable Long id) {
    taskService.delete(id);
    return ApiResponse.ok(null);
  }
}
