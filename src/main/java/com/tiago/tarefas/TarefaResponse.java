package com.tiago.tarefas;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TarefaResponse {
    private Long id;
    private String titulo;
    private String descricao;
    private boolean concluida;
}
