package com.exemplo.chamados.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class Chamado {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ABERTO;

    @ManyToOne(optional = false)
    private com.exemplo.chamados.model.Usuario cliente;

    private LocalDateTime dataCriacao = LocalDateTime.now();

    public enum Status { ABERTO, EM_ANDAMENTO, RESOLVIDO, FECHADO }
}
