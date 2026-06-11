package com.example.prj.service;

import com.example.prj.model.dto.request.GradeRequest;
import com.example.prj.model.dto.request.SubmissionRequest;
import com.example.prj.model.entity.Course;
import com.example.prj.model.entity.Submission;
import com.example.prj.model.entity.SubmissionStatus;
import com.example.prj.model.entity.User;
import com.example.prj.repository.CourseRepository;
import com.example.prj.repository.SubmissionRepository;
import com.example.prj.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public Submission submitProject(SubmissionRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User student = userRepository.findByUsername(username).orElseThrow();
        
        Course course = courseRepository.findById(request.getCourseId()).orElseThrow();
        
        Submission submission = Submission.builder()
                .course(course)
                .student(student)
                .reportUrl(request.getReportUrl())
                .status(SubmissionStatus.SUBMITTED)
                .submittedAt(LocalDateTime.now())
                .build();
        
        return submissionRepository.save(submission);
    }

    public Submission gradeSubmission(Long submissionId, GradeRequest request) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));
        
        String lecturerUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User lecturer = userRepository.findByUsername(lecturerUsername).orElseThrow();

        submission.setLecturer(lecturer);
        submission.setScore(request.getScore());
        submission.setFeedback(request.getFeedback());
        submission.setStatus(SubmissionStatus.GRADED);
        
        return submissionRepository.save(submission);
    }
}
