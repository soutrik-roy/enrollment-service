package org.online.course.registration.enrollmentservice.models;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Builder
@ToString
@Table(name = "enrollment")
@AllArgsConstructor
@NoArgsConstructor
public class Enrollment {
    @Id
    private Long id;
    @Column(name = "email")
    private String email;
    @Column(name = "course_id")
    private String courseId;
    @Column(name = "enrolled_at")
    private LocalDateTime enrolledAt;
    @Column(name = "course_name")
    private String courseName;
    @Column(name = "progress")
    private int progress;


}
