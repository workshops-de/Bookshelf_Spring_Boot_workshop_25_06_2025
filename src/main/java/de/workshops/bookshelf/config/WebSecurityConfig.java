package de.workshops.bookshelf.config;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
class WebSecurityConfig {

    private final JdbcClient jdbcClient;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(
                        authorize ->
                                authorize
                                        .anyRequest().authenticated()
                )
                .httpBasic(withDefaults())
                .formLogin(withDefaults())
                .build();
    }

    @Bean
    UserDetailsService userDetailsService() {
        return username ->
            jdbcClient
                .sql("SELECT * FROM bookshelf_user WHERE username = :username")
                .param("username", username)
                .query(
                        (rs, rowNum) ->
                            new User(
                                rs.getString("username"),
                                rs.getString("password"),
                                List.of(
                                    new SimpleGrantedAuthority(rs.getString("role"))
                                )
                            )
                )
                .optional()
                .orElse(null);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
