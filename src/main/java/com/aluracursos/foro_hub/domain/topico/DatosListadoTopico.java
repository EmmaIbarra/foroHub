package com.aluracursos.foro_hub.domain.topico;

import java.time.LocalDateTime;

public record DatosListadoTopico(
        Long id,
        String titulo,
        String autor,
        String curso,
        LocalDateTime fechaDeCreacion) {

    public DatosListadoTopico(Topico topico) {
        this(topico.getId(), topico.getTitulo(), topico.getAutor(), topico.getCurso(), topico.getFechaDeCreacion());
    }
}
