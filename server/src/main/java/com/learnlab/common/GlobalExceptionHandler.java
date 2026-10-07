package com.learnlab.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.validation.FieldError;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException exception) {
    ErrorCode code = exception.getErrorCode();
    return ResponseEntity.status(code.status())
        .body(ApiResponse.fail(code.name(), exception.getMessage(), null));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Void>> handleInvalid(MethodArgumentNotValidException exception) {
    Map<String, String> fields = new LinkedHashMap<>();
    for (FieldError error : exception.getBindingResult().getFieldErrors()) {
      fields.putIfAbsent(error.getField(), error.getDefaultMessage());
    }
    return ResponseEntity.badRequest()
        .body(ApiResponse.fail("VALIDATION_ERROR", "参数校验失败", fields));
  }

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<ApiResponse<Void>> handleUploadSize(MaxUploadSizeExceededException exception) {
    return ResponseEntity.badRequest()
        .body(ApiResponse.fail(ErrorCode.BAD_REQUEST.name(), "文件不能超过 5MB", null));
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ApiResponse<Void>> handleConflict(DataIntegrityViolationException exception) {
    return ResponseEntity.status(ErrorCode.CONFLICT.status())
        .body(ApiResponse.fail(ErrorCode.CONFLICT.name(), "数据冲突，可能是名称重复或还被别的记录引用", null));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleUnknown(Exception exception) {
    log.error("未处理异常", exception);
    return ResponseEntity.internalServerError()
        .body(ApiResponse.fail("INTERNAL_ERROR", "服务器出了点问题", null));
  }
}
