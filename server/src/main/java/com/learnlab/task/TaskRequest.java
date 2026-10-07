package com.learnlab.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TaskRequest(
    @NotBlank(message = "任务标题不能为空")
    @Size(max = 160, message = "标题最多 160 字")
    String title,
    @Size(max = 1000, message = "说明最多 1000 字")
    String detail,
    @NotNull(message = "请选择状态") TaskStatus status,
    @NotNull(message = "请选择优先级") Priority priority,
    LocalDate dueDate,
    Long courseId
) {
}
