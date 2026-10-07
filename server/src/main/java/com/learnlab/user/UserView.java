package com.learnlab.user;

public record UserView(Long id, String username, String email, String role, boolean enabled) {

  public static UserView from(UserAccount user) {
    return new UserView(user.getId(), user.getUsername(), user.getEmail(), user.getRole().name(), user.isEnabled());
  }
}
