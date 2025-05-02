package org.online.course.registration.enrollmentservice.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.online.course.registration.enrollmentservice.models.dto.EnrollmentOutput;
import org.online.course.registration.enrollmentservice.service.EnrollmentService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;
import reactor.core.publisher.Flux;

import java.util.Map;

@Component
@Log4j2
@RequiredArgsConstructor
public class EnrollmentHandler {


    private final EnrollmentService enrollmentService;



    public ServerResponse createEnrollment(ServerRequest serverRequest) {
        try {

            var requestBody = serverRequest.body(Map.class);
            String email= requestBody.get("email").toString();
            String courseId = (String) requestBody.get("courseid");
            if (email == null || courseId == null) {
                return ServerResponse.badRequest().body("email and Course ID are required");
            }

            return enrollmentService.enrollUser(email, courseId)
                    .map(enrollment -> ServerResponse.ok().body("User  with emailId " +email+ " got enrolled successfully in: " + courseId))
                    .block();
        } catch (Exception e) {
            log.error("Error during enrollment: {}", e.getMessage());
            return ServerResponse.badRequest().body(e.getMessage());
        }
    }


    public ServerResponse getEnrollmentByEmail(ServerRequest serverRequest) {
        try {
            String email = serverRequest.pathVariable("email");
            Flux<EnrollmentOutput> enrollmentFlux = enrollmentService.findEnrollmentsByEmail(email)
                    .map(enrollment -> EnrollmentOutput.builder()
                            .userEmail(enrollment.getEmail())
                            .courseId(enrollment.getCourseId())
                            .courseName(enrollment.getCourseName())
                            .progress(enrollment.getProgress())
                            .build())
                    .switchIfEmpty(Flux.error(new IllegalArgumentException("No enrollments found for email: " + email)));
            enrollmentFlux.collectList().block();
            return ServerResponse.ok().body(enrollmentFlux);

        } catch (Exception e) {
            log.error("Error fetching enrollment by email: {}", e.getMessage());
            return ServerResponse.badRequest().body("User with email ID not found");
        }
    }


    public ServerResponse getEnrollmentByCourseId(ServerRequest serverRequest) {
        try {
            String courseId = serverRequest.pathVariable("courseId");

            Flux<EnrollmentOutput> enrollmentByCourseId = enrollmentService.findEnrollmentByCourseId(courseId);

            enrollmentByCourseId.collectList().block();

            return ServerResponse.ok().body(enrollmentByCourseId);

        } catch (Exception e) {
            log.error("Error fetching enrollment by Course ID: {}", e.getMessage());
            return ServerResponse.badRequest().body("Invalid Course ID");
        }
    }


}
