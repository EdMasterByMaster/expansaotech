package br.com.expansaotech.repository;

import br.com.expansaotech.domain.InscricaoEvento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InscricaoEventoRepository extends JpaRepository<InscricaoEvento, Long> {
    boolean existsByEvento_IdAndUsuario_Id(Integer eventoId, Integer usuarioId);
    long countByEvento_Id(Integer eventoId);
}
