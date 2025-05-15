package com.tiago.tarefas;

import com.tiago.tarefas.security.JwtUtil;
import com.tiago.tarefas.security.LoginResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void registrar(RegisterRequest request) {
        Usuario usuario = Usuario.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senha(passwordEncoder.encode(request.getSenha()))
                .build();

        usuarioRepository.save(usuario);
    }

    public LoginResponse login(LoginRequest request) {
        try {
            System.out.println("Tentando login com: " + request.getEmail());

            Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

            System.out.println("Usuário encontrado: " + usuario.getEmail());
            System.out.println("Senha fornecida: " + request.getSenha());
            System.out.println("Senha armazenada (criptografada): " + usuario.getSenha());

            if (!passwordEncoder.matches(request.getSenha(), usuario.getSenha())) {
                System.out.println("Senha inválida");
                throw new BadCredentialsException("Credenciais inválidas");
            }

            String token = jwtUtil.generateToken(usuario.getEmail());
            System.out.println("Token gerado com sucesso");
            return new LoginResponse(token);
        } catch (Exception e) {
            e.printStackTrace(); // Exibe stack trace no console
            throw new RuntimeException("Erro ao autenticar usuário: " + e.getMessage());
        }
}

}
