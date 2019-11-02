package com.nodediagnostic.webserver;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.springframework.boot.WebApplicationType.SERVLET;

/**
 * Spring Boot application.
 */
@SpringBootApplication
public class Server {
    /**
     * Starts our Spring Boot application.
     */
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Server.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setWebApplicationType(SERVLET);
        app.run(args);
    }
}