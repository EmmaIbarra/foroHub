package com.aluracursos.foro_hub.infra.errores;

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

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity tratarError404(EntityNotFoundException e){
        String mensajePersonalizado = extraerMensaje(e.getMessage());
        var error = new DatosErrorInterno("Entidad no encontrada", mensajePersonalizado);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    private String extraerMensaje(String mensajeOriginal) {
        if (mensajeOriginal.contains("Unable to find")) {
            // Extraer el ID y retornar un mensaje simplificado
            String[] partes = mensajeOriginal.split(" ");
            String id = partes[partes.length - 1]; // Última palabra debería ser el ID
            return "No se encontró el tópico con el ID " + id;
        }
        // Si no cumple el patrón esperado, devolver el mensaje original
        return mensajeOriginal;
    }


    private record DatosErrorValidacion(String campo, String error){
        public DatosErrorValidacion(FieldError error) {
            this(error.getField(), error.getDefaultMessage());
        }
    }

    private record DatosErrorInterno(String titulo, String detalle) {}

}
