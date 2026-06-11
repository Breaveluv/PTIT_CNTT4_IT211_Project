package com.example.prj.controller;

import com.example.prj.model.dto.response.ApiResponse;
import com.example.prj.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/lecturer/materials")
@RequiredArgsConstructor
public class LecturerMaterialController {

    private final CloudinaryService cloudinaryService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<String>> uploadMaterial(@RequestParam("file") MultipartFile file) {
        try {
            String url = cloudinaryService.uploadFile(file);
            return ResponseEntity.ok(new ApiResponse<>(true, "File uploaded successfully to Cloudinary", url));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(new ApiResponse<>(false, "Upload failed: " + e.getMessage(), null));
        }
    }
}
