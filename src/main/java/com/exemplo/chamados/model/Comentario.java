package com.exemplo.chamados.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Chamado chamado;

    @ManyToOne(optional = false)
    private Usuario autor;

    @Column(columnDefinition = "TEXT")
    private String texto;

    private LocalDateTime dataCriacao = LocalDateTime.now();
}
