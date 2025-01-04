package com.aluracursos.foro_hub.domain.topico;

import jakarta.validation.constraints.NotNull;

public record DatosRegistroTopico(
        @NotNull
        String titulo,
        @NotNull
        String mensaje,
        @NotNull
        String autor,
        @NotNull
        String curso
) {
}
