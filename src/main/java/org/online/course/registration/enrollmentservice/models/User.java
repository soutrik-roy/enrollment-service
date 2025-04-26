package org.online.course.registration.enrollmentservice.models;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
@Data
@Builder
@ToString
@Table(name = "user_details")
public class User {
    @Id
    private Long id;
    @Column(name = "user_id")
    private UUID userId;
    @Column(name = "name")
    private String name;
    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String password;
    @Column(name = "role")
    private String role;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
