package andrebirolo.com.passin.controllers;

import andrebirolo.com.passin.dto.attendee.AttendeeListResponseDTO;
import andrebirolo.com.passin.dto.event.EventIdDTO;
import andrebirolo.com.passin.dto.event.EventRequestDTO;
import andrebirolo.com.passin.dto.event.EventResponseDTO;
import andrebirolo.com.passin.services.AttendeeService;
import andrebirolo.com.passin.services.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService service;
    private final AttendeeService attendeeService;

    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponseDTO> getEvent(@PathVariable String eventId) {
        EventResponseDTO event = this.service.getEventDetail(eventId);
        return ResponseEntity.ok(event);
    }

    @PostMapping
    public ResponseEntity<EventIdDTO> createEvent(@RequestBody EventRequestDTO body, UriComponentsBuilder uriBuilder) {
        EventIdDTO event = this.service.createEvent(body);
        var uri = uriBuilder.path("/events/{id}").buildAndExpand(event.id()).toUri();
        return ResponseEntity.created(uri).body(event);
    }

    @GetMapping("/events/attendees/{eventId}")
    public ResponseEntity<AttendeeListResponseDTO> getEventAttendees(@PathVariable String eventId) {
        AttendeeListResponseDTO attendees = this.attendeeService.getEventsAttendee(eventId);
        return ResponseEntity.ok(attendees);
    }

}
