package br.com.expansaotech.repository;

import br.com.expansaotech.domain.Evento;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventoRepository extends JpaRepository<Evento, Integer> {
    @EntityGraph(attributePaths = "ong")
    List<Evento> findAllByOrderByDataHoraAsc();

    @EntityGraph(attributePaths = "ong")
    @Override
    Optional<Evento> findById(Integer id);
}
