package br.com.expansaotech.repository;

import br.com.expansaotech.domain.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;
import java.util.Optional;

public interface ProfessorRepository extends JpaRepository<Professor, Integer> {
    @Override
    @EntityGraph(attributePaths = "usuario")
    List<Professor> findAll();

    @EntityGraph(attributePaths = "usuario")
    @Override
    Optional<Professor> findById(Integer id);
}
