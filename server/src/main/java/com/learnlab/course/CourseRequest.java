package com.learnlab.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CourseRequest(
    @NotBlank(message = "课程标题不能为空")
    @Size(max = 120, message = "标题最多 120 字")
    String title,
    @Size(max = 500, message = "简介最多 500 字")
    String summary
) {
}
