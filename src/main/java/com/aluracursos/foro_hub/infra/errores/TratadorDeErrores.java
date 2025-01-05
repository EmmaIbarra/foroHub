package com.aluracursos.foro_hub.infra.errores;

import com.aluracursos.foro_hub.domain.TopicoNoEncontradoException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TratadorDeErrores {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity tratarError400(MethodArgumentNotValidException e){
        var errores = e.getFieldErrors().stream().map(DatosErrorValidacion::new).toList() ;
        return ResponseEntity.badRequest().body(errores);
    }

    @ExceptionHandler({EntityNotFoundException.class, TopicoNoEncontradoException.class})
    public ResponseEntity tratarError404(Exception e) {
        String mensaje = e.getMessage() != null ? e.getMessage() : "El recurso solicitado no fue encontrado.";
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorRespuesta(mensaje));
    }


    private record DatosErrorValidacion(String campo, String error){
        public DatosErrorValidacion(FieldError error) {
            this(error.getField(), error.getDefaultMessage());
        }
    }


    public record ErrorRespuesta(String mensaje) {
    }


}
