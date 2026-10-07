package com.learnlab.user;

import jakarta.validation.constraints.NotNull;

public record EnabledRequest(@NotNull(message = "请指定是否启用") Boolean enabled) {
}
