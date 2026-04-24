package com.javaspring.demo.home.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/Home")
public class homeController {

    @GetMapping
    public String exibirHome() {
        return "Home/telaHome";
    }

    @GetMapping("/Produto")
    public String exibirProduto() {return "redirect:/telaProduto/lista";}

    @GetMapping("/Usuario")
    public String exibirUsuario() {
        return "redirect:/telaLogin/listaUsuarios";
    }

    @GetMapping("/PerfilUsuario")
    public String exibirPerfilUsuario() {
        return "redirect:/telaLogin/editar/{idUsuario}";
    }

}
