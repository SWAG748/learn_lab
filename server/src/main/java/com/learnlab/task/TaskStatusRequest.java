package com.learnlab.task;

import jakarta.validation.constraints.NotNull;

public record TaskStatusRequest(@NotNull(message = "请选择状态") TaskStatus status) {
}
