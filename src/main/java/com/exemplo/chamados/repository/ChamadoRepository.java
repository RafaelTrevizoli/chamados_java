package com.exemplo.chamados.repository;

import com.exemplo.chamados.model.Chamado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChamadoRepository extends JpaRepository<Chamado, Long> {
    List<Chamado> findByClienteId(Long clienteId);
}

