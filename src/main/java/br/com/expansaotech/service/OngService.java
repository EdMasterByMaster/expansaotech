package br.com.expansaotech.service;

import br.com.expansaotech.domain.Ong;
import br.com.expansaotech.repository.OngRepository;
import br.com.expansaotech.web.dto.OngInput;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OngService {
    private final OngRepository ongRepository;

    public OngService(OngRepository ongRepository) {
        this.ongRepository = ongRepository;
    }

    @Transactional(readOnly = true)
    public List<Ong> listar() {
        return ongRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Ong buscar(Integer id) {
        return ongRepository.findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoException("ONG não encontrada."));
    }

    @Transactional
    public Ong salvar(OngInput input) {
        Ong ong = new Ong();
        ong.setCnpj(input.cnpj());
        ong.setNome(input.nome());
        ong.setEmail(input.email());
        ong.setTelefone(input.telefone());
        ong.setFotoUrl(input.fotoUrl());
        return ongRepository.save(ong);
    }

    @Transactional
    public Ong atualizar(Integer id, OngInput input) {
        Ong ong = buscar(id);
        ong.setCnpj(input.cnpj());
        ong.setNome(input.nome());
        ong.setEmail(input.email());
        ong.setTelefone(input.telefone());
        ong.setFotoUrl(input.fotoUrl());
        return ongRepository.save(ong);
    }

    @Transactional
    public void excluir(Integer id) {
        ongRepository.delete(buscar(id));
    }
}
