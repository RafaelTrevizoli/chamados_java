package com.exemplo.chamados.controller;

import com.exemplo.chamados.model.Chamado;
import com.exemplo.chamados.model.Usuario;
import com.exemplo.chamados.repository.ChamadoRepository;
import com.exemplo.chamados.repository.UsuarioRepository;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/chamados")
@CrossOrigin(origins = "*")
public class ChamadoController {

    private final ChamadoRepository chamadoRepository;
    private final UsuarioRepository usuarioRepository;

    public ChamadoController(ChamadoRepository chamadoRepository, UsuarioRepository usuarioRepository) {
        this.chamadoRepository = chamadoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // ✅ Criar chamado
    @PostMapping
    public ResponseEntity<?> criarChamado(@RequestBody ChamadoRequest request) {
        try {
            Usuario cliente = usuarioRepository.findById(request.getClienteId())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            Chamado chamado = new Chamado();
            chamado.setTitulo(request.getTitulo());
            chamado.setDescricao(request.getDescricao());
            chamado.setCliente(cliente);

            Chamado salvo = chamadoRepository.save(chamado);
            return ResponseEntity.status(HttpStatus.CREATED).body(salvo);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao criar chamado: " + e.getMessage());
        }
    }

    // ✅ Listar chamados por cliente
    @GetMapping("/cliente/{id}")
    public ResponseEntity<?> listarPorCliente(@PathVariable("id") Long clienteId) {
        try {
            List<Chamado> chamados = chamadoRepository.findByClienteId(clienteId);
            return ResponseEntity.ok(chamados);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao buscar chamados: " + e.getMessage());
        }
    }

    // ✅ DTO para request
    public static class ChamadoRequest {
        private String titulo;
        private String descricao;
        private Long clienteId;

        public String getTitulo() { return titulo; }
        public void setTitulo(String titulo) { this.titulo = titulo; }

        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }

        public Long getClienteId() { return clienteId; }
        public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    }
}
