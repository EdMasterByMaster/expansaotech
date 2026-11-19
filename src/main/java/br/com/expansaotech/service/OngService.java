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
        validarCnpj(input.cnpj());
        Ong ong = new Ong();
        aplicar(ong, input);
        return ongRepository.save(ong);
    }

    @Transactional
    public Ong atualizar(Integer id, OngInput input) {
        validarCnpj(input.cnpj());
        Ong ong = buscar(id);
        aplicar(ong, input);
        return ongRepository.save(ong);
    }

    @Transactional
    public void excluir(Integer id) {
        ongRepository.delete(buscar(id));
    }

    private void aplicar(Ong ong, OngInput input) {
        ong.setCnpj(input.cnpj());
        ong.setNome(input.nomeFantasia() == null || input.nomeFantasia().isBlank()
                ? input.nome() : input.nomeFantasia());
        ong.setRazaoSocial(input.razaoSocial());
        ong.setNomeFantasia(input.nomeFantasia());
        ong.setEmail(input.email());
        ong.setTelefone(input.telefone());
        ong.setCep(input.cep());
        ong.setLogradouro(input.logradouro());
        ong.setNumero(input.numero());
        ong.setComplemento(input.complemento());
        ong.setFotoUrl(input.fotoUrl());
    }

    private void validarCnpj(String value) {
        String cnpj = value == null ? "" : value.replaceAll("\\D", "");
        if (cnpj.length() != 14 || cnpj.chars().distinct().count() == 1) {
            throw new IllegalArgumentException("Informe um CNPJ válido com 14 dígitos.");
        }
        int[] weightsFirst = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] weightsSecond = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int first = cnpjDigit(cnpj, weightsFirst, 12);
        int second = cnpjDigit(cnpj, weightsSecond, 13);
        if (cnpj.charAt(12) - '0' != first || cnpj.charAt(13) - '0' != second) {
            throw new IllegalArgumentException("Informe um CNPJ válido com dígitos verificadores corretos.");
        }
    }

    private int cnpjDigit(String cnpj, int[] weights, int length) {
        int sum = 0;
        for (int i = 0; i < length; i++) {
            sum += (cnpj.charAt(i) - '0') * weights[i];
        }
        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }
}
