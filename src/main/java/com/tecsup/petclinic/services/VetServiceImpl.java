package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.VetDTO;
import com.tecsup.petclinic.entities.Vet;
import com.tecsup.petclinic.exceptions.VetNotFoundException;
import com.tecsup.petclinic.mapper.VetMapper;
import com.tecsup.petclinic.repositories.VetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del servicio de Vet
 */
@Service
@Slf4j
public class VetServiceImpl implements VetService {

	private final VetRepository vetRepository;
	private final VetMapper vetMapper;

	public VetServiceImpl(VetRepository vetRepository, VetMapper vetMapper) {
		this.vetRepository = vetRepository;
		this.vetMapper = vetMapper;
	}

	@Override
	public VetDTO create(VetDTO vetDTO) {
		Vet vet = vetMapper.mapToEntity(vetDTO);
		if (vet.getActive() == null) {
			vet.setActive(true);   // por defecto un veterinario nuevo está activo
		}
		Vet newVet = vetRepository.save(vet);
		return vetMapper.mapToDto(newVet);
	}

	@Override
	public VetDTO update(VetDTO vetDTO) {
		Vet vet = vetRepository.save(vetMapper.mapToEntity(vetDTO));
		return vetMapper.mapToDto(vet);
	}

	@Override
	public void delete(Integer id) throws VetNotFoundException {
		Vet vet = findEntityById(id);
		vetRepository.delete(vet);
	}

	@Override
	public VetDTO findById(Integer id) throws VetNotFoundException {
		return vetMapper.mapToDto(findEntityById(id));
	}

	@Override
	public List<VetDTO> findByLastName(String lastName) {
		return vetMapper.mapToDtoList(vetRepository.findByLastName(lastName));
	}

	@Override
	public List<VetDTO> findByActive(Boolean active) {
		return vetMapper.mapToDtoList(vetRepository.findByActive(active));
	}

	@Override
	public List<VetDTO> findAll() {
		return vetMapper.mapToDtoList(vetRepository.findAll());
	}

	@Override
	public VetDTO deactivate(Integer id) throws VetNotFoundException {
		return changeActive(id, false);
	}

	@Override
	public VetDTO activate(Integer id) throws VetNotFoundException {
		return changeActive(id, true);
	}

	private VetDTO changeActive(Integer id, boolean active) throws VetNotFoundException {
		Vet vet = findEntityById(id);
		vet.setActive(active);
		return vetMapper.mapToDto(vetRepository.save(vet));
	}

	private Vet findEntityById(Integer id) throws VetNotFoundException {
		return vetRepository.findById(id)
				.orElseThrow(() -> new VetNotFoundException("Record not found...!"));
	}

}
