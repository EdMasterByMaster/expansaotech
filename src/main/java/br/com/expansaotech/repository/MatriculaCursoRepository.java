package br.com.expansaotech.repository;

import br.com.expansaotech.domain.MatriculaCurso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatriculaCursoRepository extends JpaRepository<MatriculaCurso, Long> {
    boolean existsByCurso_IdAndUsuario_Id(Integer cursoId, Integer usuarioId);
    long countByCurso_Id(Integer cursoId);
}
