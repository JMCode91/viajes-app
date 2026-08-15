package com.app.viajes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicacion backend de gestion integral de viajes.
 * Punto de entrada del proyecto Spring Boot.
 */
@SpringBootApplication
public class ViajesApplication {

    public static void main(String[] args) {
        System.setProperty("java.net.preferIPv4Stack", "true");
        SpringApplication.run(ViajesApplication.class, args);
    }

}
