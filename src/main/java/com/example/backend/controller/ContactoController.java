package com.example.backend.controller;

import com.example.backend.entidades.Contacto;
import com.example.backend.repository.ContactoRepository;
import com.example.backend.service.ContactoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/contacto")
@CrossOrigin(
        origins = "http://localhost:5173",
        allowCredentials = "true"
)
public class ContactoController {

    @Autowired
    private ContactoService service;

    private final ContactoRepository repository;

    public ContactoController(
            ContactoRepository repository) {

        this.repository = repository;
    }

    @GetMapping
    public List<Contacto> listarContactos() {

        return repository.findAll();
    }

    @PostMapping
    public ResponseEntity<Contacto> guardar(
            @RequestBody Contacto contacto){

        contacto.setFecha(LocalDateTime.now());

        contacto.setEstado("PENDIENTE");

        Contacto nuevo =
                service.guardarContacto(contacto);

        service.enviarCorreo(nuevo);

        return ResponseEntity.ok(nuevo);

    }

}
