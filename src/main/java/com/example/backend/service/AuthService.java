package com.example.backend.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    public void verificarSesion(HttpSession session) {

        if (session == null ||
                session.getAttribute("usuario") == null) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Debe iniciar sesión."
            );
        }

    }

}
