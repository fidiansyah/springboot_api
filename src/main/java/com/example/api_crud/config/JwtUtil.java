package com.example.api_crud.config;

import java.util.Date;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtUtil {
	private final String secret_key = "mysecretkey";
	private long accessTokenValidity = 24 * 60 * 60 * 1000; // 24 Jam Kadaluarsa
	private final JwtParser jwtParser;
	private final String TOKEN_HEADER = "Authorization";
	private final String TOKEN_PREFIX = "Bearer ";

	public JwtUtil() {
		this.jwtParser = Jwts.parser().setSigningKey(secret_key);
	}

	public String createToken(String uuid) {
		Claims claims = Jwts.claims().setSubject(uuid);
		Date tokenValidity = new Date(System.currentTimeMillis() + accessTokenValidity);
	
		return Jwts.builder()
				.setClaims(claims)
				.setExpiration(tokenValidity)
				.signWith(SignatureAlgorithm.HS256, secret_key)
				.compact();
	}

	public String getUuidFromToken(HttpServletRequest request) {
		Claims claims = resolveClaims(request);
		if (claims != null) {
			return claims.getSubject(); // UUID disimpan di subject
		}
		return null;
	}
	
	// Date tokenValidity = new Date(System.currentTimeMillis() +
	// accessTokenValidity);
	// return Jwts.builder().setClaims(claims).setExpiration(tokenValidity)
	// .signWith(SignatureAlgorithm.HS256, secret_key).compact();
	// }

	private Claims parseJwtClaims(String token) {
		return jwtParser.parseClaimsJws(token).getBody();
	}

	public Claims resolveClaims(HttpServletRequest req) {
		try {
			String token = resolveToken(req);
			if (token != null) {
				return parseJwtClaims(token);
			}
			return null;
		} catch (ExpiredJwtException ex) {
			return null;
		} catch (Exception ex) {
			return null;
		}
	}

	public String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(TOKEN_HEADER);
		if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
			return bearerToken.substring(TOKEN_PREFIX.length());
		}
		return null;
	}

	public boolean validateClaims(Claims claims) {
		if (claims == null) {
			return false;
		}

		// Dapatkan waktu kedaluwarsa dari klaim token
		Date expirationDate = claims.getExpiration();
		if (expirationDate == null) {
			return false;
		}
		System.out.println("expirationDate : " + expirationDate);
		System.out.println("new Date : " + new Date().getTime());
		// Periksa apakah waktu kedaluwarsa sudah lewat dari waktu saat ini
		if (expirationDate.before(new Date())) {
			// Hitung selisih waktu antara waktu kedaluwarsa dan waktu saat ini
			long timeDiff = new Date().getTime() - expirationDate.getTime();
			// Pastikan waktu kedaluwarsa lebih dari 24 jam yang lalu
			return timeDiff > accessTokenValidity ? false : true;
		}

		return true;
	}

	public String getEmail(Claims claims) {
		return claims.getSubject();
	}

}

