package br.com.convoquei.backend.core.user.services;

import br.com.convoquei.backend.app.security.jwt.service.JwtService;
import br.com.convoquei.backend.core.user.api.dto.response.TokenResponse;
import br.com.convoquei.backend.core.user.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;
    private final GenerateTokenUserService generateTokenUserService;

    public RefreshTokenService(JwtService jwtService,
                               UserRepository userRepository,
                               UserDetailsService userDetailsService,
                               GenerateTokenUserService generateTokenUserService) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
        this.generateTokenUserService = generateTokenUserService;
    }

    public TokenResponse refresh(String refreshToken) {
        if (!jwtService.isValidRefreshToken(refreshToken)) {
            throw new BadCredentialsException("Refresh token inválido, expirado ou não é do tipo refresh.");
        }

        var decoded = jwtService.decodeRefreshToken(refreshToken);
        String email = decoded.getSubject();

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Usuário não encontrado."));

        if (!user.isActive()) {
            throw new BadCredentialsException("Usuário inativo.");
        }

        var userDetails = userDetailsService.loadUserByUsername(email);
        return generateTokenUserService.generate(userDetails);
    }
}


