package com.app.viajes.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpaErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String uri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

        // Si es un error 404 y la petición NO iba dirigida a nuestra API backend...
        if (status != null && Integer.valueOf(status.toString()) == HttpStatus.NOT_FOUND.value()) {
            if (uri != null && !uri.startsWith("/api/")) {
                // ...reenviamos internamente a Angular
                return "forward:/index.html";
            }
        }
        
        // Para cualquier otro error (o errores de la API), dejamos que Spring actúe normal
        return "error"; 
    }
}