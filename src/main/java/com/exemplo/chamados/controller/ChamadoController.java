package com.exemplo.chamados.controller;

import com.exemplo.chamados.model.Chamado;
import com.exemplo.chamados.model.Usuario;
import com.exemplo.chamados.repository.ChamadoRepository;
import com.exemplo.chamados.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/chamados")
@CrossOrigin
public class ChamadoController {

    @Autowired
    private ChamadoRepository chamadoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/criar")
    public ResponseEntity<?> criar(@RequestBody Map<String, String> payload) {
        String titulo = payload.get("titulo");
        String descricao = payload.get("descricao");
        Long clienteId = null;
        try { clienteId = Long.parseLong(payload.get("clienteId")); } catch (Exception ignored) {}

        if (titulo == null || descricao == null || clienteId == null) {
            return ResponseEntity.badRequest().body(Map.of("erro", "titulo, descricao e clienteId são obrigatórios"));
        }

        Optional<Usuario> cliente = usuarioRepository.findById(clienteId);
        if (cliente.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("erro", "cliente não encontrado"));
        }

        Chamado chamado = new Chamado();
        chamado.setTitulo(titulo);
        chamado.setDescricao(descricao);
        chamado.setCliente(cliente.get());
        return ResponseEntity.ok(chamadoRepository.save(chamado));
    }

    @GetMapping("/cliente/{id}")
    public List<Chamado> listarPorCliente(@PathVariable Long id) {
        return chamadoRepository.findByClienteId(id);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatus(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        return chamadoRepository.findById(id).map(c -> {
            try {
                Chamado.Status novo = Chamado.Status.valueOf(payload.getOrDefault("status","ABERTO"));
                c.setStatus(novo);
                return ResponseEntity.ok(chamadoRepository.save(c));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(Map.of("erro","status inválido"));
            }
        }).orElse(ResponseEntity.notFound().build());
    }
}
