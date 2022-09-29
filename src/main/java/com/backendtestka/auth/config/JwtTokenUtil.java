package com.backendtestka.auth.config;

import com.backendtestka.auth.AccountModel;
import com.backendtestka.auth.AuthService;
import com.backendtestka.helpers.InvalidAccountIdException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil implements Serializable {
    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;
    private static final long serialVersionUID = -2550185165626007488L;
    @Autowired
    AuthService authService;
    @Value("${jwt.secret}")
    private String secret;

    //retrieve username from jwt token
    public String getAccountIdFromToken(String token) {

        token = token.replace("Bearer ", "");
        return getClaimFromToken(token, Claims::getSubject);
    }

    //retrieve expiration date from jwt token
    public Date getExpirationDateFromToken(String token) {
        token = token.replace("Bearer ", "");

        return getClaimFromToken(token, Claims::getExpiration);
    }

    public AccountModel getAccountFromToken(String token) throws InvalidAccountIdException {
        token = token.replace("Bearer ", "");

        final String userId = getAccountIdFromToken(token); // gives user uuid
        return authService.loadAccountById(userId);
    }

    public boolean getAdminStatusFromToken(String token) throws InvalidAccountIdException {
        token = token.replace("Bearer ", "");

        return getAccountFromToken(token).getAdmin();
    }


    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        token = token.replace("Bearer ", "");

        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    //for retrieving any information from token we will need the secret key
    private Claims getAllClaimsFromToken(String token) {
        token = token.replace("Bearer ", "");

        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    //check if the token has expired
    private Boolean isTokenExpired(String token) {
        token = token.replace("Bearer ", "");

        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    //generate token for user
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority));
        return doGenerateToken(claims, userDetails.getUsername());
    }

    //while creating the token -
    //1. Define claims of the token, like Issuer, Expiration, Subject, and the ID
    //2. Sign the JWT using the HS512 algorithm and secret key.
    //3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
    //   compaction of the JWT to a URL-safe string
    private String doGenerateToken(Map<String, Object> claims, String subject) {

        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                   .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                   .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    //validate token
    public Boolean validateToken(String token, UserDetails userDetails) {
        token = token.replace("Bearer ", "");

        final String username = getAccountIdFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
