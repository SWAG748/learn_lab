package com.learnlab.user;

public record AuthResponse(String token, UserView user) {
}
