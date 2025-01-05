package com.aluracursos.foro_hub.domain;

public class TopicoNoEncontradoException extends RuntimeException {
    public TopicoNoEncontradoException(String message) {
        super(message);
    }
}
