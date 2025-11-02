package com.exemplo.chamados.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String senha;

    // Campo adicional para nível de usuário
    @Enumerated(EnumType.STRING)
    private NivelUsuario nivel = NivelUsuario.COMUM;

    public enum NivelUsuario {
        COMUM,
        ADMIN
    }
}
