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
    // LISTAR CHAMADOS DO CLIENTE
    // ============================
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<?> listarPorCliente(@PathVariable Long idCliente) {
        return ResponseEntity.ok(chamadoRepository.findByClienteId(idCliente));
    }

    // ============================
    // BUSCAR CHAMADO POR ID (usuarioId OPCIONAL)
    // ============================
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(
            @PathVariable Long id,
            @RequestParam(required = false) Long usuarioId) {

        Chamado chamado = chamadoRepository.findById(id).orElse(null);

        if (chamado == null) {
            return ResponseEntity.notFound().build();
        }

        // Se nenhum usuarioId foi enviado (ex: navbar/includes), apenas retorna
        if (usuarioId == null) {
            return ResponseEntity.ok(chamado);
        }

        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);

        if (usuario == null) {
            return ResponseEntity.status(401).body("Usuário não encontrado");
        }

        // Se for usuário comum, só pode acessar o próprio chamado
        if (usuario.getNivel() == Usuario.NivelUsuario.COMUM &&
                !chamado.getCliente().getId().equals(usuarioId)) {

            return ResponseEntity.status(403).body("Acesso negado a este chamado");
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

    // DTO usado pelo frontend
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
    // ATUALIZAR CHAMADO (admin)
    // ============================
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarChamado(
            @PathVariable Long id,
            @RequestParam Long adminId,
            @RequestBody Chamado dados) {

        Usuario admin = usuarioRepository
                .findById(adminId)
                .orElse(null);

        if (admin == null || admin.getNivel() != Usuario.NivelUsuario.ADMIN) {
            return ResponseEntity.status(403).body("Apenas administradores podem atualizar chamados");
        }

        Chamado chamado = chamadoRepository.findById(id).orElse(null);

        if (chamado == null) {
            return ResponseEntity.notFound().build();
        }

        chamado.setTitulo(dados.getTitulo());
        chamado.setDescricao(dados.getDescricao());
        chamado.setStatus(dados.getStatus());

        chamadoRepository.save(chamado);

        return ResponseEntity.ok(chamado);
    }

    // ============================
    // ALTERAR STATUS (admin)
    // ============================
    @PutMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatus(
            @PathVariable Long id,
            @RequestParam Long adminId,
            @RequestParam Chamado.Status status) {

        Usuario admin = usuarioRepository.findById(adminId).orElse(null);

        if (admin == null || admin.getNivel() != Usuario.NivelUsuario.ADMIN) {
            return ResponseEntity.status(403).body("Apenas administradores podem alterar status");
        }

        Chamado chamado = chamadoRepository.findById(id).orElse(null);

        if (chamado == null) {
            return ResponseEntity.notFound().build();
        }

        chamado.setStatus(status);
        chamadoRepository.save(chamado);

        return ResponseEntity.ok(chamado);
    }

    // ============================
    // DELETAR CHAMADO
    // ============================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarChamado(
            @PathVariable Long id,
            @RequestParam Long usuarioId) {

        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);

        if (usuario == null) {
            return ResponseEntity.status(401).body("Usuário não encontrado");
        }

        Chamado chamado = chamadoRepository.findById(id).orElse(null);

        if (chamado == null) {
            return ResponseEntity.notFound().build();
        }

        // usuário comum só pode apagar os próprios chamados
        if (usuario.getNivel() == Usuario.NivelUsuario.COMUM &&
                !chamado.getCliente().getId().equals(usuarioId)) {

            return ResponseEntity.status(403).body("Acesso negado");
        }

        chamadoRepository.delete(chamado);
        return ResponseEntity.noContent().build();
    }
}
