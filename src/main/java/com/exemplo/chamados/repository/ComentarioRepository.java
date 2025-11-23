package com.exemplo.chamados.repository;

import com.exemplo.chamados.model.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    List<Comentario> findByChamadoIdOrderByDataCriacaoAsc(Long chamadoId);
}
