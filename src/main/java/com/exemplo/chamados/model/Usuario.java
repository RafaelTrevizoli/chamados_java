package com.exemplo.chamados.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Usuario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(unique = true, nullable = false)
    private String email;

    private String senha; // (DEV) em produção: armazenar hash

    @Enumerated(EnumType.STRING)
    private TipoUsuario tipo = TipoUsuario.CLIENTE;

    public enum TipoUsuario { CLIENTE, ATENDENTE, ADMIN }
}
