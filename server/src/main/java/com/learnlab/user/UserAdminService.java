package com.learnlab.user;

import com.learnlab.common.BusinessException;
import com.learnlab.common.ErrorCode;
import com.learnlab.common.PageResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAdminService {

  private final UserAccountRepository users;
  private final AuthService authService;

  public UserAdminService(UserAccountRepository users, AuthService authService) {
    this.users = users;
    this.authService = authService;
  }

  @Transactional(readOnly = true)
  @PreAuthorize("hasRole('ADMIN')")
  public PageResult<UserView> list(int page, int size) {
    Page<UserAccount> result = users.findAll(PageRequest.of(
        Math.max(page, 0),
        Math.min(Math.max(size, 1), 50),
        Sort.by(Sort.Direction.ASC, "id")));
    return new PageResult<>(
        result.getContent().stream().map(UserView::from).toList(),
        result.getNumber(),
        result.getSize(),
        result.getTotalElements(),
        result.getTotalPages());
  }

  @Transactional
  @PreAuthorize("hasRole('ADMIN')")
  public UserView setEnabled(Long id, boolean enabled) {
    UserAccount current = authService.requireCurrent();
    if (current.getId().equals(id) && !enabled) {
      throw new BusinessException(ErrorCode.BAD_REQUEST, "不能停用自己");
    }
    UserAccount user = users.findById(id)
        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "用户不存在"));
    user.setEnabled(enabled);
    return UserView.from(user);
  }
}
