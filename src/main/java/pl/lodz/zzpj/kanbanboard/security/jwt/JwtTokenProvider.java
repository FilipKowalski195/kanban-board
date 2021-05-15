package pl.lodz.zzpj.kanbanboard.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import pl.lodz.zzpj.kanbanboard.security.user.UserDetailsImpl;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private static final SignatureAlgorithm ALGORITHM = SignatureAlgorithm.HS512;
    private static final Key SECRET_KEY = Keys.secretKeyFor(ALGORITHM);

    @Value("${security.jwt.token.expire-length:1800000}")
    private long validityInMilliseconds = 1800000; // 30m

    public String createToken(Authentication authentication) {

        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + validityInMilliseconds))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().getSubject();
    }


    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(authToken);
            return true;
        } catch (Exception e) {
        }

        return false;
    }

}
