package edu.pe.cibertec.infracciones.repository;

import edu.pe.cibertec.infracciones.model.Infractor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface InfractorRepository extends JpaRepository<Infractor, Long> {
    Optional<Infractor> findByDni(String dni);
    boolean existsByDni(String dni);
    boolean existsByEmail(String email);
    @Query("select count(m) from Multa m where m.infractor.id = :id and m.estado = 'VENCIDA'")
    int contarMultasVencidas(Long id);

}