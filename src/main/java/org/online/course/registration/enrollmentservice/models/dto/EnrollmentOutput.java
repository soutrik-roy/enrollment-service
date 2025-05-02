package org.online.course.registration.enrollmentservice.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnrollmentOutput {
    private String courseId;
    private String courseName;
    private String userName;
    private String userEmail;
    private int progress;
}
