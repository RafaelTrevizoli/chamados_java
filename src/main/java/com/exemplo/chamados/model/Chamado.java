package com.exemplo.chamados.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Getter
@Setter
public class Chamado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ABERTO;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"senha", "nivel", "hibernateLazyInitializer", "handler"})
    private Usuario cliente;

    private LocalDateTime dataCriacao = LocalDateTime.now();

    // Comentário único do admin sobre o chamado
    @Column(columnDefinition = "TEXT")
    private String comentarioAdmin;

    public enum Status {
        ABERTO,
        EM_ANDAMENTO,
        RESOLVIDO,
        FECHADO
    }
}
