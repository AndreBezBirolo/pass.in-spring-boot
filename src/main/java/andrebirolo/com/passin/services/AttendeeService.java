package andrebirolo.com.passin.services;

import andrebirolo.com.passin.domain.attendee.Attendee;
import andrebirolo.com.passin.domain.attendee.exceptions.AttendeeAlreadyRegisteredException;
import andrebirolo.com.passin.domain.attendee.exceptions.AttendeeNotFoundException;
import andrebirolo.com.passin.domain.checkin.CheckIn;
import andrebirolo.com.passin.dto.attendee.AttendeeBadgeDTO;
import andrebirolo.com.passin.dto.attendee.AttendeeBadgeResponseDTO;
import andrebirolo.com.passin.dto.attendee.AttendeeDetails;
import andrebirolo.com.passin.dto.attendee.AttendeeListResponseDTO;
import andrebirolo.com.passin.repositories.AttendeeRepository;
import andrebirolo.com.passin.repositories.CheckinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttendeeService {
    private final AttendeeRepository attendeeRepository;
    private final CheckinRepository checkinRepository;

    public List<Attendee> getAllAttendeesFromEvent(String eventId) {
        return this.attendeeRepository.findByEventId(eventId);
    }

    public AttendeeListResponseDTO getEventsAttendee(String eventId) {
        List<Attendee> attendees = this.getAllAttendeesFromEvent(eventId);

        List<AttendeeDetails> attendeeDetailsList = attendees.stream()
                .map(attendee -> {
                    Optional<CheckIn> checkIn = this.checkinRepository.findByAttendeeId(attendee.getId());
                    LocalDateTime checkedInAt = checkIn.isPresent() ? checkIn.get().getCreatedAt() : null;
                    return new AttendeeDetails(attendee.getId(), attendee.getName(), attendee.getEmail(), attendee.getCreatedAt(), checkedInAt);
                })
                .toList();

        return new AttendeeListResponseDTO(attendeeDetailsList);
    }

    public void verifyAttendeeSubscription(String email, String eventId) {
        Optional<Attendee> isAttendeeRegistered = this.attendeeRepository.findByEventIdAndEmail(eventId, email);
        if (isAttendeeRegistered.isPresent()) {
            throw new AttendeeAlreadyRegisteredException("Attendee already registered");
        }
    }

    public void registerAttendee(Attendee newAttendee) {
        this.attendeeRepository.save(newAttendee);
    }

    public AttendeeBadgeResponseDTO getAttendeeBadge(String attendeeId, UriComponentsBuilder uriComponentsBuilder) {
        Attendee attendee = this.attendeeRepository.findById(attendeeId)
                .orElseThrow(() -> new AttendeeNotFoundException("Attendee not found"));

        var uri = uriComponentsBuilder.path("/attendee/{id}/check-in").buildAndExpand(attendee.getId()).toUriString();

        return new AttendeeBadgeResponseDTO(new AttendeeBadgeDTO(attendee.getName(), attendee.getEmail(), uri, attendee.getEvent().getId()));
    }
}
