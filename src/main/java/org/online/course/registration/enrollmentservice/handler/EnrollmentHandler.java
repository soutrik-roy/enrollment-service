package org.online.course.registration.enrollmentservice.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.online.course.registration.enrollmentservice.config.JwtUtils;
import org.online.course.registration.enrollmentservice.models.dto.EnrollmentOutput;
import org.online.course.registration.enrollmentservice.service.EnrollmentService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.Map;

@Component
@Log4j2
@RequiredArgsConstructor
public class EnrollmentHandler {

    private final EnrollmentService enrollmentService;
    private final JwtUtils jwtUtils;

    public ServerResponse createEnrollment(ServerRequest serverRequest) {
        try {
            String authHeader = serverRequest.headers().firstHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ServerResponse.status(401).body("Missing or invalid Authorization header");
            }
            String token = authHeader.substring(7);
            if (jwtUtils.validateToken(token)) {
                return ServerResponse.status(401).body("Invalid or expired token");
            }

            var requestBody = serverRequest.body(Map.class);
            String email = requestBody.get("email").toString();
            String courseId = (String) requestBody.get("courseId");
            if (email == null || courseId == null) {
                return ServerResponse.badRequest().body("Email and Course ID are required");
            }

            return enrollmentService.enrollUser(email, courseId)
                    .map(enrollment -> ServerResponse.ok().body("User with emailId " + email + " got enrolled successfully in: " + courseId))
                    .block();
        } catch (Exception e) {
            log.error("Error during enrollment: {}", e.getMessage());
            return ServerResponse.badRequest().body(e.getMessage());
        }
    }

    public ServerResponse getEnrollmentByEmail(ServerRequest serverRequest) {
        try {
            String authHeader = serverRequest.headers().firstHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ServerResponse.status(401).body("Missing or invalid Authorization header");
            }
            String token = authHeader.substring(7);
            if (jwtUtils.validateToken(token)) {
                return ServerResponse.status(401).body("Invalid or expired token");
            }

            String email = serverRequest.pathVariable("email");
            var enrollments = enrollmentService.findEnrollmentsByEmail(email)
                    .map(enrollment -> EnrollmentOutput.builder()
                            .courseId(enrollment.getCourseId())
                            .courseName(enrollment.getCourseName())
                            .progress(enrollment.getProgress())
                            .build())
                    .collectList()
                    .block();

            if (enrollments == null || enrollments.isEmpty()) {
                return ServerResponse.status(404).body("No enrollments found for email: " + email);
            }

            return ServerResponse.ok().body(enrollments);

        } catch (Exception e) {
            log.error("Error fetching enrollment by email: {}", e.getMessage());
            return ServerResponse.badRequest().body("User with email ID not found");
        }
    }
    public ServerResponse getEnrollmentByCourseId(ServerRequest serverRequest) {
        try {
            String authHeader = serverRequest.headers().firstHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ServerResponse.status(401).body("Missing or invalid Authorization header");
            }
            String token = authHeader.substring(7);
            if (jwtUtils.validateToken(token)) {
                return ServerResponse.status(401).body("Invalid or expired token");
            }

            String courseId = serverRequest.pathVariable("courseId");

            var enrollments = enrollmentService.findEnrollmentByCourseId(courseId)
                    .collectList()
                    .block();

            if (enrollments == null || enrollments.isEmpty()) {
                return ServerResponse.status(404).body("No enrollments found for course ID: " + courseId);
            }

            return ServerResponse.ok().body(enrollments);

        } catch (Exception e) {
            log.error("Error fetching enrollment by Course ID: {}", e.getMessage());
            return ServerResponse.badRequest().body("Invalid Course ID");
        }
    }
}