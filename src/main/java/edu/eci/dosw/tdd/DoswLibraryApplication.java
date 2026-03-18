package edu.eci.dosw.tdd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DoswLibraryApplication {

    public static void main(String[] args) {
        // Esta línea es la que arranca Spring Boot, inicializa Lombok, Swagger y tus Controladores
        SpringApplication.run(DoswLibraryApplication.class, args);
    }

}