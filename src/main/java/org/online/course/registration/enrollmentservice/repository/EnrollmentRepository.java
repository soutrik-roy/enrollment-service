package org.online.course.registration.enrollmentservice.repository;


import org.online.course.registration.enrollmentservice.models.Enrollment;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
@Repository
public interface EnrollmentRepository extends R2dbcRepository<Enrollment, Long> {
    Flux<Enrollment> findByUserId(UUID userId);

    Flux<Enrollment> findByCourseId(String courseId);

    Mono<Enrollment> findByUserIdAndCourseId(UUID userId, String courseId);
}