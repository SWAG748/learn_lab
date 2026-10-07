package com.learnlab.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentRequest(
    @NotBlank(message = "评论不能为空")
    @Size(max = 1000, message = "评论最多 1000 字")
    String content
) {
}
