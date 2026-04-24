package com.javaspring.demo.usuario;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JpaUserDetailsService jpaUserDetailsService;

    public SecurityConfig(JpaUserDetailsService jpaUserDetailsService) {
        this.jpaUserDetailsService = jpaUserDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .userDetailsService(jpaUserDetailsService) // Usa o nosso serviço de utilizador
                .authorizeHttpRequests(authorize -> authorize
                        // Permite acesso público a CSS, JS e imagens
                        .requestMatchers("/css/**", "/js/**", "/imagens/**").permitAll()
                        // Permite acesso à sua página de login e ao endpoint de salvar
                        .requestMatchers("/", "/telaLogin", "/telaLogin/salvar").permitAll()
                        // Qualquer outra requisição precisa de autenticação
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        // Define a sua página de login personalizada
                        .loginPage("/telaLogin")
                        // O Spring espera que os campos se chamem 'username' e 'password' no HTML
                        .usernameParameter("nmEmail") // Informa que o campo de utilizador é 'nmEmail'
                        .passwordParameter("nmSenha") // Informa que o campo de senha é 'nmSenha'
                        // Endpoint para onde o formulário de login envia os dados
                        .loginProcessingUrl("/telaLogin/login")
                        // Para onde ir após um login bem-sucedido
                        .defaultSuccessUrl("/Home", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        // URL para deslogar
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        // Para onde ir após o logout
                        .logoutSuccessUrl("/telaLogin")
                        .permitAll()
                );

        return http.build();
    }
}

