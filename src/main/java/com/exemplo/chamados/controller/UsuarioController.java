package com.exemplo.chamados.controller;

import com.exemplo.chamados.model.Usuario;
import com.exemplo.chamados.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    @PostMapping("/register")
    public ResponseEntity<Usuario> registrar(@RequestBody Usuario usuario) {
        return ResponseEntity.ok(usuarioRepository.save(usuario));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario usuario) {

        Usuario u = usuarioRepository.findByEmail(usuario.getEmail())
                .orElse(null);

        if (u != null && u.getSenha().equals(usuario.getSenha()))
            return ResponseEntity.ok(u);

        return ResponseEntity.status(401).body("Credenciais inválidas");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscar(@PathVariable Long id) {
        return usuarioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Usuario dados) {

        Usuario u = usuarioRepository.findById(id).orElse(null);
        if (u == null) return ResponseEntity.notFound().build();

        u.setNome(dados.getNome());
        u.setEmail(dados.getEmail());
        u.setSenha(dados.getSenha());
        u.setNivel(dados.getNivel());

        return ResponseEntity.ok(usuarioRepository.save(u));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        if (!usuarioRepository.existsById(id))
            return ResponseEntity.notFound().build();

        usuarioRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
