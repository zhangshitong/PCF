package com.iSupervision.config;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
@ComponentScan
@RestController
public class SsoApplication {

	@RequestMapping("/user")
	public Principal user(Principal user) {
		return user;
	}

	public static void main(String[] args) {
		SpringApplication.run(SsoApplication.class, args);
	}

	@Controller
	public static class LoginSeccuess {

		@RequestMapping("/login")
		public String loginSuccess() {
			return "redirect:/#/";
		}

		@RequestMapping(value = "/logout")
	    public String logout(HttpSession session) {
			session.invalidate();
			return "";
	    }

	}

	@Configuration
	@Component
	@EnableOAuth2Sso
	public static class LoginConfigurer extends WebSecurityConfigurerAdapter {

		@Value("${security.trust.origin}")
		private String trustOrigin;

		@Override
		public void configure(HttpSecurity http) throws Exception {
			http.headers().frameOptions().disable();
			http.antMatcher("/**").authorizeRequests().anyRequest()
					.authenticated().and().csrf().disable()
					.addFilterAfter(xhrFieldsHeaderFilter(), CsrfFilter.class)
					.logout().logoutUrl("/logout").permitAll()
					.logoutSuccessUrl("/");
		}

		private Filter xhrFieldsHeaderFilter() {
			return new OncePerRequestFilter() {
				@Override
				protected void doFilterInternal(HttpServletRequest request,
						HttpServletResponse response, FilterChain filterChain)
						throws ServletException, IOException {
//					CsrfToken csrf = (CsrfToken) request
//							.getAttribute(CsrfToken.class.getName());
//					if (csrf != null) {
//						Cookie cookie = new Cookie("XSRF-TOKEN",
//								csrf.getToken());
//						cookie.setPath(request.getContextPath());
//						response.addCookie(cookie);
//					}
					response.addHeader("Access-Control-Allow-Origin",trustOrigin);
					response.addHeader("Access-Control-Allow-Methods","GET,POST,DELETE,PUT");
					response.addHeader("Access-Control-Allow-Credentials","true");
					filterChain.doFilter(request, response);
				}
			};
		}

	}
}
