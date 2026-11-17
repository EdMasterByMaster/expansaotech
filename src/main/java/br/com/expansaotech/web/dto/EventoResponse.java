package br.com.expansaotech.web.dto;

import br.com.expansaotech.domain.Evento;
import java.time.LocalDateTime;

public record EventoResponse(
        Integer id,
        Integer ongId,
        String ongNome,
        String titulo,
        String descricao,
        LocalDateTime dataHora,
        String localEvento,
        String fotoUrl,
        LocalDateTime dataCriacao) {

    public static EventoResponse from(Evento evento) {
        return new EventoResponse(evento.getId(), evento.getOng().getId(), evento.getOng().getNome(),
                evento.getTitulo(), evento.getDescricao(), evento.getDataHora(), evento.getLocalEvento(),
                evento.getFotoUrl(), evento.getDataCriacao());
    }
}
