package org.online.course.registration.enrollmentservice.models;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
@Data
@Builder
@ToString
@Table(name = "enrollment")
@AllArgsConstructor
@NoArgsConstructor
public class Enrollment {
    @Id
    private Long id;
    @Column(name = "user_id")
    private UUID userId;
    @Column(name = "course_id")
    private String courseId;
    @Column(name = "enrolled_at")
    private LocalDateTime enrolledAt;
    @Column(name = "progress")
    private int progress;


}
