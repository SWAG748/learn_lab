package com.learnlab.user;

import com.learnlab.common.BusinessException;
import com.learnlab.common.CurrentUser;
import com.learnlab.common.ErrorCode;
import com.learnlab.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

  private final UserAccountRepository users;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public AuthService(UserAccountRepository users, PasswordEncoder passwordEncoder, JwtService jwtService) {
    this.users = users;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
  }

  @Transactional
  public AuthResponse register(RegisterRequest request) {
    String username = request.username().trim();
    String email = request.email().trim().toLowerCase();
    if (users.existsByUsername(username)) {
      throw new BusinessException(ErrorCode.CONFLICT, "用户名已存在");
    }
    if (users.existsByEmail(email)) {
      throw new BusinessException(ErrorCode.CONFLICT, "邮箱已注册");
    }
    UserAccount user = new UserAccount();
    user.setUsername(username);
    user.setEmail(email);
    user.setPasswordHash(passwordEncoder.encode(request.password()));
    user.setRole(Role.USER);
    user.setEnabled(true);
    users.save(user);
    return new AuthResponse(jwtService.issue(user), UserView.from(user));
  }

  @Transactional(readOnly = true)
  public AuthResponse login(LoginRequest request) {
    UserAccount user = users.findByUsername(request.username().trim())
        .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED, "用户名或密码错误"));
    if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
      throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户名或密码错误");
    }
    if (!user.isEnabled()) {
      throw new BusinessException(ErrorCode.FORBIDDEN, "账号已停用");
    }
    return new AuthResponse(jwtService.issue(user), UserView.from(user));
  }

  @Transactional(readOnly = true)
  public UserView me() {
    return UserView.from(requireCurrent());
  }

  @Transactional
  public void changePassword(PasswordChangeRequest request) {
    UserAccount user = requireCurrent();
    if (!passwordEncoder.matches(request.oldPassword(), user.getPasswordHash())) {
      throw new BusinessException(ErrorCode.BAD_REQUEST, "原密码不正确");
    }
    user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
  }

  @Transactional(readOnly = true)
  public UserAccount requireCurrent() {
    return users.findByUsername(CurrentUser.username())
        .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED, "请先登录"));
  }
}
