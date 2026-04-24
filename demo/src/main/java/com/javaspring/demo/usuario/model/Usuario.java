package com.javaspring.demo.usuario.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name= "USUARIO")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USUARIO_ID")
    private Integer idUsuario;

    @Column(name = "NOME", nullable = false)
    private String nmNome;


    @Column(name = "EMAIL", nullable = false)
    private String nmEmail;

    @Column(name = "SENHA", nullable = false)
    private String nmSenha;

    @Column(name = "ENDERECO", nullable = false)
    private String nmEndereco;

    @Column(name = "TELEFONE", nullable = false)
    private String nmTelefone;

    public Usuario() {
        this.idUsuario = idUsuario;
        this.nmNome = nmNome;
        this.nmEmail = nmEmail;
        this.nmSenha = nmSenha;
        this.nmEndereco = nmEndereco;
        this.nmTelefone = nmTelefone;
    }

}