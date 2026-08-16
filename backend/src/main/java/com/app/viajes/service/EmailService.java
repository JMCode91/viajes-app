package com.app.viajes.service;

/**
 * Servicio de envio de correos electronicos de la aplicacion.
 */
public interface EmailService {

    /**
     * Envia el correo de confirmacion de registro con el enlace de activacion
     * de la cuenta para el usuario indicado.
     *
     * @param email direccion de correo del usuario recien registrado
     * @param token token de verificacion de un solo uso
     */
    void enviarCorreoConfirmacion(String email, String token);
}