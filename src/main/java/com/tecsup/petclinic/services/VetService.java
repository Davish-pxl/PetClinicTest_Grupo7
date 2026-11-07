package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.VetDTO;
import com.tecsup.petclinic.exceptions.VetNotFoundException;

import java.util.List;

/**
 * Servicio de la entidad Vet
 */
public interface VetService {

	VetDTO create(VetDTO vetDTO);

	VetDTO update(VetDTO vetDTO);

	void delete(Integer id) throws VetNotFoundException;

	VetDTO findById(Integer id) throws VetNotFoundException;

	List<VetDTO> findByLastName(String lastName);

	List<VetDTO> findByActive(Boolean active);

	List<VetDTO> findAll();

	VetDTO deactivate(Integer id) throws VetNotFoundException;

	VetDTO activate(Integer id) throws VetNotFoundException;

}
