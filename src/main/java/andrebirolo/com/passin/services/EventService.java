package andrebirolo.com.passin.services;

import andrebirolo.com.passin.domain.attendee.Attendee;
import andrebirolo.com.passin.domain.event.Event;
import andrebirolo.com.passin.domain.event.exceptions.EventNotFoundException;
import andrebirolo.com.passin.dto.event.EventIdDTO;
import andrebirolo.com.passin.dto.event.EventRequestDTO;
import andrebirolo.com.passin.dto.event.EventResponseDTO;
import andrebirolo.com.passin.repositories.AttendeeRepository;
import andrebirolo.com.passin.repositories.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final AttendeeRepository attendeeRepository;

    public EventResponseDTO getEventDetail(String eventId) {
        Event event = this.eventRepository
                .findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + eventId));
        List<Attendee> attendeeList = this.attendeeRepository.findByEventId(eventId);
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
}
