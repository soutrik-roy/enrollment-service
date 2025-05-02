package org.online.course.registration.enrollmentservice.repository;


import org.online.course.registration.enrollmentservice.models.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface UserRepository  extends R2dbcRepository<User, Long> {



    @Query("SELECT * FROM userS WHERE email = :email")
    Mono<User> findByEmail(String email);
}
