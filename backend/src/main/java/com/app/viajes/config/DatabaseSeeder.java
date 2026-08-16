package com.app.viajes.config;

import com.app.viajes.model.Usuario;
import com.app.viajes.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seeder de desarrollo: inicializa automaticamente una serie de usuarios
 * de prueba cuando la base de datos esta vacia (o carece de esos correos),
 * para agilizar las pruebas locales sin registros manuales.
 */
@Component
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSeeder.class);

    private static final String PASSWORD_DEV = "password";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        crearUsuarioSiNoExiste("Javi", "javi@viajes.com");
        crearUsuarioSiNoExiste("Cynthia", "cynthia@viajes.com");
        log.info("Usuarios de desarrollo inicializados correctamente");
    }

    private void crearUsuarioSiNoExiste(String nombre, String email) {
        if (usuarioRepository.existsByEmail(email)) {
            return;
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(PASSWORD_DEV));
        // Los usuarios de desarrollo nacen activados: el flujo real de registro
        // exige confirmar la cuenta por email, pero estos se crean listos para login.
        usuario.setActivo(true);

        usuarioRepository.save(usuario);
        log.info("Creado usuario de desarrollo: {} <{}>", nombre, email);
    }
}