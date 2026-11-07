package com.tecsup.petclinic.webs;

import com.tecsup.petclinic.dtos.VetDTO;
import com.tecsup.petclinic.exceptions.VetNotFoundException;
import com.tecsup.petclinic.services.VetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST de la entidad Vet
 */
@RestController
@Slf4j
public class VetController {

	private final VetService vetService;

	public VetController(VetService vetService) {
		this.vetService = vetService;
	}

	/**
	 * Lista todos los veterinarios. Opcionalmente filtra por estado: /vets?active=true
	 */
	@GetMapping(value = "/vets")
	public ResponseEntity<List<VetDTO>> findAllVets(@RequestParam(required = false) Boolean active) {
		List<VetDTO> vets = (active == null) ? vetService.findAll() : vetService.findByActive(active);
		return ResponseEntity.ok(vets);
	}

	@GetMapping(value = "/vets/{id}")
	public ResponseEntity<VetDTO> findById(@PathVariable Integer id) {
		try {
			return ResponseEntity.ok(vetService.findById(id));
		} catch (VetNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping(value = "/vets")
	public ResponseEntity<VetDTO> create(@RequestBody VetDTO vetDTO) {
		VetDTO newVet = vetService.create(vetDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(newVet);
	}

	@PutMapping(value = "/vets/{id}")
	public ResponseEntity<VetDTO> update(@RequestBody VetDTO vetDTO, @PathVariable Integer id) {
		try {
			VetDTO current = vetService.findById(id);
			current.setFirstName(vetDTO.getFirstName());
			current.setLastName(vetDTO.getLastName());
			current.setEmail(vetDTO.getEmail());
			current.setPhone(vetDTO.getPhone());
			if (vetDTO.getActive() != null) {
				current.setActive(vetDTO.getActive());
			}
			return ResponseEntity.ok(vetService.update(current));
		} catch (VetNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * Soft delete: marca al veterinario como inactivo
	 */
	@PutMapping(value = "/vets/{id}/deactivate")
	public ResponseEntity<VetDTO> deactivate(@PathVariable Integer id) {
		try {
			return ResponseEntity.ok(vetService.deactivate(id));
		} catch (VetNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * Reactiva a un veterinario inactivo
	 */
	@PutMapping(value = "/vets/{id}/activate")
	public ResponseEntity<VetDTO> activate(@PathVariable Integer id) {
		try {
			return ResponseEntity.ok(vetService.activate(id));
		} catch (VetNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping(value = "/vets/{id}")
	public ResponseEntity<String> delete(@PathVariable Integer id) {
		try {
			vetService.delete(id);
			return ResponseEntity.ok(" Delete ID :" + id);
		} catch (VetNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

}
