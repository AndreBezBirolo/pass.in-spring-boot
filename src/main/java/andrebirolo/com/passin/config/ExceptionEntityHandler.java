package andrebirolo.com.passin.config;

import andrebirolo.com.passin.domain.event.exceptions.EventNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionEntityHandler {

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity handleException(Exception e) {
        return ResponseEntity.notFound().build();
    }
}
