package org.online.course.registration.enrollmentservice.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.online.course.registration.enrollmentservice.models.Enrollment;
import org.online.course.registration.enrollmentservice.models.EnrollmentOutput;
import org.online.course.registration.enrollmentservice.repository.EnrollmentRepository;
import org.online.course.registration.enrollmentservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
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


    public void enrollUser(UUID userId, String courseId) {
        log.info("Enrolling user with ID: {} in course: {}", userId, courseId);

        userRepository.findByUserId(userId)
                .switchIfEmpty(Mono.defer(() -> {
                    log.error("User with ID: {} not found", userId);
                    return Mono.empty();
                }))
                .flatMap(userDetails -> {
                    if (!"Student".equalsIgnoreCase(userDetails.getRole())) {

                        log.error("User with ID: {} does not have the required role to enroll in a course", userId);
                        return Mono.empty();
                    }
                    return enrollmentRepository.findByUserIdAndCourseId(userId, courseId)
                            .flatMap(existingEnrollment -> {
                                log.error("User with ID: {} is already enrolled in course: {}", userId, courseId);
                                return Mono.empty();
                            })
                            .switchIfEmpty(Mono.defer(() -> {
                                Enrollment enrollment = Enrollment.builder()
                                        .userId(userId)
                                        .courseId(courseId)
                                        .courseName(courseIdToNameMap.get(courseId))
                                        .enrolledAt(java.time.LocalDateTime.now())
                                        .progress(0)
                                        .build();
                                return enrollmentRepository.save(enrollment)
                                        .doOnSuccess(savedEnrollment -> log.info("User with ID: {} successfully enrolled in course: {}", userId, courseId));
                            }));
                })
                .subscribe();
    }


    public Flux<Enrollment> findEnrollmentsByUserId(UUID userId) {
        return enrollmentRepository.findByUserId(userId);
    }

    public Flux<EnrollmentOutput> findEnrollmentByCourseId(String courseId) {
        return enrollmentRepository.findByCourseId(courseId)
                .flatMap(enrollment -> userRepository.findByUserId(enrollment.getUserId())
                        .map(userDetails -> {
                            EnrollmentOutput output = new EnrollmentOutput();
                            output.setUserId(userDetails.getUserId());
                            output.setUserName(userDetails.getName());
                            output.setCourseId(enrollment.getCourseId());
                            output.setCourseName(enrollment.getCourseName());
                            output.setUserEmail(userDetails.getEmail());
                            output.setProgress(enrollment.getProgress());
                            return output;
                        }));
    }

}
