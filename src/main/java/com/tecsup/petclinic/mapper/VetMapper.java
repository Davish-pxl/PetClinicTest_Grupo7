package com.tecsup.petclinic.mapper;

import com.tecsup.petclinic.dtos.VetDTO;
import com.tecsup.petclinic.entities.Vet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper entre Vet y VetDTO
 */
@Mapper(componentModel = "spring")
public interface VetMapper {

	@Mapping(target = "specialties", ignore = true)
	Vet mapToEntity(VetDTO vetDTO);

	VetDTO mapToDto(Vet vet);

	List<VetDTO> mapToDtoList(List<Vet> vetList);

}
