package com.javaspring.demo.lojaProduto.model;


import jakarta.persistence.*;

@Table(name = "ITEM_CARRINHO")
public class LojaProduto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ITEM_CARRINHO_ID")
    private Integer idItemCarrinho;

    @Column(name = "CARRINHO_ID")
    private Integer idCarrinho;

    @Column(name = "PRODUTO_ID")
    private Integer idProduto;

    @Column(name = "QUANTIDADE")
    private int qtdeCarrinho;

    @Column(name = "PRECO")
    private int vlPreco;



}
