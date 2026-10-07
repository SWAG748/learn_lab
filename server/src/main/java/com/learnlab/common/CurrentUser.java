package com.learnlab.common;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;

public final class CurrentUser {

  private CurrentUser() {
  }

  public static String username() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null
        || !authentication.isAuthenticated()
        || authentication instanceof AnonymousAuthenticationToken) {
      throw new BusinessException(ErrorCode.UNAUTHORIZED, "请先登录");
    }
    return authentication.getName();
  }
}
