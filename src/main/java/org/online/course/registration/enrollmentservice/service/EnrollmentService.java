package org.online.course.registration.enrollmentservice.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.online.course.registration.enrollmentservice.models.Enrollment;
import org.online.course.registration.enrollmentservice.models.dto.EnrollmentOutput;
import org.online.course.registration.enrollmentservice.repository.EnrollmentRepository;
import org.online.course.registration.enrollmentservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Log4j2
@Service
public class EnrollmentService {
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;

    private final Map<String, String> courseIdToNameMap = new HashMap<>();

    {
        // Initialize the courseIdToNameMap with some sample data
        courseIdToNameMap.put("CS101", "DataStructure");
        courseIdToNameMap.put("CS102", "FullStack");
        courseIdToNameMap.put("CS103", "Scalable services");
    }


    public Mono<Object> enrollUser(String email, String courseId) {
        log.info("Enrolling user with emailID: {} in course: {}", email, courseId);

        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.defer(() -> {
                    log.error("User with emailID: {} not found", email);
                    return Mono.error(new IllegalArgumentException("User not found with emailID: " + email));
                }))
                .flatMap(userDetails -> {
                    if (!"Student".equalsIgnoreCase(userDetails.getRole())) {
                        log.error("User with emailID: {} does not have the required role to enroll in a course", email);
                        return Mono.error(new IllegalArgumentException("User does not have the required role to enroll in a course"));
                    }
                    return enrollmentRepository.findByEmailAndCourseId(email, courseId)
                            .flatMap(existingEnrollment -> {
                                log.error("User with ID: {} is already enrolled in course: {}", email, courseId);
                                return Mono.error(new IllegalStateException("User is already enrolled in the course"));
                            })
                            .switchIfEmpty(Mono.defer(() -> {
                                Enrollment enrollment = Enrollment.builder()
                                        .email(email)
                                        .courseId(courseId)
                                        .courseName(courseIdToNameMap.get(courseId))
                                        .enrolledAt(java.time.LocalDateTime.now())
                                        .progress(0)
                                        .build();
                                return enrollmentRepository.save(enrollment)
                                        .cast(Enrollment.class)
                                        .doOnSuccess(savedEnrollment -> log.info("User with emailID : {} successfully enrolled in course: {}", email, courseId));
                            }));
                });
    }


    public Flux<Enrollment> findEnrollmentsByEmail(String email) {
        return enrollmentRepository.findByEmail(email);
    }

    public Flux<EnrollmentOutput> findEnrollmentByCourseId(String courseId) {
        return enrollmentRepository.findByCourseId(courseId)
                .flatMap(enrollment -> userRepository.findByEmail(enrollment.getEmail())
                        .map(userDetails -> {
                            EnrollmentOutput output = new EnrollmentOutput();
                            output.setUserName(userDetails.getName());
                            output.setCourseId(enrollment.getCourseId());
                            output.setCourseName(enrollment.getCourseName());
                            output.setUserEmail(userDetails.getEmail());
                            output.setProgress(enrollment.getProgress());
                            return output;
                        }));
    }

}
