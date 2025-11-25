package com.exemplo.chamados.controller;

import com.exemplo.chamados.model.Chamado;
import com.exemplo.chamados.model.Usuario;
import com.exemplo.chamados.repository.ChamadoRepository;
import com.exemplo.chamados.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chamados")
@CrossOrigin(origins = "*")
public class ChamadoController {

    @Autowired
    private ChamadoRepository chamadoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // ============================
    // LISTAR TODOS (dashboard geral)
    // ============================
    @GetMapping
    public ResponseEntity<?> listarTodos() {
        return ResponseEntity.ok(chamadoRepository.findAll());
    }

    // ============================
    // LISTAR POR CLIENTE (usuário comum)
    // ============================
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<?> listarPorCliente(@PathVariable Long idCliente) {
        return ResponseEntity.ok(chamadoRepository.findByClienteId(idCliente));
    }

    // ============================
    // ADMIN — FILTRAR CHAMADOS
    // ============================
    @GetMapping("/admin-filtro")
    public ResponseEntity<?> adminFiltro(
            @RequestParam Long adminId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long clienteId) {

        Usuario admin = usuarioRepository.findById(adminId).orElse(null);

        if (admin == null || admin.getNivel() != Usuario.NivelUsuario.ADMIN)
            return ResponseEntity.status(403).body("Acesso negado (não é admin)");

        List<Chamado> lista = chamadoRepository.findAll();

        // FILTRO DE STATUS
        if (status != null && !status.isBlank()) {
            lista = lista.stream()
                    .filter(c -> c.getStatus().name().equals(status))
                    .toList();
        }

        // FILTRO DE CLIENTE
        if (clienteId != null) {
            lista = lista.stream()
                    .filter(c -> c.getCliente().getId().equals(clienteId))
                    .toList();
        }

        return ResponseEntity.ok(lista);
    }

    // ============================
    // BUSCAR POR ID (com permissão)
    // ============================
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(
            @PathVariable Long id,
            @RequestParam Long usuarioId) {

        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario == null) return ResponseEntity.status(401).body("Usuário não encontrado");

        Chamado chamado = chamadoRepository.findById(id).orElse(null);
        if (chamado == null) return ResponseEntity.notFound().build();

        // Usuário comum só pode ver SE for o dono
        if (usuario.getNivel() == Usuario.NivelUsuario.COMUM &&
                !chamado.getCliente().getId().equals(usuarioId)) {

            return ResponseEntity.status(403).body("Acesso negado");
        }

        return ResponseEntity.ok(chamado);
    }

    // ============================
    // CRIAR CHAMADO (somente usuário comum)
    // ============================
    @PostMapping
    public ResponseEntity<?> criarChamado(@RequestBody CriarChamadoRequest req) {

        Usuario cliente = usuarioRepository.findById(req.getClienteId()).orElse(null);
        if (cliente == null)
            return ResponseEntity.status(404).body("Cliente não encontrado");

        Chamado chamado = new Chamado();
        chamado.setTitulo(req.getTitulo());
        chamado.setDescricao(req.getDescricao());
        chamado.setCliente(cliente);

        chamadoRepository.save(chamado);

        return ResponseEntity.ok(chamado);
    }

    public static class CriarChamadoRequest {
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

    // ============================
    // ALTERAR STATUS (ADMIN)
    // ============================
    @PutMapping("/{id}/status")
    public ResponseEntity<?> alterarStatus(
            @PathVariable Long id,
            @RequestParam Long adminId,
            @RequestParam Chamado.Status status) {

        Usuario admin = usuarioRepository.findById(adminId).orElse(null);
        if (admin == null || admin.getNivel() != Usuario.NivelUsuario.ADMIN)
            return ResponseEntity.status(403).body("Apenas administradores");

        Chamado chamado = chamadoRepository.findById(id).orElse(null);
        if (chamado == null) return ResponseEntity.notFound().build();

        chamado.setStatus(status);
        chamadoRepository.save(chamado);

        return ResponseEntity.ok(chamado);
    }

    // ============================
    // EXCLUIR (cliente ou admin)
    // ============================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarChamado(
            @PathVariable Long id,
            @RequestParam Long usuarioId) {

        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario == null) return ResponseEntity.status(401).body("Usuário não encontrado");

        Chamado chamado = chamadoRepository.findById(id).orElse(null);
        if (chamado == null) return ResponseEntity.notFound().build();

        // Cliente só pode excluir o que pertence a ele
        if (usuario.getNivel() == Usuario.NivelUsuario.COMUM &&
                !chamado.getCliente().getId().equals(usuarioId)) {

            return ResponseEntity.status(403).body("Acesso negado");
        }

        chamadoRepository.delete(chamado);
        return ResponseEntity.noContent().build();
    }
}
