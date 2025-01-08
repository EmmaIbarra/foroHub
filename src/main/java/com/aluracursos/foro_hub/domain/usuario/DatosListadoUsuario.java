package com.aluracursos.foro_hub.domain.usuario;

public record DatosListadoUsuario(
        Long id,
        String nombre,
        String email
) {
    public DatosListadoUsuario(Usuario usuario) {
        this(usuario.getId(), usuario.getNombre(), usuario.getEmail());
    }
}
