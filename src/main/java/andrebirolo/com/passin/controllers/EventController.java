package andrebirolo.com.passin.controllers;

import andrebirolo.com.passin.dto.attendee.AttendeeIdDTO;
import andrebirolo.com.passin.dto.attendee.AttendeeListResponseDTO;
import andrebirolo.com.passin.dto.attendee.AttendeeRequestDTO;
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
    private final EventService eventService;
    private final AttendeeService attendeeService;

    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponseDTO> getEvent(@PathVariable String eventId) {
        EventResponseDTO event = this.eventService.getEventDetail(eventId);
        return ResponseEntity.ok(event);
    }

    @PostMapping
    public ResponseEntity<EventIdDTO> createEvent(@RequestBody EventRequestDTO body, UriComponentsBuilder uriBuilder) {
        EventIdDTO event = this.eventService.createEvent(body);
        var uri = uriBuilder.path("/events/{id}").buildAndExpand(event.id()).toUri();
        return ResponseEntity.created(uri).body(event);
    }

    @PostMapping("/{eventId}/attendees")
    public ResponseEntity<AttendeeIdDTO> registerParticipant(@PathVariable String eventId, @RequestBody AttendeeRequestDTO body, UriComponentsBuilder uriBuilder) {
        AttendeeIdDTO attendeeIdDTO = this.eventService.registerAttendeeOnEvent(eventId, body);
        var uri = uriBuilder.path("/attendees/{attendeId}/badge").buildAndExpand(attendeeIdDTO.attendeeId()).toUri();

        return ResponseEntity.created(uri).body(attendeeIdDTO);
    }

    @GetMapping("/events/attendees/{eventId}")
    public ResponseEntity<AttendeeListResponseDTO> getEventAttendees(@PathVariable String eventId) {
        AttendeeListResponseDTO attendees = this.attendeeService.getEventsAttendee(eventId);
        return ResponseEntity.ok(attendees);
    }

}
