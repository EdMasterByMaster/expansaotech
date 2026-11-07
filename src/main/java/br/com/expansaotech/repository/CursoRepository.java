package br.com.expansaotech.repository;

import br.com.expansaotech.domain.Curso;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CursoRepository extends JpaRepository<Curso, Integer> {
    @EntityGraph(attributePaths = {"ong", "professor", "professor.usuario"})
    List<Curso> findAllByOrderByTituloAsc();

    @EntityGraph(attributePaths = {"ong", "professor", "professor.usuario"})
    @Override
    java.util.Optional<Curso> findById(Integer id);

    boolean existsByProfessor_Id(Integer professorId);
}
