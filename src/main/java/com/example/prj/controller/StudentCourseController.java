package com.example.prj.controller;

import com.example.prj.model.dto.response.CourseResponse;
import com.example.prj.service.CourseService;
import com.example.prj.model.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/student/courses")
@RequiredArgsConstructor
public class StudentCourseController {

    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getAvailableCourses() {
        List<CourseResponse> list = courseService.getAllAvailableCourses();
        return ResponseEntity.ok(new ApiResponse<>(true, "Fetched available courses", list));
    }

    @PostMapping("/{courseId}/register")
    public ResponseEntity<ApiResponse<Void>> registerToCourse(@PathVariable Long courseId) {
        courseService.registerToCourse(courseId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Registered to course successfully", null));
    }
}
