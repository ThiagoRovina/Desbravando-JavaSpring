package com.javaspring.demo.usuario;

import com.javaspring.demo.usuario.dao.usuarioDAO;
import com.javaspring.demo.usuario.model.Usuario;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    private final usuarioDAO usuarioDAO;

    public JpaUserDetailsService(usuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioDAO.buscarPorEmail(username);

        if (usuario == null) {
            throw new UsernameNotFoundException("Usuário não encontrado com o email: " + username);
        }

        return new User(usuario.getNmEmail(), usuario.getNmSenha(), new ArrayList<>());
    }
}
