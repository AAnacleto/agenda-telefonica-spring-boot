package com.desafio.agenda_telefonica.service;

import com.desafio.agenda_telefonica.model.Contato;
import com.desafio.agenda_telefonica.repository.ContatoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ContatoServiceTest {

    @Mock
    private ContatoRepository repository;

    @InjectMocks
    private ContatoService service;

    private Contato contato;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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
    // TESTES: SALVAR
    // =============================

    @Test
    void deveSalvarContatoComSucesso() {
        when(repository.findByCelular(anyString())).thenReturn(Optional.empty());
        when(repository.save(any(Contato.class))).thenReturn(contato);

        Contato salvo = service.salvar(contato);

        assertNotNull(salvo);
        assertEquals("Allysson", salvo.getNome());
        verify(repository).save(any(Contato.class));
    }

    @Test
    void deveLancarExcecaoSeCelularVazio() {
        contato.setCelular("");
        assertThrows(IllegalArgumentException.class, () -> service.salvar(contato));
        verify(repository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoSeCelularDuplicado() {
        when(repository.findByCelular("81999999999")).thenReturn(Optional.of(contato));

        Contato novo = new Contato();
        novo.setCelular("81999999999");

        assertThrows(IllegalArgumentException.class, () -> service.salvar(novo));
    }

    @Test
    void deveLancarExcecaoDeIntegridadeAoSalvar() {
        when(repository.findByCelular(anyString())).thenReturn(Optional.empty());
        when(repository.save(any())).thenThrow(DataIntegrityViolationException.class);

        assertThrows(IllegalArgumentException.class, () -> service.salvar(contato));
    }

    // =============================
    // TESTES: ATUALIZAR
    // =============================

    @Test
    void deveAtualizarContatoComSucesso() {
        when(repository.findById(1L)).thenReturn(Optional.of(contato));
        when(repository.findByCelular("81988888888")).thenReturn(Optional.empty());
        when(repository.save(any())).thenReturn(contato);

        Contato atualizado = new Contato();
        atualizado.setNome("Allysson Atualizado");
        atualizado.setCelular("81988888888");

        Contato result = service.atualizar(1L, atualizado);

        assertEquals("Allysson Atualizado", result.getNome());
        verify(repository).save(any());
    }

    @Test
    void deveLancarExcecaoAoAtualizarContatoInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.atualizar(99L, contato));
    }

    // =============================
    // TESTES: DELETAR
    // =============================

    @Test
    void deveDeletarContatoComSucesso() {
        when(repository.findById(1L)).thenReturn(Optional.of(contato));
        doNothing().when(repository).delete(contato);

        service.deletarPorId(1L);

        verify(repository).delete(contato);
    }

    @Test
    void deveLancarExcecaoAoDeletarInexistente() {
        when(repository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.deletarPorId(2L));
    }

    // =============================
    // TESTES: ATIVAR / INATIVAR
    // =============================

    @Test
    void deveInativarContato() {
        when(repository.findById(1L)).thenReturn(Optional.of(contato));
        service.inativar(1L);
        assertFalse(contato.isAtivo());
        verify(repository).save(contato);
    }

    @Test
    void deveAtivarContato() {
        contato.setAtivo(false);
        when(repository.findById(1L)).thenReturn(Optional.of(contato));
        service.ativar(1L);
        assertTrue(contato.isAtivo());
        verify(repository).save(contato);
    }

    @Test
    void deveLancarExcecaoAoInativarContatoInexistente() {
        when(repository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.inativar(10L));
    }

    // =============================
    // TESTES: FAVORITAR / DESFAVORITAR
    // =============================

    @Test
    void deveFavoritarContato() {
        when(repository.findById(1L)).thenReturn(Optional.of(contato));
        when(repository.save(any())).thenReturn(contato);

        Contato result = service.favoritar(1L);

        assertTrue(result.isFavorito());
        verify(repository).save(contato);
    }

    @Test
    void deveDesfavoritarContato() {
        contato.setFavorito(true);
        when(repository.findById(1L)).thenReturn(Optional.of(contato));
        when(repository.save(any())).thenReturn(contato);

        Contato result = service.desfavoritar(1L);

        assertFalse(result.isFavorito());
        verify(repository).save(contato);
    }

    // =============================
    // TESTES: LISTAGENS / CONTAGENS
    // =============================

    @Test
    void deveListarContatosAtivos() {
        when(repository.findByAtivoTrue()).thenReturn(List.of(contato));

        List<Contato> result = service.listar();

        assertEquals(1, result.size());
        verify(repository).findByAtivoTrue();
    }

    @Test
    void deveListarFavoritos() {
        when(repository.findByAtivoTrueAndFavoritoTrue()).thenReturn(List.of(contato));
        List<Contato> result = service.listarFavoritos();
        assertEquals(1, result.size());
    }

    @Test
    void deveListarInativos() {
        when(repository.findByAtivoFalse()).thenReturn(List.of(contato));
        List<Contato> result = service.listarInativos();
        assertEquals(1, result.size());
    }

    @Test
    void deveContarTotais() {
        when(repository.count()).thenReturn(10L);
        when(repository.countByAtivoTrue()).thenReturn(7L);
        when(repository.countByAtivoFalse()).thenReturn(3L);
        when(repository.countByFavoritoTrueAndAtivoTrue()).thenReturn(2L);

        assertEquals(10L, service.totalContatos());
        assertEquals(7L, service.totalContatosAtivos());
        assertEquals(3L, service.totalContatosInativos());
        assertEquals(2L, service.totalFavoritos());
    }
}
