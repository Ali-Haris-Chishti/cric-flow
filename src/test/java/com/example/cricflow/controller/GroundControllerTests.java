package com.example.cricflow.controller;

import com.example.cricflow.exception.EntityDoesNotExistsException;
import com.example.cricflow.exception.NameAlreadyExistsException;
import com.example.cricflow.exception.validator.GroundFieldsException;
import com.example.cricflow.model.Ground;
import com.example.cricflow.service.GroundService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = GroundController.class)
public class GroundControllerTests {

    @MockBean
    private GroundService groundService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("Controller Test for adding a ground with unique name")
    @Test
    public void givenUniqueGroundName_whenCreateGroundIsHit_thenGroundObjectWithGivenNameToUpperCaseAndCreatedStatusIsReturned() throws Exception {
        //given
        String groundName = "Nice Ground";
        given(groundService.createGround(groundName))
                .willReturn(new ResponseEntity<>(new Ground(1L, groundName.toUpperCase()), HttpStatus.CREATED));

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
    }

    @DisplayName("Controller Test for adding a ground with non unique name")
    @Test
    public void givenNonUniqueGroundName_whenCreateGroundIsHit_thenConflictStatusIsReturned() throws Exception {
        //given
        String groundName = "Nice Ground";
        given(groundService.createGround(groundName))
                .willThrow(NameAlreadyExistsException.class);

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
        given(groundService.createGround(groundName))
                .willThrow(GroundFieldsException.class);

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

    @DisplayName("Controller Test for adding multiple grounds with unique name")
    @Test
    public void givenMultipleUniqueGroundNames_whenCreateAllGroundsIsHit_thenListOfCreatedGroundsIsReturned() throws Exception {
        //given
        List<String> groundNames = Arrays.asList( "Nice Ground", "SCME Ground", "HBL Ground");
        given(groundService.createMultipleGrounds(groundNames))
                .willReturn(new ResponseEntity<>(
                        Arrays.asList(new Ground(1L, groundNames.get(0).toUpperCase()),
                        new Ground(2L, groundNames.get(1).toUpperCase()),
                        new Ground(3L, groundNames.get(2).toUpperCase()))
                        , HttpStatus.CREATED
                ));

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
    }

    @DisplayName("Controller Test for reading a ground with id that exists")
    @Test
    public void givenGroundIdThatExists_whenGetGroundIsHit_thenGroundObjectWithGivenIdIsReturned() throws Exception {
        //given
        Long groundId = 1L;
        given(groundService.readGround(groundId))
                .willReturn(new ResponseEntity<>(new Ground(1L, "NICE GROUND"), HttpStatus.OK));

        //when
        ResultActions response = mockMvc.perform(
                get("/api/v1/ground/get/1")
                        .contentType("application/json")
        );

        //then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groundId", is(1)))
                .andExpect(jsonPath("$.groundName", is("NICE GROUND")));
    }

    @DisplayName("Controller Test for reading a ground with id that does not exists")
    @Test
    public void givenGroundIdThatDoesNotExists_whenGetGroundIsHit_thenEntityDoesNotExistsExceptionIsThrown() throws Exception {
        //given
        Long groundId = 1L;
        given(groundService.readGround(groundId))
                .willThrow(EntityDoesNotExistsException.class);

        //when
        ResultActions response = mockMvc.perform(
                get("/api/v1/ground/get/1")
                        .contentType("application/json")
        );

        //then
        response.andDo(print())
                .andExpect(status().isNotFound());
    }

    @DisplayName("Controller Test for retrieving all grounds")
    @Test
    public void givenNothing_whenGetAllIsHit_thenListOfAllGroundsIsReturned() throws Exception{
        //given
        List<Ground> grounds = Arrays.asList(new Ground(1L, "NICE GROUND"), new Ground(2L, "SCME GROUND"));
        given(groundService.readAllGrounds())
                .willReturn(new ResponseEntity<>(grounds, HttpStatus.OK));

        //when
        ResultActions response = mockMvc.perform(get("/api/v1/ground/get-all")
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }


    @DisplayName("Controller Test for updating a ground")
    @Test
    public void givenValidUpdatedGroundObject_whenUpdateHit_thenGroundIsUpdatedAndUpdatedGroundAndCreatedStatusIsReturned() throws Exception{
        //given
        Ground ground = new Ground(1L, "NICE GROUND");
        ground.setGroundName("SCME GROUND");
        given(groundService.updateGround(ground))
                .willReturn(new ResponseEntity<>(ground, HttpStatus.CREATED));
        //when
        ResultActions response = mockMvc.perform(put("/api/v1/ground/update")
                .content(objectMapper.writeValueAsString(ground))
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.groundName", is(ground.getGroundName().toUpperCase())));
    }

    @DisplayName("Controller Test for deleting a ground")
    @Test
    public void givenGroundIdThatExists_whenDeleteIsHit_thenGroundIsDeletedSuccessMessageWithOkStatusIsReturned() throws Exception{
        //given
        given(groundService.deleteGround(1L))
                .willReturn(new ResponseEntity<>("GROUND WITH ID: " + 1 + ", DELETED SUCCESSFULLY!", HttpStatus.OK));
        //when
        ResultActions response = mockMvc.perform(delete("/api/v1/ground/delete/1")
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        response.andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("Controller Test for deleting a grounds")
    @Test
    public void givenNothing_whenDeleteAllIsHit_thenAllGroundsAreDeletedSuccessMessageWithOkStatusIsReturned() throws Exception{
        //given
        given(groundService.deleteAllGrounds())
                .willReturn(new ResponseEntity<>("ALL 3 GROUNDS DELETED SUCCESSFULLY!", HttpStatus.OK));
        //when
        ResultActions response = mockMvc.perform(delete("/api/v1/ground/delete/1")
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        response.andDo(print())
                .andExpect(status().isOk());
    }

}
