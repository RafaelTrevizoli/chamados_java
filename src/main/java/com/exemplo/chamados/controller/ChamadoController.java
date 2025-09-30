package com.exemplo.chamados.controller;

import com.exemplo.chamados.model.Chamado;
import com.exemplo.chamados.model.Usuario;
import com.exemplo.chamados.repository.ChamadoRepository;
import com.exemplo.chamados.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/chamados")
@CrossOrigin
public class ChamadoController {

    @Autowired
    private ChamadoRepository chamadoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // ✅ Criar chamado
    @PostMapping("/criar")
    public ResponseEntity<?> criar(@RequestBody Map<String, String> payload) {
        try {
            String titulo = payload.get("titulo");
            String descricao = payload.get("descricao");
            Long clienteId = Long.parseLong(payload.get("clienteId"));

            Optional<Usuario> cliente = usuarioRepository.findById(clienteId);
            if (!cliente.isPresent()) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("erro", "Cliente não encontrado"));
            }

            Chamado chamado = new Chamado();
            chamado.setTitulo(titulo);
            chamado.setDescricao(descricao);
            chamado.setCliente(cliente.get());
            chamado.setStatus(Chamado.Status.ABERTO);

            Chamado salvo = chamadoRepository.save(chamado);

            Map<String, Object> resposta = new HashMap<>();
            resposta.put("id", salvo.getId());
            resposta.put("titulo", salvo.getTitulo());
            resposta.put("descricao", salvo.getDescricao());
            resposta.put("status", salvo.getStatus().name());
            resposta.put("dataCriacao", salvo.getDataCriacao());

            return ResponseEntity.ok(resposta);

        } catch (Exception e) {
            System.out.println("❌ ERRO AO CRIAR CHAMADO:");
            e.printStackTrace();
            return ResponseEntity.status(500).body(Collections.singletonMap("erro", e.getMessage()));
        }
    }

    // ✅ Listar chamados por cliente (com DTO simples)
    @GetMapping("/cliente/{id}")
    public ResponseEntity<?> listarPorCliente(@PathVariable("id") Long id) {
        try {
            List<Chamado> chamados = chamadoRepository.findByCliente_Id(id);
            List<Map<String, Object>> resposta = new ArrayList<>();

            for (Chamado c : chamados) {
                Map<String, Object> dto = new HashMap<>();
                dto.put("id", c.getId());
                dto.put("titulo", c.getTitulo());
                dto.put("descricao", c.getDescricao());
                dto.put("status", c.getStatus().name());
                dto.put("dataCriacao", c.getDataCriacao());
                resposta.add(dto);
            }

            System.out.println("✅ Chamados retornados: " + resposta.size());
            return ResponseEntity.ok(resposta);

        } catch (Exception e) {
            System.out.println("❌ ERRO AO LISTAR CHAMADOS:");
            e.printStackTrace();
            return ResponseEntity.status(500).body(Collections.singletonMap("erro", e.getMessage()));
        }
    }

    // ✅ Atualizar status do chamado
    @PutMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatus(@PathVariable("id") Long id, @RequestBody Map<String, String> payload) {
        try {
            String novoStatus = payload.get("status");

            Optional<Chamado> chamadoOpt = chamadoRepository.findById(id);
            if (!chamadoOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Chamado chamado = chamadoOpt.get();
            try {
                Chamado.Status status = Chamado.Status.valueOf(novoStatus);
                chamado.setStatus(status);
                chamadoRepository.save(chamado);
                return ResponseEntity.ok(Collections.singletonMap("mensagem", "Status atualizado com sucesso"));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("erro", "Status inválido"));
            }

        } catch (Exception e) {
            System.out.println("❌ ERRO AO ATUALIZAR STATUS:");
            e.printStackTrace();
            return ResponseEntity.status(500).body(Collections.singletonMap("erro", e.getMessage()));
        }
    }
}
