package com.desafio.agenda_telefonica.repository;

import com.desafio.agenda_telefonica.model.Contato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContatoRepository extends JpaRepository<Contato, Long> {
    // Buscar contato pelo celular (para validar unicidade)
    Optional<Contato> findByCelular(String celular);

    // Listar apenas contatos ativos
    List<Contato> findByAtivoTrue();

    List<Contato> findByAtivoFalse();

    // Listar apenas contatos ativos e favoritos
    List<Contato> findByAtivoTrueAndFavoritoTrue();

    // Contar contatos ativos
    long countByAtivoTrue();

    long countByAtivoFalse();

    long countByFavoritoTrueAndAtivoTrue();

}
