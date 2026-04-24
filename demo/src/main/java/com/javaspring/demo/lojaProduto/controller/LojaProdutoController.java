package com.javaspring.demo.lojaProduto.controller;

import com.javaspring.demo.produtos.dao.ProdutosDAO;
import com.javaspring.demo.produtos.model.Produtos;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/lojaProduto")
public class LojaProdutoController {
    private final ProdutosDAO produtosdao;

    public LojaProdutoController(ProdutosDAO produtosdao) {
        this.produtosdao = produtosdao;
    }


    public void compraProdutos(Produtos precoTotal, String metodoPagamento){

    }




    @GetMapping("/lista")
    public String exibirFormLista(Model model) {
        List<Produtos> produtos = produtosdao.listarTodos();
        model.addAttribute("produtos", produtos);
        return "Produto/listaProdutos";
    }

    @GetMapping("/imagem/{nome}")
    public ResponseEntity<byte[]> exibirImagem(@PathVariable("nome") String nomeDoArquivo) throws IOException {
        String uploadDir = "D:/Gerenciador/ProjectOd/Sistema-Basico/demo/uploads/";
        Path filePath = Paths.get(uploadDir, nomeDoArquivo);

        if(!Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }

        byte[] bytes = Files.readAllBytes(filePath);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }



}
