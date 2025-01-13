package com.aluracursos.foro_hub.infra.excepciones;

public class ValidacionException extends RuntimeException {
    public ValidacionException(String message) {
        super(message);
    }
}
