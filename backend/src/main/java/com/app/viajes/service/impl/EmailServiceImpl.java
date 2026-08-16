package com.app.viajes.service.impl;

import com.app.viajes.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Implementacion del servicio de correo con Spring Mail ({@link JavaMailSender}).
 *
 * <p>El envio se degrada con un log de error si el SMTP no esta accesible
 * (configuracion dummy de desarrollo), de modo que el fallo de conexion nunca
 * aborta la transaccion de registro en entornos locales.</p>
 */
@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    /**
     * URL del frontend que procesa el token de verificacion de la cuenta.
     */
    private static final String URL_FRONTEND_VERIFICACION = "http://localhost:4200/auth/verificar?token=";

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void enviarCorreoConfirmacion(String email, String token) {
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setTo(email);
            mensaje.setSubject("Confirma tu cuenta en Viajes");
            mensaje.setText("""
                    Bienvenido a Viajes.

                    Para activar tu cuenta pulsa en el siguiente enlace:
                    %s%s

                    El enlace es valido durante 24 horas.
                    Si no has solicitado este registro, ignora este correo.
                    """.formatted(URL_FRONTEND_VERIFICACION, token));

            mailSender.send(mensaje);
            log.info("Correo de confirmacion enviado a {}", email);
        } catch (Exception ex) {
            // En desarrollo el SMTP puede no estar disponible: se registra el error
            // pero no se aborta la transaccion de registro.
            log.error("No se pudo enviar el correo de confirmacion a {}: {}", email, ex.getMessage(), ex);
        }
    }
}