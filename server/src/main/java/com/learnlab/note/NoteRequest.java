package com.learnlab.note;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record NoteRequest(
    @NotBlank(message = "标题不能为空")
    @Size(max = 160, message = "标题最多 160 字")
    String title,
    @NotBlank(message = "正文不能为空")
    @Size(max = 20000, message = "正文最多 20000 字")
    String content,
    @NotNull(message = "请选择课程") Long courseId,
    boolean pinned,
    List<@NotBlank(message = "标签不能是空白") @Size(max = 40, message = "标签最多 40 字") String> tags
) {
}
