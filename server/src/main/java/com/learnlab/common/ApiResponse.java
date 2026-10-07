package com.learnlab.common;

import java.util.Map;

/**
 * 接口统一外壳。成功和失败都是这个形状，前端只需要认这一处。
 * fields 只在参数校验失败时有值，key 是字段名。
 */
public record ApiResponse<T>(
    boolean success,
    String code,
    String message,
    T data,
    Map<String, String> fields
) {

  public static <T> ApiResponse<T> ok(T data) {
    return new ApiResponse<>(true, "OK", null, data, null);
  }

  public static ApiResponse<Void> fail(String code, String message, Map<String, String> fields) {
    return new ApiResponse<>(false, code, message, null, fields);
  }
}
