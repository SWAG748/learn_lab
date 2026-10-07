package com.learnlab.user;

import com.learnlab.common.ApiResponse;
import com.learnlab.common.PageResult;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

  private final UserAdminService userAdminService;

  public AdminUserController(UserAdminService userAdminService) {
    this.userAdminService = userAdminService;
  }

  @GetMapping
  public ApiResponse<PageResult<UserView>> list(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    return ApiResponse.ok(userAdminService.list(page, size));
  }

  @PatchMapping("/{id}/enabled")
  public ApiResponse<UserView> setEnabled(@PathVariable Long id, @Valid @RequestBody EnabledRequest request) {
    return ApiResponse.ok(userAdminService.setEnabled(id, request.enabled()));
  }
}
