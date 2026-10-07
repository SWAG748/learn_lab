package com.learnlab.security;

import com.learnlab.config.AppProperties;
import com.learnlab.user.UserAccount;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class JwtService {

  private final JwtEncoder jwtEncoder;
  private final long expirationMinutes;

  public JwtService(JwtEncoder jwtEncoder, AppProperties properties) {
    this.jwtEncoder = jwtEncoder;
    this.expirationMinutes = properties.jwt().expirationMinutes();
  }

  public String issue(UserAccount user) {
    Instant now = Instant.now();
    JwtClaimsSet claims = JwtClaimsSet.builder()
        .subject(user.getUsername())
        .issuedAt(now)
        .expiresAt(now.plus(expirationMinutes, ChronoUnit.MINUTES))
        .claim("uid", user.getId())
        .claim("roles", List.of("ROLE_" + user.getRole().name()))
        .build();
    JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
    return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
  }
}
