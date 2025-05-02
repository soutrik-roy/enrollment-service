package org.online.course.registration.enrollmentservice.router;

import org.online.course.registration.enrollmentservice.handler.EnrollmentHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class EnrollmentRouter {
    public static final String ENROLLMENT_PATH = "/enrollment";
    public static final String ENROLLMENT_COURSE_PATH = "/enrollment/course/{courseId}";
    public static final String ENROLLMENT_USER_PATH = "/enrollments/{email}";

    @Bean
    RouterFunction<ServerResponse> routes(EnrollmentHandler enrollmentHandler) {
        return org.springframework.web.servlet.function.RouterFunctions
                .route()
                .POST(ENROLLMENT_PATH, enrollmentHandler::createEnrollment)
                .GET(ENROLLMENT_USER_PATH, enrollmentHandler::getEnrollmentByEmail)
                .GET(ENROLLMENT_COURSE_PATH, enrollmentHandler::getEnrollmentByCourseId)
                .build();
    }
}
