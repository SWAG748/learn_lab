package com.learnlab.common;

public enum ErrorCode {
  BAD_REQUEST(400),
  UNAUTHORIZED(401),
  FORBIDDEN(403),
  NOT_FOUND(404),
  CONFLICT(409);

  private final int status;

  ErrorCode(int status) {
    this.status = status;
  }

  public int status() {
    return status;
  }
}
