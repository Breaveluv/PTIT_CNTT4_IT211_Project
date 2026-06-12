package com.example.prj.controller;

import com.example.prj.model.dto.request.SubmissionRequest;
import com.example.prj.model.entity.Submission;
import com.example.prj.model.dto.response.ApiResponse;
import com.example.prj.service.SubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/student/submissions")
@RequiredArgsConstructor
public class StudentSubmissionController {

    private final SubmissionService submissionService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Submission>> submitProject(@Valid @ModelAttribute SubmissionRequest request) {
        Submission submission = submissionService.submitProject(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Submitted successfully", submission));
    }
}
