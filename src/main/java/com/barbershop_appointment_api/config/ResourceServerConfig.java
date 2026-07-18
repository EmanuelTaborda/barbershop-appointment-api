/**
 * Configuração central de segurança da aplicação (Spring Security).
 *
 * Define duas filter chains independentes, priorizadas por @Order:
 *
 *   1. h2SecurityFilterChain (Order 1) — ativa apenas no profile "test",
 *      libera acesso ao console do H2 Database (sem CSRF, sem X-Frame-Options)
 *      para facilitar inspeção do banco em ambiente de desenvolvimento/teste.
 *
 *   2. rsSecurityFilterChain (Order 3) — chain principal da API REST, que
 *      configura a aplicação como um OAuth2 Resource Server: valida tokens JWT
 *      recebidos no header Authorization e extrai as authorities (roles como
 *      ROLE_CLIENT, ROLE_BARBER, ROLE_ADMIN) via jwtAuthenticationConverter.
 *
 * Também centraliza a configuração de CORS (origens permitidas via
 * cors.origins no application.properties), permitindo que o frontend
 * (rodando em outra origem) consiga se comunicar com a API enviando
 * credenciais (Authorization header) nas requisições.
 *
 * Observação: a API é stateless (autenticação via JWT, sem sessão/cookies),
 * por isso o CSRF é desabilitado em ambas as chains — essa proteção só faz
 * sentido para fluxos autenticados por cookie de sessão.
 */

package com.barbershop_appointment_api.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class ResourceServerConfig {

	@Value("${cors.origins}")
	private String corsOrigins; /// Origens permitidas para CORS, injetadas do application.properties
	/// (ex: "http://localhost:3000,https://meusite.com")


	/**
	 * Filter chain exclusiva para o console do H2.
	 * Só é ativada no profile "test", evitando expor o H2 em produção.
	 * @Order(1) garante que essa chain seja avaliada ANTES da chain principal (rsSecurityFilterChain),
	 * já que securityMatcher() restringe o escopo dela apenas às rotas do H2 console.
	 */
	@Bean
	@Profile("test")
	@Order(1)
	public SecurityFilterChain h2SecurityFilterChain(HttpSecurity http) throws Exception {

		http.securityMatcher(PathRequest.toH2Console()).csrf(csrf -> csrf.disable())// CSRF desabilitado pq o H2 console usa formulários HTML simples, sem token CSRF
				.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));
		return http.build();
	}


	/**
	 * Filter chain principal da API REST (Resource Server).
	 * @Order(3) faz ela ser avaliada por último, depois da chain do H2 (Order 1).
	 * Como não tem securityMatcher(), essa chain casa com QUALQUER request que não tenha sido
	 * capturado pelas chains anteriores.
	 */
	@Bean
	@Order(3)
	public SecurityFilterChain rsSecurityFilterChain(HttpSecurity http) throws Exception {

		http.csrf(csrf -> csrf.disable());// CSRF é uma proteção pensada pra sessões com cookies; como a API é stateless (JWT no header), não se aplica aqui
		http.authorizeHttpRequests(authorize -> authorize
				// rotas públicas (cadastro, login, etc) - ajuste conforme seus endpoints reais
				.requestMatchers(HttpMethod.POST, "/users/cliente").permitAll()
				.requestMatchers("/oauth2/**").permitAll()
				// todo o resto exige token válido
				.anyRequest().authenticated()
		);

		http.oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
				.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
		);

		http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
		return http.build();
	}


	/**
	 * Converte as claims do JWT em authorities do Spring Security (ex: ROLE_CLIENT, ROLE_BARBER).
	 * Necessário pq por padrão o Spring espera a claim "scope"/"scp", mas aqui o token
	 * carrega as roles numa claim customizada chamada "authorities".
	 */
	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
		grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");// nome da claim no JWT onde estão as roles
		grantedAuthoritiesConverter.setAuthorityPrefix("");// remove o prefixo "SCOPE_" padrão, já que as roles já vêm como "ROLE_CLIENT" etc.

		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
		return jwtAuthenticationConverter;
	}

	/**
	 * Define as regras de CORS: quais origens, métodos e headers são permitidos
	 * nas requisições cross-origin (ex: frontend rodando em outra porta/domínio).
	 */
	@Bean
	CorsConfigurationSource corsConfigurationSource() {

		String[] origins = corsOrigins.split(",");// transforma a string do properties numa lista de origens

		CorsConfiguration corsConfig = new CorsConfiguration();
		corsConfig.setAllowedOriginPatterns(Arrays.asList(origins));// origins com pattern (aceita wildcard, ex: https://*.meusite.com)
		corsConfig.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE", "PATCH"));
		corsConfig.setAllowCredentials(true);// permite envio de cookies/Authorization header em requisições cross-origin
		corsConfig.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));// headers que o frontend pode enviar

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfig);// aplica essa config pra todas as rotas
		return source;
	}

	@Bean
	public CorsFilter corsFilter() {
		return new CorsFilter(corsConfigurationSource());
	}
}
