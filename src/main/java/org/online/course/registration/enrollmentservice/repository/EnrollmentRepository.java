package org.online.course.registration.enrollmentservice.repository;


import org.online.course.registration.enrollmentservice.models.Enrollment;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@Repository
public interface EnrollmentRepository extends R2dbcRepository<Enrollment, Long> {

    Flux<Enrollment> findByCourseId(String courseId);

    Flux<Enrollment> findByEmail(String email);

    Mono<Enrollment> findByEmailAndCourseId(String email, String courseId);
}