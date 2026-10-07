package com.learnlab.tag;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TagService {

  private final TagRepository tags;

  public TagService(TagRepository tags) {
    this.tags = tags;
  }

  @Cacheable("tags")
  @Transactional(readOnly = true)
  public List<String> listNames() {
    return tags.findAllByOrderByNameAsc().stream().map(Tag::getName).toList();
  }

  @CacheEvict(value = "tags", allEntries = true)
  @Transactional
  public Tag findOrCreate(String rawName) {
    String name = rawName.trim();
    return tags.findByNameIgnoreCase(name).orElseGet(() -> {
      Tag tag = new Tag();
      tag.setName(name);
      return tags.save(tag);
    });
  }
}
