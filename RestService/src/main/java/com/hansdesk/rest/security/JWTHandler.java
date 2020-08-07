package com.hansdesk.rest.security;

import com.hansdesk.rest.vo.UserVO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Key;
import java.util.*;

/**
 * 모든 method가 static인 singleton 클래스 객체이다.
 * 아래와 같이 JWT 관련 작업을 수행한다.
 * 1. 토큰을 생성
 * 2. 주어진 JWT 토큰을 가지고 응답 헤더에 Authorization 헤더를 추가한다.
 * 3. Authorization 헤더에서 JWT 토큰을 추출하여 Authentication 객체 생성
 */
class JWTHandler {

    private static final long EXPIRATIONTIME = 864_000_000; // 10 days
    private static final String SECRET = "8w4bk2zekfoG1kd5uv2nbaF4J19fJkeiuqkdivjwkqpKekJmvzZBe2I0Tbhgya8k"; // len=64, alphabet, number
    private static final String HEADER_NAME = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer";
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    static String generateJwtToken(Authentication authentication) {
        // UserService에서 인증할 때 생성한 Authentication의 principal에 userVO를 설정해 놓았다.
        UserVO userVO = (UserVO) authentication.getPrincipal();

        // JWT 토큰에 추가할 정보들을 추려서 claims에 저장한다.
        // 아래 정보말고 다른 정보는 필요할 때 DB에서 읽어온다고 가정하고 일단 아래 값들만 넣는다.
        // JWT의 payload는 다른 사용자가 열어볼 수 있기 때문에 민감한 정보는 넣지 않는게 좋다.
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", userVO.getUid().toString());
        claims.put("nickname", userVO.getNickname());

        // payload에 추가할 수 있게 권한들을 문자열로 변환.
        ArrayList<String> userRoles = new ArrayList<>();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority authority : authorities)
            userRoles.add(authority.toString());
        claims.put("authorities", userRoles);

        // 토큰을 생성해서 반환.
        return Jwts.builder()
                .setSubject(userVO.getEmail()) // JWT의 'sub'ject claim에 email 정보 저장
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME)) // 토큰 만료 시간 설정.
                .addClaims(claims) // 커스텀 정보 추가.
                .signWith(KEY, SignatureAlgorithm.HS512)
                .compact();
    }

    static void addJwtToResponseHeader(String jwt, HttpServletResponse res) {
        res.addHeader(HEADER_NAME, TOKEN_PREFIX + " " + jwt);
    }

    static Authentication getAuthenticationFromHeader(HttpServletRequest request) {
        String token = request.getHeader(HEADER_NAME);
        if (token != null) {
            Claims claims = Jwts.parser().setSigningKey(KEY).parseClaimsJws(token.replace(TOKEN_PREFIX, "")).getBody();

            String email = claims.getSubject();
            Long uid;
            try {
                uid = Long.valueOf((String) claims.get("uid"));
            } catch (Exception e) {
                uid = null;
            }
            String nickname = (String) claims.get("nickname");

            @SuppressWarnings("unchecked")
            Collection<String> userRoles = (Collection<String>) claims.get("authorities");
            ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
            for (String role : userRoles)
                authorities.add(new SimpleGrantedAuthority(role));

            UserVO userVO = new UserVO();
            userVO.setEmail(email);
            userVO.setUid(uid);
            userVO.setNickname(nickname);
            // UserVO의 나머지 값들은 필요할 때 DB에서 가져온다.

            return new UsernamePasswordAuthenticationToken(userVO, null, authorities);
        }

        return null;
    }
}
