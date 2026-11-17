package br.com.expansaotech.service;

import br.com.expansaotech.domain.Evento;
import br.com.expansaotech.repository.EventoRepository;
import br.com.expansaotech.repository.OngRepository;
import br.com.expansaotech.web.dto.EventoInput;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventoService {
    private final EventoRepository eventoRepository;
    private final OngRepository ongRepository;

    public EventoService(EventoRepository eventoRepository, OngRepository ongRepository) {
        this.eventoRepository = eventoRepository;
        this.ongRepository = ongRepository;
    }

    @Transactional(readOnly = true)
    public List<Evento> listar() {
        return eventoRepository.findAllByOrderByDataHoraAsc();
    }

    @Transactional(readOnly = true)
    public Evento buscar(Integer id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Evento não encontrado."));
    }

    @Transactional
    public Evento criar(EventoInput input) {
        Evento evento = eventoRepository.save(aplicar(new Evento(), input));
        return buscar(evento.getId());
    }

    @Transactional
    public Evento atualizar(Integer id, EventoInput input) {
        Evento evento = eventoRepository.save(aplicar(buscar(id), input));
        return buscar(evento.getId());
    }

    @Transactional
    public void excluir(Integer id) {
        eventoRepository.delete(buscar(id));
    }

    private Evento aplicar(Evento evento, EventoInput input) {
        evento.setOng(ongRepository.findById(input.ongId())
                .orElseThrow(() -> new RegistroNaoEncontradoException("ONG não encontrada.")));
        evento.setTitulo(input.titulo());
        evento.setDescricao(input.descricao());
        evento.setDataHora(input.dataHora());
        evento.setLocalEvento(input.localEvento());
        evento.setFotoUrl(input.fotoUrl());
        return evento;
    }
}
