package com.exemplo.chamados.controller;

import com.exemplo.chamados.model.Chamado;
import com.exemplo.chamados.model.Usuario;
import com.exemplo.chamados.repository.ChamadoRepository;
import com.exemplo.chamados.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chamados")
@CrossOrigin(origins = "*")
public class ChamadoController {

    @Autowired
    private ChamadoRepository chamadoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // ============================
    // LISTAR CHAMADOS DO CLIENTE
    // ============================
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<?> listarPorCliente(@PathVariable("idCliente") Long idCliente) {
        List<Chamado> lista = chamadoRepository.findByClienteId(idCliente);
        return ResponseEntity.ok(lista);
    }

    // ============================
    // LISTAR CHAMADOS PARA ADMIN (com filtros)
    // GET /api/chamados/admin?adminId=1&status=ABERTO&clienteId=2
    // ============================
    @GetMapping("/admin")
    public ResponseEntity<?> listarParaAdmin(
            @RequestParam("adminId") Long adminId,
            @RequestParam(value = "status", required = false) Chamado.Status status,
            @RequestParam(value = "clienteId", required = false) Long clienteId) {

        Usuario admin = usuarioRepository.findById(adminId).orElse(null);

        if (admin == null || admin.getNivel() != Usuario.NivelUsuario.ADMIN) {
            return ResponseEntity.status(403).body("Apenas administradores podem listar chamados como admin");
        }

        List<Chamado> lista = chamadoRepository.findAll();

        if (status != null) {
            lista = lista.stream()
                    .filter(c -> c.getStatus() == status)
                    .collect(Collectors.toList());
        }

        if (clienteId != null) {
            lista = lista.stream()
                    .filter(c -> c.getCliente() != null && clienteId.equals(c.getCliente().getId()))
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(lista);
    }

    // ============================
    // BUSCAR CHAMADO POR ID
    // ============================
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(
            @PathVariable("id") Long id,
            @RequestParam("usuarioId") Long usuarioId) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElse(null);

        if (usuario == null) {
            return ResponseEntity.status(401).body("Usuário não encontrado");
        }

        Chamado chamado = chamadoRepository.findById(id).orElse(null);

        if (chamado == null) {
            return ResponseEntity.notFound().build();
        }

        // Usuário comum só pode ver chamados dele
        if (usuario.getNivel() == Usuario.NivelUsuario.COMUM &&
                !chamado.getCliente().getId().equals(usuarioId)) {

            return ResponseEntity.status(403).body("Acesso negado ao chamado");
        }

        return ResponseEntity.ok(chamado);
    }

    // ============================
    // CRIAR CHAMADO
    // ============================
    @PostMapping
    public ResponseEntity<?> criarChamado(@RequestBody CriarChamadoRequest request) {

        Usuario cliente = usuarioRepository.findById(request.getClienteId())
                .orElse(null);

        if (cliente == null) {
            return ResponseEntity.status(404).body("Cliente não encontrado");
        }

        Chamado chamado = new Chamado();
        chamado.setTitulo(request.getTitulo());
        chamado.setDescricao(request.getDescricao());
        chamado.setCliente(cliente);

        chamadoRepository.save(chamado);

        return ResponseEntity.ok(chamado);
    }

    // DTO usado pelo frontend para criar
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

    // DTO p/ admin atualizar status + comentário
    public static class AtualizarAdminRequest {
        private Chamado.Status status;
        private String comentarioAdmin;

        public Chamado.Status getStatus() { return status; }
        public void setStatus(Chamado.Status status) { this.status = status; }

        public String getComentarioAdmin() { return comentarioAdmin; }
        public void setComentarioAdmin(String comentarioAdmin) { this.comentarioAdmin = comentarioAdmin; }
    }

    // ============================
    // ATUALIZAR CHAMADO (ADMIN)
    // PUT /api/chamados/{id}/admin?adminId=1
    // body: { "status": "RESOLVIDO", "comentarioAdmin": "..." }
    // ============================
    @PutMapping("/{id}/admin")
    public ResponseEntity<?> atualizarChamadoAdmin(
            @PathVariable("id") Long id,
            @RequestParam("adminId") Long adminId,
            @RequestBody AtualizarAdminRequest dados) {

        Usuario admin = usuarioRepository.findById(adminId).orElse(null);

        if (admin == null || admin.getNivel() != Usuario.NivelUsuario.ADMIN) {
            return ResponseEntity.status(403).body("Apenas administradores podem atualizar chamados");
        }

        Chamado chamado = chamadoRepository.findById(id).orElse(null);
        if (chamado == null) return ResponseEntity.notFound().build();

        if (dados.getStatus() != null) {
            chamado.setStatus(dados.getStatus());
        }
        chamado.setComentarioAdmin(dados.getComentarioAdmin());

        chamadoRepository.save(chamado);

        return ResponseEntity.ok(chamado);
    }

    // ============================
    // DELETAR CHAMADO (COMUM só pode deletar o próprio)
    // ============================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarChamado(
            @PathVariable("id") Long id,
            @RequestParam("usuarioId") Long usuarioId) {

        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);

        if (usuario == null) {
            return ResponseEntity.status(401).body("Usuário não encontrado");
        }

        Chamado chamado = chamadoRepository.findById(id).orElse(null);

        if (chamado == null) {
            return ResponseEntity.notFound().build();
        }

        if (usuario.getNivel() == Usuario.NivelUsuario.COMUM &&
                !chamado.getCliente().getId().equals(usuarioId)) {

            return ResponseEntity.status(403).body("Acesso negado");
        }

        chamadoRepository.delete(chamado);
        return ResponseEntity.noContent().build();
    }
}
