package com.example.prj.controller;

import com.example.prj.model.dto.request.CourseRequest;
import com.example.prj.model.dto.response.CourseResponse;
import com.example.prj.model.entity.Course;
import com.example.prj.model.entity.User;
import com.example.prj.model.dto.response.ApiResponse;
import com.example.prj.repository.CourseRepository;
import com.example.prj.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/courses")
@RequiredArgsConstructor
public class AdminCourseController {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CourseResponse>>> getAllCourses(
            @RequestParam(required = false) String query,
            Pageable pageable) {
        Page<Course> coursePage;
        if (query != null && !query.isEmpty()) {
            coursePage = courseRepository.findByCourseNameContainingIgnoreCaseOrCourseCodeContainingIgnoreCase(query, query, pageable);
        } else {
            coursePage = courseRepository.findAll(pageable);
        }
        Page<CourseResponse> page = coursePage.map(this::mapToCourseResponse);
        return ResponseEntity.ok(new ApiResponse<>(true, "Fetched courses", page));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(@RequestBody CourseRequest request) {
        User lecturer = null;
        if (request.getLecturerId() != null) {
            lecturer = userRepository.findById(request.getLecturerId()).orElse(null);
        }
        
        Course course = Course.builder()
                .courseCode(request.getCourseCode())
                .courseName(request.getCourseName())
                .description(request.getDescription())
                .credit(request.getCredit())
                .lecturer(lecturer)
                .build();
        
        CourseResponse resp = mapToCourseResponse(courseRepository.save(course));
        return new ResponseEntity<>(new ApiResponse<>(true, "Created course successfully", resp), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseResponse>> getCourseById(@PathVariable Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        return ResponseEntity.ok(new ApiResponse<>(true, "Fetched course", mapToCourseResponse(course)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseResponse>> updateCourse(@PathVariable Long id, @RequestBody CourseRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        
        course.setCourseCode(request.getCourseCode());
        course.setCourseName(request.getCourseName());
        course.setDescription(request.getDescription());
        course.setCredit(request.getCredit());
        
        if (request.getLecturerId() != null) {
            User lecturer = userRepository.findById(request.getLecturerId()).orElse(null);
            course.setLecturer(lecturer);
        }
        
        CourseResponse resp = mapToCourseResponse(courseRepository.save(course));
        return ResponseEntity.ok(new ApiResponse<>(true, "Updated course successfully", resp));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable Long id) {
        courseRepository.deleteById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Deleted course successfully", null));
    }

    private CourseResponse mapToCourseResponse(Course course) {
        return CourseResponse.builder()
                .id(course.getId())
                .courseCode(course.getCourseCode())
                .courseName(course.getCourseName())
                .description(course.getDescription())
                .credit(course.getCredit())
                .lecturerName(course.getLecturer() != null ? course.getLecturer().getFullName() : "N/A")
                .build();
    }
}
