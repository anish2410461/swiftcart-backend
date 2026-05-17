package ecommerse.project.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    // 🔥 SKIP FILTER FOR PUBLIC APIS
   @Override
protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();
    String method = request.getMethod();

    // path.startsWith("/api/products") covers /api/products AND /api/products/123...
    return path.startsWith("/api/auth") || 
           (path.startsWith("/api/products") && method.equals("GET"));
}

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        System.out.println("🔍 JwtFilter running for: " + request.getMethod() + " " + request.getRequestURI());

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                String username = jwtUtil.extractUsername(token);
                String role     = jwtUtil.extractRole(token);

                System.out.println("✅ Token valid for user: " + username + " | role: " + role);

                if (role == null) role = "USER";

                GrantedAuthority authority =
                        new SimpleGrantedAuthority("ROLE_" + role);

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(username, null, List.of(authority));

                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (Exception e) {
                System.out.println("❌ JWT validation FAILED: " + e.getMessage());
            }
        } else {
            System.out.println("⚠️ No Authorization header found for: " + request.getRequestURI());
        }

        filterChain.doFilter(request, response);
    }
}