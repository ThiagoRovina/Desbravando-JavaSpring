package com.javaspring.demo.produtos.controller;

import com.javaspring.demo.produtos.dao.ProdutosDAO;
import com.javaspring.demo.produtos.model.Produtos;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/telaProduto")
public class ProdutosController {

    private final ProdutosDAO produtosdao;

    public ProdutosController(ProdutosDAO produtosdao) {
        this.produtosdao = produtosdao;
    }

    @GetMapping
    public String exibirForm(Model model) {
        model.addAttribute("produto", new Produtos());
        return "Produto/telaProduto";
    }

    @GetMapping("/lista")
    public String exibirFormLista(Model model) {
        List<Produtos> produtos = produtosdao.listarTodos();
        model.addAttribute("produtos", produtos);
        return "Produto/listaProdutos";
    }

    @PostMapping("/salvarProduto")
    public String salvarProduto(@RequestParam(required = false) Integer idProduto,
                                @RequestParam String nmProduto,
                                @RequestParam String deProduto,
                                @RequestParam String nuPreco,
                                @RequestParam int qtEstoque,
                                @RequestParam(required = false) String nmImagemProduto,
                                RedirectAttributes redirectAttributes) {
        try {
            Produtos produto = new Produtos();
            produto.setNmProduto(nmProduto);
            produto.setDeProduto(deProduto);
            produto.setQtEstoque(qtEstoque);
            produto.setImagemProduto(nmImagemProduto);

            // Converte o preço formatado (ex: "1.234,56") para centavos (int)
            if (nuPreco != null && !nuPreco.isEmpty()) {
                int precoCentavos = (int) (Double.parseDouble(nuPreco.replace(".", "").replace(",", ".")) * 100);
                produto.setNuPreco(precoCentavos);
            }

            if (idProduto == null) {
                produtosdao.gravar(produto);
                ProdutosDAO.insereLog("PRODUTOS", ProdutosDAO.TipoOcorrenciaLog.INSERCAO);
                redirectAttributes.addFlashAttribute("message", "Produto cadastrado com sucesso!");
            } else {
                produto.setIdProduto(idProduto);
                produtosdao.editar(produto);
                ProdutosDAO.insereLog("PRODUTOS", ProdutosDAO.TipoOcorrenciaLog.ALTERACAO);
                redirectAttributes.addFlashAttribute("message", "Produto atualizado com sucesso!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("erro", "Erro interno no servidor.");
            return "Produto/telaProduto";
        }

        return "redirect:/telaProduto/lista";
    }


    @GetMapping("/editar/{idProduto}")
    public String editarProduto(@PathVariable Integer idProduto, Model model, RedirectAttributes redirectAttributes) {
        try {
            Produtos produto = produtosdao.buscarPorId(idProduto);

            if (produto == null) {
                redirectAttributes.addFlashAttribute("erro", "Produto não encontrado.");
                return "redirect:/telaProduto/lista";
            }
            model.addAttribute("produto", produto);
            return "Produto/telaProduto";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("erro", "Erro ao carregar produto.");
            return "redirect:/telaProduto/lista";
        }
    }

    @GetMapping("/excluir/{idProduto}")
    public String excluirProduto(@PathVariable("idProduto") Integer idProduto, RedirectAttributes redirectAttributes) {
        try {
            produtosdao.excluir(idProduto);
            ProdutosDAO.insereLog("PRODUTOS", ProdutosDAO.TipoOcorrenciaLog.EXCLUSAO);
            redirectAttributes.addFlashAttribute("message", "Produto excluído com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("erro", "Erro ao excluir o produto.");
        }

        return "redirect:/telaProduto/lista";
    }
}

