package org.online.course.registration.enrollmentservice.repository;


import org.online.course.registration.enrollmentservice.models.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserRepository  extends R2dbcRepository<User, Long> {


    @Query("SELECT * FROM user_details WHERE user_id = :userId")
    Mono<User> findByUserId(UUID userId);
}
