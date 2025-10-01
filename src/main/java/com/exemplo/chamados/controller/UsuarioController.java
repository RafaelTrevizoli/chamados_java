package com.exemplo.chamados.controller;

import com.exemplo.chamados.model.Usuario;
import com.exemplo.chamados.repository.UsuarioRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // ✅ Cadastro
    @PostMapping("/register")
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario usuario) {
        try {
            // Verifica se o email já existe
            Optional<Usuario> existente = usuarioRepository.findByEmail(usuario.getEmail());
            if (existente.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("E-mail já cadastrado.");
            }

            Usuario novo = usuarioRepository.save(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(novo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao cadastrar: " + e.getMessage());
        }
    }

    // ✅ Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario usuario) {
        try {
            Optional<Usuario> existente = usuarioRepository.findByEmail(usuario.getEmail());
            if (existente.isPresent() && existente.get().getSenha().equals(usuario.getSenha())) {
                return ResponseEntity.ok(existente.get());
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("E-mail ou senha inválidos.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao logar: " + e.getMessage());
        }
    }
}
