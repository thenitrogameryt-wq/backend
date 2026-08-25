package com.example.backend.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import com.example.backend.entidades.Contacto;
import com.example.backend.repository.ContactoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ContactoService {

    @Autowired
    private ContactoRepository contactoRepository;

    @Autowired
    private JavaMailSender mailSender;

    public Contacto guardarContacto(Contacto contacto) {

        contacto.setFecha(LocalDateTime.now());
        contacto.setEstado("PENDIENTE");

        return contactoRepository.save(contacto);

    }

    public void enviarCorreo(Contacto contacto) {

        SimpleMailMessage correo = new SimpleMailMessage();

        correo.setTo("edrumayy23@gmail.com");

        correo.setSubject("Nueva consulta desde el Portal Web");

        correo.setText(
                "Nombre: " + contacto.getNombre() +
                        "\nCorreo: " + contacto.getCorreo() +
                        "\nAsunto: " + contacto.getAsunto() +
                        "\n\nMensaje:\n" + contacto.getMensaje()
        );

        mailSender.send(correo);
    }

}