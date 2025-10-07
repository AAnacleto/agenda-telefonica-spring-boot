package com.desafio.agenda_telefonica.controller;

import com.desafio.agenda_telefonica.dto.ResponseDTO;
import com.desafio.agenda_telefonica.model.Contato;
import com.desafio.agenda_telefonica.service.ContatoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ContatoController.class)
class ContatoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContatoService service;

    @Autowired
    private ObjectMapper objectMapper;

    private Contato contato;

    @BeforeEach
    void setUp() {
        contato = new Contato();
        contato.setId(1L);
        contato.setNome("Allysson");
        contato.setCelular("81999999999");
        contato.setTelefone("8133333333");
        contato.setEmail("allysson@email.com");
        contato.setAtivo(true);
        contato.setFavorito(false);
    }

    // =============================
    // POST /api/contatos
    // =============================

    @Test
    void deveSalvarContatoComSucesso() throws Exception {
        when(service.salvar(any(Contato.class))).thenReturn(contato);

        mockMvc.perform(post("/api/contatos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contato)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.mensagem").value("Contato cadastrado com sucesso!"));


        verify(service).salvar(any(Contato.class));
    }

    @Test
    void deveRetornarErroAoSalvarComCelularInvalido() throws Exception {
        when(service.salvar(any())).thenThrow(new IllegalArgumentException("Celular não pode estar vazio"));

        mockMvc.perform(post("/api/contatos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contato)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.mensagem").value("Celular não pode estar vazio"));
    }

    // =============================
    // GET /api/contatos
    // =============================

    @Test
    void deveListarContatos() throws Exception {
        when(service.listar()).thenReturn(List.of(contato));

        mockMvc.perform(get("/api/contatos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Allysson"));
    }

    // =============================
    // GET /api/contatos/{id}
    // =============================

    @Test
    void deveBuscarContatoPorId() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(Optional.of(contato));

        mockMvc.perform(get("/api/contatos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.mensagem").value("Contato encontrado com sucesso!"));

    }

    @Test
    void deveRetornarErroSeContatoNaoEncontrado() throws Exception {
        // Simula que o service retorna Optional.empty()
        when(service.buscarPorId(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/contatos/1"))
                .andExpect(status().isBadRequest()) // o controller captura IllegalArgumentException e devolve 400
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.mensagem").value("Contato não encontrado com ID: 1"));
    }

    // =============================
    // PUT /api/contatos/{id}
    // =============================

    @Test
    void deveAtualizarContato() throws Exception {
        contato.setNome("Atualizado");
        when(service.atualizar(eq(1L), any())).thenReturn(contato);

        mockMvc.perform(put("/api/contatos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contato)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.mensagem").value("Contato atualizado com sucesso!"));

    }

    @Test
    void deveRetornarErroAoAtualizarContatoInexistente() throws Exception {
        when(service.atualizar(eq(99L), any())).thenThrow(new IllegalArgumentException("Contato não encontrado"));

        mockMvc.perform(put("/api/contatos/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contato)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.mensagem").value("Contato não encontrado"));
    }

    // =============================
    // PATCH /api/contatos/{id}/inativar
    // =============================

    @Test
    void deveInativarContato() throws Exception {
        doNothing().when(service).inativar(1L);

        mockMvc.perform(patch("/api/contatos/1/inativar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value("Contato inativado com sucesso!"));
    }

    // =============================
    // PATCH /api/contatos/{id}/ativar
    // =============================

    @Test
    void deveAtivarContato() throws Exception {
        doNothing().when(service).ativar(1L);

        mockMvc.perform(patch("/api/contatos/1/ativar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value("Contato ativado com sucesso!"));
    }

    // =============================
    // PATCH /api/contatos/{id}/favoritar
    // =============================

    @Test
    void deveFavoritarContato() throws Exception {
        contato.setFavorito(true);
        when(service.favoritar(1L)).thenReturn(contato);

        mockMvc.perform(patch("/api/contatos/1/favoritar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.mensagem").value("Contato marcado como favorito com sucesso!"));
    }

    // =============================
    // PATCH /api/contatos/{id}/desfavoritar
    // =============================

    @Test
    void deveDesfavoritarContato() throws Exception {
        contato.setFavorito(false);
        when(service.desfavoritar(1L)).thenReturn(contato);

        mockMvc.perform(patch("/api/contatos/1/desfavoritar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value("Contato removido dos favoritos com sucesso!"));

    }

    // =============================
    // DELETE /api/contatos/{id}
    // =============================

    @Test
    void deveDeletarContato() throws Exception {
        doNothing().when(service).deletarPorId(1L);

        mockMvc.perform(delete("/api/contatos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value("Contato deletado com sucesso!"));

        verify(service).deletarPorId(1L);
    }

    // =============================
    // GET /api/contatos/total e outros contadores
    // =============================

    @Test
    void deveRetornarTotalDeContatos() throws Exception {
        when(service.totalContatos()).thenReturn(10L);

        mockMvc.perform(get("/api/contatos/total"))
                .andExpect(status().isOk())
                .andExpect(content().string("10"));
    }

    @Test
    void deveRetornarTotalDeAtivos() throws Exception {
        when(service.totalContatosAtivos()).thenReturn(8L);

        mockMvc.perform(get("/api/contatos/totalAtivos"))
                .andExpect(status().isOk())
                .andExpect(content().string("8"));
    }

    @Test
    void deveRetornarTotalDeInativos() throws Exception {
        when(service.totalContatosInativos()).thenReturn(2L);

        mockMvc.perform(get("/api/contatos/totalInativos"))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }

    @Test
    void deveRetornarTotalDeFavoritos() throws Exception {
        when(service.totalFavoritos()).thenReturn(3L);

        mockMvc.perform(get("/api/contatos/totalFavoritos"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    // =============================
    // GET /api/contatos/favoritos e /inativos
    // =============================

    @Test
    void deveListarFavoritos() throws Exception {
        when(service.listarFavoritos()).thenReturn(List.of(contato));

        mockMvc.perform(get("/api/contatos/favoritos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Allysson"));
    }

    @Test
    void deveListarInativos() throws Exception {
        when(service.listarInativos()).thenReturn(List.of(contato));

        mockMvc.perform(get("/api/contatos/inativos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Allysson"));
    }
}
