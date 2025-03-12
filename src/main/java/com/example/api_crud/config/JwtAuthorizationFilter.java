package com.example.api_crud.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.*;
import org.springframework.security.core.context.SecurityContextHolder;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {
	private JwtUtil jwtUtil = new JwtUtil();
	private ObjectMapper mapper = new ObjectMapper();

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		Map<String, Object> errorDetails = new HashMap<>();

		try {
			String accessToken = jwtUtil.resolveToken(request);
			System.out.println("token : " + accessToken);
			if (accessToken != null) {
				Claims claims = jwtUtil.resolveClaims(request);

				if (claims != null & jwtUtil.validateClaims(claims)) {
					String email = claims.getSubject();
					System.out.println("email : " + email);
					Authentication authentication = new UsernamePasswordAuthenticationToken(email, "",
							new ArrayList<>());
					SecurityContextHolder.getContext().setAuthentication(authentication);
				} else {
					System.out.println("EXPIRED ");
				}
			}
		} catch (Exception e) {
			errorDetails.put("message", "Authentication Error");
			errorDetails.put("details", e.getMessage());
			response.setStatus(HttpStatus.FORBIDDEN.value());
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			mapper.writeValue(response.getWriter(), errorDetails);
		}
		filterChain.doFilter(request, response);
	}

}
