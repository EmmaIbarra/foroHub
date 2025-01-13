package com.aluracursos.foro_hub.infra.errores;

import com.aluracursos.foro_hub.infra.excepciones.ValidacionException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class TratadorDeErrores {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<DatosErrorValidacion>> tratarError400(MethodArgumentNotValidException e) {
        var errores = e.getFieldErrors().stream()
                .map(DatosErrorValidacion::new)
                .toList();
        return ResponseEntity.badRequest().body(errores);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorRespuesta> tratarError404(EntityNotFoundException e) {
        String mensaje = e.getMessage() != null ? e.getMessage() : "El recurso solicitado no fue encontrado.";
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorRespuesta(mensaje));
    }

    @ExceptionHandler(ValidacionException.class)
    public ResponseEntity<ErrorRespuesta> manejarErrorDeDuplicados(ValidacionException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorRespuesta(e.getMessage()));
    }


    private record DatosErrorValidacion(String campo, String error) {
        public DatosErrorValidacion(FieldError error) {
            this(error.getField(), error.getDefaultMessage());
        }
    }

    public record ErrorRespuesta(String mensaje) {}
}
