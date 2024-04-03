package andrebirolo.com.passin.repositories;

import andrebirolo.com.passin.domain.checkin.CheckIn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CheckinRepository extends JpaRepository<CheckIn, Integer> {
    
    Optional<CheckIn> findByAttendeeId(String attendeeId);
}
