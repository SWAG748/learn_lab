package com.learnlab.course;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {

  Page<Course> findByOwnerId(Long ownerId, Pageable pageable);

  long countByOwnerId(Long ownerId);
}
