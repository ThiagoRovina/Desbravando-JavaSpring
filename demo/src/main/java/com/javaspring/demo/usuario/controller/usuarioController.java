package com.javaspring.demo.usuario.controller;

import com.javaspring.demo.produtos.dao.ProdutosDAO;
import com.javaspring.demo.produtos.model.Produtos;
import com.javaspring.demo.usuario.dao.usuarioDAO;
import com.javaspring.demo.usuario.model.Usuario;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/telaLogin")
public class usuarioController {

    private final ProdutosDAO produtosdao;
    private final usuarioDAO usuariodao;
    private final PasswordEncoder passwordEncoder;

    public usuarioController(ProdutosDAO produtosdao, usuarioDAO usuariodao, PasswordEncoder passwordEncoder) {
        this.produtosdao = produtosdao;
        this.usuariodao = usuariodao;
        this.passwordEncoder = passwordEncoder;
    }


    @GetMapping("/Home")
    public String exibirHome() {
        return "Home/telaHome";
    }


    @GetMapping
    public String exibirForm() {
        return "Usuario/telaLogin";
    }

    @GetMapping("/listaUsuarios")
    public String exibirListaUsuarios(Model model) {
        List<Usuario> usuarios = usuariodao.listarTodos();
        model.addAttribute("usuarios", usuarios);
        return "Usuario/listaUsuarios";
    }

    @GetMapping("/PerfilUsuario")
    public String exibirPerfilUsuarios(Model model) {
        return "Usuario/PerfilUsuario";
    }


    public String hashSenha(String senhaEmTextoSimples) {
        if (senhaEmTextoSimples == null || senhaEmTextoSimples.isEmpty()) {
            throw new IllegalArgumentException("A senha não pode ser nula ou vazia.");
        }
        System.out.println(senhaEmTextoSimples);
        return passwordEncoder.encode(senhaEmTextoSimples);
    }

    @GetMapping("/editar/{idUsuario}")
    public String editarUsuario(@PathVariable Integer idUsuario, Model model, RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = usuariodao.buscarPorId(idUsuario);

            if (usuario == null) {
                redirectAttributes.addFlashAttribute("erro", "Usuario não encontrado.");
                return "redirect:/telaLogin/lista";
            }
            model.addAttribute("usuario", usuario);
            return "Usuario/PerfilUsuario";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("erro", "Erro ao carregar produto.");
            return "redirect:/telaLogin/listaUsuarios";
        }
    }

    @PostMapping("/salvar")
    public String salvar(@RequestParam(required = false) Integer idusuario,
                         @RequestParam String nmNome,
                         @RequestParam String nmEmail,
                         @RequestParam(required = false) String nmSenha,
                         @RequestParam String nmEndereco,
                         @RequestParam String nmTelefone,
                         RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario;
            boolean novoCadastro = (idusuario == null || idusuario == -1);

            if (!novoCadastro) {
                usuario = usuariodao.buscarPorId(idusuario);
                if (usuario == null) {
                    redirectAttributes.addFlashAttribute("erro", "Usuário não encontrado.");
                    return "redirect:/telaLogin/listaUsuarios";
                }
            } else {
                usuario = new Usuario();
            }

            usuario.setNmNome(nmNome);
            usuario.setNmEmail(nmEmail);
            usuario.setNmEndereco(nmEndereco);
            usuario.setNmTelefone(nmTelefone);

            // Só atualiza a senha se vier preenchida (evita sobrescrever com vazio)
            if (nmSenha != null && !nmSenha.isEmpty()) {
                usuario.setNmSenha(hashSenha(nmSenha));
            }

            if (novoCadastro) {
                usuariodao.gravar(usuario);
                usuarioDAO.insereLog("USUARIO", usuarioDAO.TipoOcorrenciaLog.INSERCAO);
                redirectAttributes.addFlashAttribute("message", "Usuário cadastrado com sucesso!");
            } else {
                usuariodao.editar(usuario);
                usuarioDAO.insereLog("USUARIO", usuarioDAO.TipoOcorrenciaLog.ALTERACAO);
                redirectAttributes.addFlashAttribute("message", "Usuário atualizado com sucesso!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("erro", "Erro interno no servidor.");
        }
        return "redirect:/telaLogin/listaUsuarios";
    }




    public String limparCampos(Model model) {
        model.addAttribute("produto", new Produtos());
        return "Produto/telaProduto";
    }

    @GetMapping("/excluir/{idUsuario}")
    public String excluirProduto(@PathVariable("idUsuario") Integer idUsuario, RedirectAttributes redirectAttributes) {
        try {
            usuariodao.excluir(idUsuario);
            usuariodao.insereLog("USUARIO", usuarioDAO.TipoOcorrenciaLog.EXCLUSAO);
            redirectAttributes.addFlashAttribute("message", "Produto excluído com sucesso!");

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("erro", "Erro ao excluir o produto.");
        }

        return "redirect:/telaLogin/listaUsuarios";
    }



    @PostMapping("/login")
    public String validarLogin(@RequestParam String nmEmail,
                               @RequestParam String nmSenha,
                               RedirectAttributes redirectAttributes) {

        Usuario usuario = usuariodao.buscarPorEmail(nmEmail);

        if (usuario != null && passwordEncoder.matches(nmSenha, usuario.getNmSenha())) {
            redirectAttributes.addFlashAttribute("message", "Login realizado com sucesso!");
            return "redirect:/telaLogin/Home";
        } else {
            redirectAttributes.addFlashAttribute("error", "Credenciais inválidas. Tente novamente.");
            return "redirect:/telaLogin";
        }
    }



}