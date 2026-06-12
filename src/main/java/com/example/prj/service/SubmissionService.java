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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private static final long MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "zip", "rar", "7z", "pdf", "doc", "docx", "ppt", "pptx"
    );

    private final SubmissionRepository submissionRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    public Submission submitProject(SubmissionRequest request) {
        validateSubmission(request);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User student = userRepository.findByUsername(username).orElseThrow();

        Course course = courseRepository.findById(request.getCourseId()).orElseThrow();

        String reportUrl = null;
        if (request.getFile() != null && !request.getFile().isEmpty()) {
            try {
                reportUrl = cloudinaryService.uploadFile(request.getFile());
            } catch (IOException e) {
                throw new IllegalArgumentException("Upload file thất bại: " + e.getMessage());
            }
        }

        Submission submission = Submission.builder()
                .course(course)
                .student(student)
                .githubRepoUrl(request.getGithubRepoUrl())
                .reportUrl(reportUrl)
                .status(SubmissionStatus.SUBMITTED)
                .submittedAt(LocalDateTime.now())
                .build();

        return submissionRepository.save(submission);
    }

    private void validateSubmission(SubmissionRequest request) {
        boolean hasGithub = request.getGithubRepoUrl() != null && !request.getGithubRepoUrl().isBlank();
        boolean hasFile = request.getFile() != null && !request.getFile().isEmpty();

        if (!hasGithub && !hasFile) {
            throw new IllegalArgumentException("Phải cung cấp link GitHub hoặc file đồ án");
        }

        if (!hasFile) {
            return;
        }

        MultipartFile file = request.getFile();
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new IllegalArgumentException("File vượt quá dung lượng cho phép (tối đa 10MB)");
        }

        String extension = getFileExtension(file.getOriginalFilename());
        if (extension == null || !ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException(
                    "Định dạng file không được hỗ trợ. Cho phép: " + String.join(", ", ALLOWED_EXTENSIONS)
            );
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return null;
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
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
