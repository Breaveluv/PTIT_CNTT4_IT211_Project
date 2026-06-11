package com.example.prj.repository;

import com.example.prj.model.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Page<Course> findByCourseNameContainingIgnoreCaseOrCourseCodeContainingIgnoreCase(String name, String code, Pageable pageable);
}
