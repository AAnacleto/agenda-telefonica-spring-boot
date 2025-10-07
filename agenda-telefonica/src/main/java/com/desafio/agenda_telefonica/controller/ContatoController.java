package com.desafio.agenda_telefonica.controller;

import com.desafio.agenda_telefonica.dto.ResponseDTO;
import com.desafio.agenda_telefonica.model.Contato;
import com.desafio.agenda_telefonica.service.ContatoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contatos")
public class ContatoController {

    @Autowired
    private ContatoService service;

    // Criar contato
    @PostMapping
    public ResponseEntity<ResponseDTO<Contato>> salvar(@RequestBody Contato contato) {
        return criarResposta(() -> service.salvar(contato), "Contato cadastrado com sucesso!");
    }

    // Listar todos os contatos (retorna lista diretamente)
    @GetMapping
    public List<Contato> listar() {
        return service.listar();
    }

    // Total de contatos (retorna número diretamente)
    @GetMapping("/total")
    public long totalContatos() {
        return service.totalContatos();
    }

    @GetMapping("/totalAtivos")
    public long totalAtivos() {
        return service.totalContatosAtivos();
    }

    @GetMapping("/totalInativos")
    public long totalInativos() {
        return service.totalContatosInativos();
    }

    @GetMapping("/totalFavoritos")
    public ResponseEntity<Long> totalFavoritos() {
        long total = service.totalFavoritos();
        return ResponseEntity.ok(total);
    }

    // Contatos favoritos (retorna lista diretamente)
    @GetMapping("/favoritos")
    public List<Contato> listarFavoritos() {
        return service.listarFavoritos();
    }

    // Contatos favoritos (retorna lista diretamente)
    @GetMapping("/inativos")
    public List<Contato> listarInativos() {
        return service.listarInativos();
    }

    // Buscar contato por ID (retorna objeto diretamente)
    @GetMapping("/{id}")
    public Contato buscarPorID(@PathVariable Long id) {
        return service.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Contato não encontrado com ID: " + id));
    }

    // Atualizar contato
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<Contato>> atualizar(@PathVariable Long id, @RequestBody Contato contato) {
        return criarResposta(() -> service.atualizar(id, contato), "Contato atualizado com sucesso!");
    }

    // Inativar contato
    @PatchMapping("/{id}/inativar")
    public ResponseEntity<ResponseDTO<Void>> inativar(@PathVariable Long id) {
        return criarResposta(() -> {
            service.inativar(id);
            return null;
        }, "Contato inativado com sucesso!");
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<ResponseDTO<Void>> ativar(@PathVariable Long id) {
        return criarResposta(() -> {
            service.ativar(id);
            return null;
        }, "Contato ativado com sucesso!");
    }

    // Alternar favorito
   /* @PatchMapping("/{id}/favorito")
    public ResponseEntity<ResponseDTO<Contato>> toggleFavorito(@PathVariable Long id) {
        return criarResposta(() -> service.toggleFavorito(id), "Status de favorito atualizado com sucesso!");
    }*/

    @PatchMapping("/{id}/favoritar")
    public ResponseEntity<ResponseDTO<Contato>> favoritarContato(@PathVariable Long id) {
        return criarResposta(() -> service.favoritar(id), "Contato marcado como favorito com sucesso!");
    }

    @PatchMapping("/{id}/desfavoritar")
    public ResponseEntity<ResponseDTO<Contato>> desfavoritarContato(@PathVariable Long id) {
        return criarResposta(() -> service.desfavoritar(id), "Contato removido dos favoritos com sucesso!");
    }


    // Deletar contato
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<Void>> deletarContato(@PathVariable Long id) {
        return criarResposta(() -> {
            service.deletarPorId(id);
            return null;
        }, "Contato deletado com sucesso!");
    }

    //Método utilitário para padronizar respostas
    private <T> ResponseEntity<ResponseDTO<T>> criarResposta(ServiceCall<T> call, String mensagemSucesso) {
        try {
            T resultado = call.executar();
            ResponseDTO<T> response = new ResponseDTO<>("success", mensagemSucesso, resultado);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ResponseDTO<T> response = new ResponseDTO<>("error", e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            ResponseDTO<T> response = new ResponseDTO<>("error", "Erro inesperado: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @FunctionalInterface
    private interface ServiceCall<T> {
        T executar() throws Exception;
    }
}

