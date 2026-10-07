package com.learnlab.security;

import com.learnlab.user.UserAccount;
import com.learnlab.user.UserAccountRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 每次请求都按 token 里的用户名回库看一眼账号还在不在、有没有被停用。
 * JWT 本身只是一张签名过的票据，停用账号必须靠这一步才能立刻生效。
 */
@Component
public class DbJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

  private final UserAccountRepository users;

  public DbJwtAuthenticationConverter(UserAccountRepository users) {
    this.users = users;
  }

  @Override
  public AbstractAuthenticationToken convert(Jwt jwt) {
    UserAccount user = users.findByUsername(jwt.getSubject())
        .orElseThrow(() -> new DisabledException("账号不存在或已失效"));
    if (!user.isEnabled()) {
      throw new DisabledException("账号已停用");
    }
    List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    return new JwtAuthenticationToken(jwt, authorities, user.getUsername());
  }
}
