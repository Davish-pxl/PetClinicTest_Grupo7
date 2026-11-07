package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.tecsup.petclinic.dtos.VetDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración de VetController.
 * La petición recorre el stack real: controlador -> servicio -> repositorio -> H2
 * usando los datos iniciales de schema.sql / data.sql.
 */
@AutoConfigureMockMvc
@SpringBootTest
@Slf4j
public class VetControllerTest {

	private static final ObjectMapper om = new ObjectMapper();

	@Autowired
	private MockMvc mockMvc;

	// ===================== LISTADO Y CONSULTA =====================

	@Test
	public void testFindAllVets() throws Exception {

		final int ID_FIRST_RECORD = 1;

		this.mockMvc.perform(get("/vets"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$[0].id", is(ID_FIRST_RECORD)));
	}

	@Test
	public void testFindVetOK() throws Exception {

		this.mockMvc.perform(get("/vets/1"))   // Vet 1 = James Carter
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.firstName", is("James")))
				.andExpect(jsonPath("$.lastName", is("Carter")))
				.andExpect(jsonPath("$.email", is("james.carter@petclinic.com")))
				.andExpect(jsonPath("$.active", is(true)));
	}

	@Test
	public void testFindVetKO() throws Exception {

		mockMvc.perform(get("/vets/666"))
				.andExpect(status().isNotFound());
	}

	// ===================== CREACIÓN Y SOFT DELETE =====================

	@Test
	public void testCreateVet() throws Exception {

		String FIRST_NAME = "Gregory";
		String LAST_NAME = "House";
		String EMAIL = "gregory.house@petclinic.com";
		String PHONE = "6085559999";

		VetDTO newVet = VetDTO.builder()
				.firstName(FIRST_NAME)
				.lastName(LAST_NAME)
				.email(EMAIL)
				.phone(PHONE)
				.build();

		this.mockMvc.perform(post("/vets")
						.content(om.writeValueAsString(newVet))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.firstName", is(FIRST_NAME)))
				.andExpect(jsonPath("$.lastName", is(LAST_NAME)))
				.andExpect(jsonPath("$.email", is(EMAIL)))
				.andExpect(jsonPath("$.phone", is(PHONE)))
				.andExpect(jsonPath("$.active", is(true)));
	}

	@Test
	public void testDeactivateVet() throws Exception {

		Integer id = createVet("Vet", "ToDeactivate");

		mockMvc.perform(put("/vets/" + id + "/deactivate"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(id)))
				.andExpect(jsonPath("$.active", is(false)));

		// Se confirma en la BD
		mockMvc.perform(get("/vets/" + id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.active", is(false)));
	}

	@Test
	public void testReactivateVet() throws Exception {

		final int INACTIVE_VET_ID = 6;   // Sharon Jenkins (active = false)

		mockMvc.perform(put("/vets/" + INACTIVE_VET_ID + "/activate"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(INACTIVE_VET_ID)))
				.andExpect(jsonPath("$.active", is(true)));

		// Se restaura el dato semilla para no afectar a otras pruebas
		mockMvc.perform(put("/vets/" + INACTIVE_VET_ID + "/deactivate"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.active", is(false)));
	}

	// ===================== FILTROS, ACTUALIZACIÓN Y BORRADO =====================

	@Test
	public void testFindActiveVets() throws Exception {

		mockMvc.perform(get("/vets").param("active", "true"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[*].active", everyItem(is(true))))
				.andExpect(jsonPath("$[*].id", hasItem(1)));
	}

	@Test
	public void testFindInactiveVets() throws Exception {

		mockMvc.perform(get("/vets").param("active", "false"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[*].active", everyItem(is(false))))
				.andExpect(jsonPath("$[*].id", hasItem(6)));
	}

	@Test
	public void testUpdateVet() throws Exception {

		String UP_FIRST_NAME = "Meredith";
		String UP_LAST_NAME = "Grey";
		String UP_EMAIL = "meredith.grey@petclinic.com";

		// CREATE
		Integer id = createVet("Vet", "ToUpdate");

		// UPDATE
		VetDTO upVet = VetDTO.builder()
				.id(id)
				.firstName(UP_FIRST_NAME)
				.lastName(UP_LAST_NAME)
				.email(UP_EMAIL)
				.build();

		mockMvc.perform(put("/vets/" + id)
						.content(om.writeValueAsString(upVet))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk());

		// FIND
		mockMvc.perform(get("/vets/" + id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(id)))
				.andExpect(jsonPath("$.firstName", is(UP_FIRST_NAME)))
				.andExpect(jsonPath("$.lastName", is(UP_LAST_NAME)))
				.andExpect(jsonPath("$.email", is(UP_EMAIL)));

		// DELETE
		mockMvc.perform(delete("/vets/" + id))
				.andExpect(status().isOk());
	}

	@Test
	public void testDeleteVet() throws Exception {

		Integer id = createVet("Vet", "ToDelete");

		mockMvc.perform(delete("/vets/" + id))
				.andExpect(status().isOk());

		mockMvc.perform(get("/vets/" + id))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testDeleteVetKO() throws Exception {

		mockMvc.perform(delete("/vets/1000"))
				.andExpect(status().isNotFound());
	}

	// ===================== HELPER =====================

	/**
	 * Crea un veterinario vía POST /vets y devuelve su id
	 */
	private Integer createVet(String firstName, String lastName) throws Exception {

		VetDTO newVet = VetDTO.builder()
				.firstName(firstName)
				.lastName(lastName)
				.email(firstName.toLowerCase() + "." + lastName.toLowerCase() + "@petclinic.com")
				.phone("6085550000")
				.build();

		ResultActions mvcActions = mockMvc.perform(post("/vets")
						.content(om.writeValueAsString(newVet))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		String response = mvcActions.andReturn().getResponse().getContentAsString();
		return JsonPath.parse(response).read("$.id");
	}

}
