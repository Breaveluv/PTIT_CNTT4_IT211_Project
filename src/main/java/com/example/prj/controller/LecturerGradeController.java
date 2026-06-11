package com.example.prj.controller;

import com.example.prj.model.dto.request.GradeRequest;
import com.example.prj.model.entity.Submission;
import com.example.prj.model.dto.response.ApiResponse;
import com.example.prj.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/lecturer/grades")
@RequiredArgsConstructor
public class LecturerGradeController {

    private final SubmissionService submissionService;

    @PutMapping("/{submissionId}")
    public ResponseEntity<ApiResponse<Submission>> gradeSubmission(
            @PathVariable Long submissionId,
            @RequestBody GradeRequest request) {
        Submission submission = submissionService.gradeSubmission(submissionId, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Graded successfully", submission));
    }
}
