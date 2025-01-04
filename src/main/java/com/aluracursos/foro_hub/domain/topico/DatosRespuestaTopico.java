package com.aluracursos.foro_hub.domain.topico;

import java.time.LocalDateTime;

public record DatosRespuestaTopico(
        Long id,
        String titulo,
        String mensaje,
        LocalDateTime fechaDeCreacion,
        Boolean status,
        String autor,
        String curso
) {
}
