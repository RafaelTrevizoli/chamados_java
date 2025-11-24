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
    // NOVO → LISTAR TODOS (para dashboard)
    // ============================
    @GetMapping
    public ResponseEntity<?> listarTodos() {
        return ResponseEntity.ok(chamadoRepository.findAll());
    }

    // ============================
    // LISTAR CHAMADOS DO CLIENTE
    // ============================
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<?> listarPorCliente(@PathVariable Long idCliente) {
        return ResponseEntity.ok(chamadoRepository.findByClienteId(idCliente));
    }

    // ============================
    // BUSCAR POR ID
    // ============================
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(
            @PathVariable Long id,
            @RequestParam Long usuarioId) {

        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario == null) return ResponseEntity.status(401).body("Usuário não encontrado");

        Chamado c = chamadoRepository.findById(id).orElse(null);
        if (c == null) return ResponseEntity.notFound().build();

        if (usuario.getNivel() == Usuario.NivelUsuario.COMUM &&
                !c.getCliente().getId().equals(usuarioId))
            return ResponseEntity.status(403).body("Acesso negado");

        return ResponseEntity.ok(c);
    }

    // ============================
    // ADMIN → LISTAR TODOS
    // ============================
    @GetMapping("/admin-filtro")
    public ResponseEntity<?> adminFiltro(@RequestParam Long adminId) {

        Usuario admin = usuarioRepository.findById(adminId).orElse(null);
        if (admin == null || admin.getNivel() != Usuario.NivelUsuario.ADMIN)
            return ResponseEntity.status(403).body("Acesso negado");

        return ResponseEntity.ok(chamadoRepository.findAll());
    }

    // ============================
    // CRIAR CHAMADO
    // ============================
    @PostMapping
    public ResponseEntity<?> criarChamado(@RequestBody CriarChamadoRequest req) {

        Usuario cliente = usuarioRepository.findById(req.getClienteId()).orElse(null);
        if (cliente == null)
            return ResponseEntity.status(404).body("Cliente não encontrado");

        Chamado c = new Chamado();
        c.setTitulo(req.getTitulo());
        c.setDescricao(req.getDescricao());
        c.setCliente(cliente);

        chamadoRepository.save(c);

        return ResponseEntity.ok(c);
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
    // ADMIN → ALTERAR STATUS
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
    // EXCLUIR CHAMADO (cliente ou admin)
    // ============================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarChamado(
            @PathVariable Long id,
            @RequestParam Long usuarioId) {

        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario == null) return ResponseEntity.status(401).body("Usuário não encontrado");

        Chamado c = chamadoRepository.findById(id).orElse(null);
        if (c == null) return ResponseEntity.notFound().build();

        if (usuario.getNivel() == Usuario.NivelUsuario.COMUM &&
                !c.getCliente().getId().equals(usuarioId))
            return ResponseEntity.status(403).body("Acesso negado");

        chamadoRepository.delete(c);
        return ResponseEntity.noContent().build();
    }
}
