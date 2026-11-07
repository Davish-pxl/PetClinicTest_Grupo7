package com.tecsup.petclinic.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de la entidad Vet (veterinario)
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class VetDTO {

	private Integer id;

	private String firstName;

	private String lastName;

	private String email;

	private String phone;

	private Boolean active;

}
