package com.learnlab.course;

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
@RequestMapping("/api/courses")
public class CourseController {

  private final CourseService courseService;

  public CourseController(CourseService courseService) {
    this.courseService = courseService;
  }

  @GetMapping
  public ApiResponse<PageResult<CourseView>> list(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "9") int size) {
    return ApiResponse.ok(courseService.list(page, size));
  }

  @GetMapping("/{id}")
  public ApiResponse<CourseView> get(@PathVariable Long id) {
    return ApiResponse.ok(courseService.get(id));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ApiResponse<CourseView> create(@Valid @RequestBody CourseRequest request) {
    return ApiResponse.ok(courseService.create(request));
  }

  @PutMapping("/{id}")
  public ApiResponse<CourseView> update(@PathVariable Long id, @Valid @RequestBody CourseRequest request) {
    return ApiResponse.ok(courseService.update(id, request));
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Void> delete(@PathVariable Long id) {
    courseService.delete(id);
    return ApiResponse.ok(null);
  }
}
