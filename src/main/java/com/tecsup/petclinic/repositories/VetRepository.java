package com.tecsup.petclinic.repositories;

import com.tecsup.petclinic.entities.Vet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de la entidad Vet
 */
@Repository
public interface VetRepository extends JpaRepository<Vet, Integer> {

	// Buscar veterinarios por apellido
	List<Vet> findByLastName(String lastName);

	// Buscar veterinarios activos / inactivos
	List<Vet> findByActive(Boolean active);

}
