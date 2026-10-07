package com.learnlab.common;

/** 业务上能预期的失败，比如「笔记不存在」。不要用它包装真正的程序错误。 */
public class BusinessException extends RuntimeException {

  private final ErrorCode errorCode;

  public BusinessException(ErrorCode errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }
}
