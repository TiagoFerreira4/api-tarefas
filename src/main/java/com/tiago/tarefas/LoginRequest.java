package com.tiago.tarefas;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String senha;
}
