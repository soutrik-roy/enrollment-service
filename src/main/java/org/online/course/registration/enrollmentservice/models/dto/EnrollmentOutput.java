package org.online.course.registration.enrollmentservice.models.dto;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentOutput {
    private String courseId;
    private String courseName;
    private UUID userId;
    private String userName;
    private String userEmail;
    private int progress;
}
