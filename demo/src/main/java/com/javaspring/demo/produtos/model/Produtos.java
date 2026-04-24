package com.javaspring.demo.produtos.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name= "PRODUTOS")
public class Produtos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PRODUTO")
    private Integer idProduto;

    @Column(name = "NM_PRODUTO")
    private String nmProduto;

    @Column(name = "DE_PRODUTO")
    private String deProduto;

    @Column(name = "NU_PRECO")
    private int nuPreco;

    @Column(name = "QT_ESTOQUE")
    private int qtEstoque;

    @Lob
    @Column(name = "IMAGEM_PRODUTO")
    private String ImagemProduto;

    public Produtos() {
        // Default constructor - Lombok will handle getters/setters
    }
}
