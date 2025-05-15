package com.tiago.tarefas;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tarefas")
@RequiredArgsConstructor
public class TarefaController {

    private final TarefaRepository tarefaRepository;
    private final UsuarioRepository usuarioRepository;

    @PostMapping
    public ResponseEntity<String> criarTarefa(@RequestBody TarefaRequest request,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Tarefa tarefa = Tarefa.builder()
                .titulo(request.getTitulo())
                .descricao(request.getDescricao())
                .concluida(request.isConcluida())
                .usuario(usuario)
                .build();

        tarefaRepository.save(tarefa);
        return ResponseEntity.ok("Tarefa criada com sucesso!");
    }

    @GetMapping
    public ResponseEntity<List<TarefaResponse>> listarTarefas(@AuthenticationPrincipal UserDetails userDetails) {
        List<Tarefa> tarefas = tarefaRepository.findByUsuarioEmail(userDetails.getUsername());

        List<TarefaResponse> response = tarefas.stream()
                .map(t -> new TarefaResponse(t.getId(), t.getTitulo(), t.getDescricao(), t.isConcluida()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> atualizarTarefa(@PathVariable Long id,
                                                  @RequestBody TarefaRequest request,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));

        if (!tarefa.getUsuario().getEmail().equals(userDetails.getUsername())) {
            return ResponseEntity.status(403).body("Você não pode editar essa tarefa.");
        }

        tarefa.setTitulo(request.getTitulo());
        tarefa.setDescricao(request.getDescricao());
        tarefa.setConcluida(request.isConcluida());
        tarefaRepository.save(tarefa);

        return ResponseEntity.ok("Tarefa atualizada com sucesso!");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletarTarefa(@PathVariable Long id,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));

        if (!tarefa.getUsuario().getEmail().equals(userDetails.getUsername())) {
            return ResponseEntity.status(403).body("Você não pode deletar essa tarefa.");
        }

        tarefaRepository.delete(tarefa);
        return ResponseEntity.ok("Tarefa deletada com sucesso!");
    }
}
