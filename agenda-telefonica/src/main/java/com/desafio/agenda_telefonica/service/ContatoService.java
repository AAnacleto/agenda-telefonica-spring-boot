package com.desafio.agenda_telefonica.service;

import com.desafio.agenda_telefonica.model.Contato;
import com.desafio.agenda_telefonica.repository.ContatoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContatoService {

    @Autowired
    private ContatoRepository repository;

    // =============================
    // CRUD BÁSICO
    // =============================

    // SALVAR
   /* public Contato salvar(Contato contato) {
        // Validar celular
        if(contato.getCelular() == null || contato.getCelular().isBlank()) {
            throw new IllegalArgumentException("Celular não pode estar vazio");
        }
        validarCelularUnico(contato.getCelular(), null);

        // transforma telefone vazio em null
        if(contato.getTelefone() != null && contato.getTelefone().isBlank()) {
            contato.setTelefone(null);
        }

        // Novo contato sempre ativo
        contato.setAtivo(true);

        try {
            return repository.save(contato);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Celular já cadastrado!");
        }
    }*/

    public Contato salvar(Contato contato) {
        // 1. Validar celular não vazio
        if(contato.getCelular() == null || contato.getCelular().isBlank()) {
            throw new IllegalArgumentException("Celular não pode estar vazio");
        }

        // 2. CORREÇÃO: Validar celular único, ignorando o ID se for uma atualização.
        // Se 'contato.getId()' for nulo (novo), o 'validarCelularUnico' só verificará se
        // o celular já existe em qualquer outro registro. Se não for nulo (atualização),
        // ele ignorará o registro com esse ID durante a verificação.
        validarCelularUnico(contato.getCelular(), contato.getId());

        // 3. Transforma telefone vazio em null
        if(contato.getTelefone() != null && contato.getTelefone().isBlank()) {
            contato.setTelefone(null);
        }

        // 4. Melhoria Lógica: Define 'ativo' como true APENAS se for um NOVO contato.
        // Isso evita reativar um contato que foi inativado durante uma atualização.
        if (contato.getId() == null) {
            contato.setAtivo(true);
        }
        // Se for uma atualização, o status 'ativo' virá do objeto 'contato' (ou do que
        // foi carregado previamente e modificado), respeitando a intenção da requisição.

        // 5. Tenta salvar
        try {
            return repository.save(contato);
        } catch (DataIntegrityViolationException e) {
            // Esta exceção de DB é uma segurança caso a validação em 2. falhe
            // ou não exista no repositório (Constraint UNIQUE do banco).
            throw new IllegalArgumentException("Celular já cadastrado!");
        }
    }

    // Listar todos contatos ativos
    public List<Contato> listar() {
        return repository.findByAtivoTrue();
    }

    // Buscar contato por ID (ativo)
    public Optional<Contato> buscarPorId(Long id) {
        return repository.findById(id);
    }

    // Atualizar contato
    public Contato atualizar(Long id, Contato contato) {
        Contato existente = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Contato não encontrado com ID: " + id));

        validarCelularUnico(contato.getCelular(), id);

        existente.setNome(contato.getNome());
        existente.setTelefone(contato.getTelefone());
        existente.setCelular(contato.getCelular());
        existente.setEmail(contato.getEmail());
        existente.setFavorito(contato.isFavorito());
        existente.setAtivo(contato.isAtivo());

        return repository.save(existente);
    }

    // Deletar contato
    public void deletarPorId(Long id) {
        Contato contato = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Contato não encontrado com ID: " + id));
        repository.delete(contato);
    }

    // =============================
    // FUNCIONALIDADES ESPECIAIS
    // =============================

    // Inativar contato
    public void inativar(Long id) {
        Contato contato = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Contato não encontrado com ID: " + id));
        contato.setAtivo(false);
        repository.save(contato);
    }

    public void ativar(Long id) {
        Contato contato = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Contato não encontrado com ID: " + id));
        contato.setAtivo(true);
        repository.save(contato);
    }

    // Alternar favorito
    public Contato toggleFavorito(Long id) {
        Contato contato = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contato não encontrado"));
        contato.setFavorito(!contato.isFavorito());
        return repository.save(contato);
    }

    // Favoritar um contato
    public Contato favoritar(Long id) {
        Contato contato = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contato não encontrado"));
        contato.setFavorito(true);
        return repository.save(contato);
    }

    // Desfavoritar um contato
    public Contato desfavoritar(Long id) {
        Contato contato = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contato não encontrado"));
        contato.setFavorito(false);
        return repository.save(contato);
    }

    // Listar contatos favoritos ativos
    public List<Contato> listarFavoritos() {
        return repository.findByFavoritoTrue();
    }

    public List<Contato> listarInativos(){
        return repository.findByAtivoFalse();
    }

    // =============================
    // UTILITÁRIOS
    // =============================

    // Contar total de contatos ativos
    public long totalContatos() {
        return repository.count();
    }

    public long totalContatosAtivos() {
        return repository.countByAtivoTrue();
    }

    public long totalContatosInativos() {
        return repository.countByAtivoFalse();
    }

    public long totalFavoritos(){
        return repository.countByFavoritoTrue();
    }

    // =============================
    // VALIDAÇÕES
    // =============================

    private void validarCelularUnico(String celular, Long contatoId) {
        Optional<Contato> contatoExistente = repository.findByCelular(celular);

        // Se encontrou algum contato com esse celular
        if (contatoExistente.isPresent()) {
            // Se é criação (contatoId == null) ou se é atualização de outro contato
            if (contatoId == null || !contatoExistente.get().getId().equals(contatoId)) {
                throw new IllegalArgumentException("Celular já cadastrado!");
            }
        }
    }
}
