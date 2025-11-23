package com.exemplo.chamados.controller;

import com.exemplo.chamados.model.Chamado;
import com.exemplo.chamados.model.Comentario;
import com.exemplo.chamados.model.Usuario;
import com.exemplo.chamados.repository.ChamadoRepository;
import com.exemplo.chamados.repository.ComentarioRepository;
import com.exemplo.chamados.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comentarios")
@CrossOrigin(origins = "*")
public class ComentarioController {

    @Autowired
    private ComentarioRepository comentarioRepository;

    @Autowired
    private ChamadoRepository chamadoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // LISTAR COMENTÁRIOS DO CHAMADO
    @GetMapping("/{chamadoId}")
    public ResponseEntity<?> listar(@PathVariable Long chamadoId) {
        return ResponseEntity.ok(
                comentarioRepository.findByChamadoIdOrderByDataCriacaoAsc(chamadoId)
        );
    }

    // CRIAR COMENTÁRIO (apenas admin)
    @PostMapping
    public ResponseEntity<?> criar(@RequestParam Long adminId, @RequestBody Comentario dto) {

        Usuario admin = usuarioRepository.findById(adminId).orElse(null);

        if (admin == null || admin.getNivel() != Usuario.NivelUsuario.ADMIN)
            return ResponseEntity.status(403).body("Apenas admin pode comentar");

        Chamado chamado = chamadoRepository.findById(dto.getChamado().getId()).orElse(null);

        if (chamado == null)
            return ResponseEntity.status(404).body("Chamado não encontrado");

        Comentario novo = new Comentario();
        novo.setChamado(chamado);
        novo.setAutor(admin);
        novo.setTexto(dto.getTexto());

        comentarioRepository.save(novo);

        return ResponseEntity.ok(novo);
    }
}
