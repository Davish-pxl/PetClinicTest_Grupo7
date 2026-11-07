package com.tecsup.petclinic.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Set;

/**
 * 
 * @author jgomezm
 *
 */
@NoArgsConstructor
@Entity(name = "specialties")
@Data
public class Specialty {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "name")
	private String name;

	@ManyToMany(mappedBy = "specialties", fetch = FetchType.LAZY)
	@ToString.Exclude
	//@EqualsAndHashCode.Exclude
	private Set<Vet> vets;
}