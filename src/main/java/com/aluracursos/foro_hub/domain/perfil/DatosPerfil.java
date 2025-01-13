package com.aluracursos.foro_hub.domain.perfil;

import jakarta.validation.constraints.NotBlank;

public record DatosPerfil(
        Long id,
        @NotBlank String nombre
) {
}
