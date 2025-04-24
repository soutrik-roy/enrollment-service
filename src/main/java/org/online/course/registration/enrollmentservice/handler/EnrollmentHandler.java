package org.online.course.registration.enrollmentservice.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.online.course.registration.enrollmentservice.models.Enrollment;
import org.online.course.registration.enrollmentservice.service.EnrollmentService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.UUID;

@Component
@Log4j2
@RequiredArgsConstructor
public class EnrollmentHandler {


    private final EnrollmentService enrollmentService;



    public ServerResponse createEnrollment(ServerRequest serverRequest) {
        try {

            var requestBody = serverRequest.body(Map.class);
            String userId= requestBody.get("userid").toString();
            String courseId = (String) requestBody.get("courseid");
            if (userId == null || courseId == null) {
                return ServerResponse.badRequest().body("User ID and Course ID are required");
            }

            enrollmentService.enrollUser(UUID.fromString(userId), courseId);
            return ServerResponse.ok().body("User enrolled successfully");
        } catch (Exception e) {
            log.error("Error during enrollment: {}", e.getMessage());
            return ServerResponse.badRequest().body("Invalid request or enrollment failed");
        }
    }

    public ServerResponse getEnrollmentById(ServerRequest serverRequest) {
        try {

            // extract enrollmentId from the request
            String enrollmentId = serverRequest.pathVariable("id");

            Flux<Enrollment> enrollmentFlux = enrollmentService.findEnrollmentsByUserId(UUID.fromString(enrollmentId));

            enrollmentFlux.collectList().block();

            return ServerResponse.ok().body(enrollmentFlux);

        } catch (Exception e) {
            log.error("Error fetching enrollment by ID: {}", e.getMessage());
            return ServerResponse.badRequest().body("Invalid enrollment ID");
        }
    }

    public ServerResponse getEnrollmentByUserId(ServerRequest serverRequest) {
        try {
            // extract userId from the request
            String userId = serverRequest.pathVariable("userId");

            Flux<Enrollment> enrollmentFlux = enrollmentService.findEnrollmentsByUserId(UUID.fromString(userId));

            enrollmentFlux.collectList().block();

            return ServerResponse.ok().body(enrollmentFlux);

        } catch (Exception e) {
            log.error("Error fetching enrollment by User ID: {}", e.getMessage());
            return ServerResponse.badRequest().body("Invalid User ID");
        }
    }

    
}
