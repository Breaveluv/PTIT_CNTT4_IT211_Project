package com.example.prj.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequest {
    private String courseCode;
    private String courseName;
    private String description;
    private Integer credit;
    private Long lecturerId;
}
