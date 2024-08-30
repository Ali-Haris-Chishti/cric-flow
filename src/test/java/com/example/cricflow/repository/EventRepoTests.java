package com.example.cricflow.repository;

import com.example.cricflow.BaseData;
import com.example.cricflow.model.BallEvent;
import com.example.cricflow.model.event.Wicket;
import com.example.cricflow.model.literal.WicketType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class EventRepoTests extends BaseData {

    @Autowired private EventRepo eventRepo;

    @BeforeEach
    public void setUp() {
        prepare();
    }

    @AfterEach
    public void tearDown() {
        deleteRelatedTablesData();
    }

    @DisplayName("Repository Test for adding a event")
    @Test
    public void givenBallEventObject_whenSaved_thenBallEventIsReturned() {
        //given

        //when
        BallEvent savedBallEvent = eventRepo.save(event1);

        //then
        assertThat(savedBallEvent).isNotNull();
        assertThat(savedBallEvent.eventEquals(event1)).isTrue();
    }

    @DisplayName("Repository Test for updating a event")
    @Test
    public void givenBallEventObject_whenUpdated_thenUpdatedBallEventIsReturned() {
        //given
        event1 = eventRepo.save(event1);

        //when
        ((Wicket) event1).setWicketType(WicketType.RUN_OUT);
        BallEvent updatedEvent = eventRepo.save(event1);

        //then
        assertThat(((Wicket)updatedEvent).getWicketType()).isEqualTo(((Wicket)event1).getWicketType());
        assertThat(updatedEvent.eventEquals(event1)).isTrue();
    }

    @DisplayName("Repository Test for retrieving single event")
    @Test
    public void givenBallEventId_whenRetrieved_thenOptionalBallEventIsReturned() {
        //given
        event1 = eventRepo.save(event1);

        //when
        Optional<BallEvent> present = eventRepo.findById(event1.getEventId());
        Optional<BallEvent> empty = eventRepo.findById(999L);

        //then
        assertThat(present).isPresent();
        assertThat(empty).isNotPresent();
    }

    @DisplayName("Repository Test for deleting a event")
    @Test
    public void givenBallEventId_whenDeleted_thenBallEventIsRemoved(){
        //given
        event1 = eventRepo.save(event1);

        //when
        eventRepo.deleteById(event1.getEventId());
        Optional<BallEvent> optionalBallEvent = eventRepo.findById(event1.getEventId());

        //then
        assertThat(optionalBallEvent).isNotPresent();
    }

    @DisplayName("Repository Test for adding multiple events")
    @Test
    public void givenListOfBallEvents_whenSaved_thenListOfEmployeesIsReturned() {
        //given
        List<BallEvent> events = List.of(event1, event2);

        //when
        List<BallEvent> ballEvents = eventRepo.saveAll(events);

        //then
        assertThat(ballEvents.get(0).eventEquals(events.get(0))).isTrue();
        assertThat(ballEvents.get(1).eventEquals(events.get(1))).isTrue();
        assertThat(ballEvents.size()).isEqualTo(events.size());
    }

    @DisplayName("Repository Test for retrieving list of all events")
    @Test
    public void givenNothing_whenAllBallEventsRetrieved_thenListOfAllBallEventsIsReturned(){
        //given
        List<BallEvent> events = List.of(event1, event2);
        eventRepo.saveAll(events);

        //when
        List<BallEvent> retrievedEvents = eventRepo.findAll();

        //then
        assertThat(retrievedEvents.get(0).eventEquals(events.get(0))).isTrue();
        assertThat(retrievedEvents.get(1).eventEquals(events.get(1))).isTrue();
        assertThat(retrievedEvents.size()).isEqualTo(events.size());
    }

    @Override
    public void deleteRelatedTablesData(){
        eventRepo.deleteAll();
    }

    @Override
    public void prepare() {

    }
}
