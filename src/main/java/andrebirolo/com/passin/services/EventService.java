package andrebirolo.com.passin.services;

import andrebirolo.com.passin.domain.attendee.Attendee;
import andrebirolo.com.passin.domain.attendee.exceptions.EventFullException;
import andrebirolo.com.passin.domain.event.Event;
import andrebirolo.com.passin.domain.event.exceptions.EventNotFoundException;
import andrebirolo.com.passin.dto.attendee.AttendeeIdDTO;
import andrebirolo.com.passin.dto.attendee.AttendeeRequestDTO;
import andrebirolo.com.passin.dto.event.EventIdDTO;
import andrebirolo.com.passin.dto.event.EventRequestDTO;
import andrebirolo.com.passin.dto.event.EventResponseDTO;
import andrebirolo.com.passin.repositories.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final AttendeeService attendeeService;

    public EventResponseDTO getEventDetail(String eventId) {
        Event event = getEvent(eventId);
        List<Attendee> attendeeList = this.attendeeService.getAllAttendeesFromEvent(eventId);
        return new EventResponseDTO(event, attendeeList.size());
    }

    public EventIdDTO createEvent(EventRequestDTO eventRequestDTO) {
        Event event = new Event();
        event.setTitle(eventRequestDTO.title());
        event.setDetails(eventRequestDTO.details());
        event.setMaximumAttendees(eventRequestDTO.maximumAttendees());
        event.setSlug(createSlug(eventRequestDTO.title()));
        this.eventRepository.save(event);
        return new EventIdDTO(event.getId());
    }

    private String createSlug(String title) {
        String normalized = Normalizer.normalize(title, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCOMBINING_DIACRITICAL_MARKS}", "")
                .replaceAll("[^\\w\\s]", "")
                .replaceAll("\\s+", "-")
                .toLowerCase();
    }


    public AttendeeIdDTO registerAttendeeOnEvent(String eventId, AttendeeRequestDTO attendeeRequestDTO) {
        this.attendeeService.verifyAttendeeSubscription(attendeeRequestDTO.email(), eventId);

        Event event = getEvent(eventId);
        List<Attendee> attendeeList = this.attendeeService.getAllAttendeesFromEvent(eventId);

        if (event.getMaximumAttendees() <= attendeeList.size()) {
            throw new EventFullException("Event is full");
        } else {
            Attendee attendee = new Attendee();
            attendee.setName(attendeeRequestDTO.name());
            attendee.setEmail(attendeeRequestDTO.email());
            attendee.setEvent(event);
            attendee.setCreatedAt(LocalDateTime.now());

            this.attendeeService.registerAttendee(attendee);

            return new AttendeeIdDTO(attendee.getId());
        }
    }

    private Event getEvent(String eventId) {
        return this.eventRepository
                .findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + eventId));
    }
}
