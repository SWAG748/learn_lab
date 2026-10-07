package com.learnlab.tag;

import com.learnlab.common.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Locale;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tag")
public class Tag extends AuditableEntity {

  @Column(nullable = false, unique = true, length = 40)
  private String name;

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof Tag tag)) {
      return false;
    }
    return name != null && name.equalsIgnoreCase(tag.name);
  }

  @Override
  public int hashCode() {
    return name == null ? 0 : name.toLowerCase(Locale.ROOT).hashCode();
  }
}
