package com.tiago.tarefas;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TarefaRequest {
    private String titulo;
    private String descricao;
    private boolean concluida;
}
