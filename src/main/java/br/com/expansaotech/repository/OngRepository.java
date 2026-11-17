package br.com.expansaotech.repository;

import br.com.expansaotech.domain.Ong;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OngRepository extends JpaRepository<Ong, Integer> {
}
