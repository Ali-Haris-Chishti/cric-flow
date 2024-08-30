package com.example.cricflow.integration;

import com.example.cricflow.model.Ground;
import com.example.cricflow.repository.GroundRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class GroundIntegrationTests {

    @Autowired private GroundRepo groundRepo;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        groundRepo.deleteAll();
    }

    @DisplayName("Integration Test for adding a ground with unique name")
    @Test
    public void givenUniqueGroundName_whenCreateGroundIsHit_thenGroundObjectWithGivenNameToUpperCaseIsReturned() throws Exception {
        //given
        String groundName = "Nice Ground";
        //when
        ResultActions response = mockMvc.perform(
                post("/api/v1/ground/create")
                        .param("groundName", groundName)
                        .contentType("application/json")
        );

        //then
        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.groundId", is(notNullValue())))
                .andExpect(jsonPath("$.groundName", is(groundName.toUpperCase())));
        assertThat(groundRepo.findAll().size()).isEqualTo(1);
    }

    @DisplayName("Integration Test for adding a ground with non unique name")
    @Test
    public void givenNonUniqueGroundName_whenCreateGroundIsHit_thenNameAlreadyExistsExceptionIsThrown() throws Exception {
        //given
        String groundName = "Nice Ground";
        // now ground table already has a ground with given name, ignoring type case
        groundRepo.save(new Ground(null, "NICE GROUND"));

        //when
        ResultActions response = mockMvc.perform(
                post("/api/v1/ground/create")
                        .param("groundName", groundName)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        response.andDo(print())
                .andExpect(status().isConflict());
    }

    @DisplayName("Controller Test for adding a ground with name not meeting the validations")
    @Test
    public void givenInvalidGroundName_whenCreateGroundIsHit_thenBadRequestStatusIsReturned() throws Exception {
        //given
        String groundName = "Ni";

        //when
        ResultActions response = mockMvc.perform(
                post("/api/v1/ground/create")
                        .param("groundName", groundName)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        response.andDo(print())
                .andExpect(status().isNotAcceptable());
    }

    @DisplayName("Integration Test for adding multiple grounds with unique name")
    @Test
    public void givenMultipleUniqueGroundNames_whenCreateAllGroundsIsHit_thenListOfCreatedGroundsIsReturned() throws Exception {
        //given
        List<String> groundNames = Arrays.asList( "Nice Ground", "SCME Ground", "HBL Ground");

        //when
        ResultActions response = mockMvc.perform(
                post("/api/v1/ground/create-all")
                        .content(objectMapper.writeValueAsString(groundNames))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].groundName", is(groundNames.get(0).toUpperCase())))
                .andExpect(jsonPath("$[1].groundName", is(groundNames.get(1).toUpperCase())))
                .andExpect(jsonPath("$[2].groundName", is(groundNames.get(2).toUpperCase())));
        assertThat(groundRepo.findAll().size()).isEqualTo(3);
    }

    @DisplayName("Integration Test for reading a ground with id that exists")
    @Test
    public void givenGroundIdThatExists_whenGetGroundIsHit_thenGroundObjectWithGivenIdIsReturned() throws Exception {
        //given
        Ground ground = groundRepo.save(new Ground(null, "NICE GROUND"));

        //when
        ResultActions response = mockMvc.perform(
                get("/api/v1/ground/get/" + ground.getGroundId())
                        .contentType("application/json")
        );

        //then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groundId", is(ground.getGroundId().intValue())))
                .andExpect(jsonPath("$.groundName", is("NICE GROUND")));
    }

    @DisplayName("Integration Test for reading a ground with id that does not exists")
    @Test
    public void givenGroundIdThatDoesNotExists_whenGetGroundIsHit_thenEntityDoesNotExistsExceptionIsThrown() throws Exception {
        //given
        Long groundId = 1L;
        //when
        ResultActions response = mockMvc.perform(
                get("/api/v1/ground/get/1")
                        .contentType("application/json")
        );

        //then
        response.andDo(print())
                .andExpect(status().isNotFound());
    }

    @DisplayName("Integration Test for retrieving all grounds")
    @Test
    public void givenNothing_whenGetAllIsHit_thenListOfAllGroundsIsReturned() throws Exception{
        //given
        List<Ground> grounds = Arrays.asList(new Ground(null, "NICE GROUND"), new Ground(null, "SCME GROUND"));
        groundRepo.saveAll(grounds);
        //when
        ResultActions response = mockMvc.perform(get("/api/v1/ground/get-all")
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].groundName", is("NICE GROUND")))
                .andExpect(jsonPath("$[1].groundName", is("SCME GROUND")))
                .andExpect(jsonPath("$", hasSize(2)));
        assertThat(groundRepo.findAll().size()).isEqualTo(2);
    }

    @DisplayName("Integration Test for updating a ground")
    @Test
    public void givenValidUpdatedGroundObject_whenUpdateHit_thenUpdatedGroundAndCreatedStatusIsReturned() throws Exception{
        //given
        Ground ground = groundRepo.save(new Ground(null, "NICE GROUND"));
        ground.setGroundName("SCME Ground");
        //when
        ResultActions response = mockMvc.perform(put("/api/v1/ground/update")
                .content(objectMapper.writeValueAsString(ground))
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.groundName", is(ground.getGroundName().toUpperCase())));
        assertThat(groundRepo.findAll().size()).isEqualTo(1);
    }

    @DisplayName("Integration Test for deleting a ground")
    @Test
    public void givenGroundIdThatExists_whenDeleteIsHit_thenGroundIsDeletedSuccessMessageWithOkStatusIsReturned() throws Exception{
        //given
        Ground ground1 = groundRepo.save(new Ground(null, "NICE GROUND"));
        Ground ground2 = groundRepo.save(new Ground(null, "SCME GROUND"));
        Ground ground3 = groundRepo.save(new Ground(null, "SMME GROUND"));

        //when
        ResultActions response = mockMvc.perform(delete("/api/v1/ground/delete/" + ground1.getGroundId())
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        response.andDo(print())
                .andExpect(status().isOk());
        assertThat(groundRepo.findAll().size()).isEqualTo(2);
        assertThat(groundRepo.findById(ground1.getGroundId())).isNotPresent();
    }

    @DisplayName("Integration Test for deleting a;; grounds")
    @Test
    public void givenNothing_whenDeleteAllIsHit_thenAllGroundsAreDeletedSuccessMessageWithOkStatusIsReturned() throws Exception{
        //given
        Ground ground1 = groundRepo.save(new Ground(null, "NICE GROUND"));
        Ground ground2 = groundRepo.save(new Ground(null, "SCME GROUND"));
        Ground ground3 = groundRepo.save(new Ground(null, "SMME GROUND"));

        //when
        ResultActions response = mockMvc.perform(delete("/api/v1/ground/delete-all")
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        response.andDo(print())
                .andExpect(status().isOk());
        assertThat(groundRepo.findAll().size()).isEqualTo(0);
    }

    @DisplayName("Controller Test for searching a ground")
    @Test
    public void givenCharacterSequence_whenSearchIsHit_thenListOfAllGroundsWithGivenSequenceInTheirNameAndOkStatusIsReturned() throws Exception{
        //given
        Ground ground1 = new Ground(null, "NICE GROUND");
        Ground ground2 = new Ground(null, "SCME GROUND");
        Ground ground3 = new Ground(null, "HBL GROUND");
        groundRepo.saveAll(Arrays.asList(ground1, ground2, ground3));

        //when
        ResultActions response = mockMvc.perform(get("/api/v1/ground/search")
                .param("seq", "e g")
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].size()", is(2)));
    }

}
