package com.aluracursos.foro_hub.controller;

import com.aluracursos.foro_hub.domain.perfil.DatosPerfil;
import com.aluracursos.foro_hub.domain.perfil.Perfil;
import com.aluracursos.foro_hub.domain.perfil.PerfilRepository;
import com.aluracursos.foro_hub.domain.usuario.*;
import com.aluracursos.foro_hub.infra.errores.TratadorDeErrores;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;

    public UsuarioController(UsuarioRepository usuarioRepository, PerfilRepository perfilRepository) {
        this.usuarioRepository = usuarioRepository;
        this.perfilRepository = perfilRepository;
    }

    @PostMapping
    public ResponseEntity<?> registrarUsuario(@RequestBody @Valid DatosRegistroUsuario datosRegistroUsuario,
                                                                  UriComponentsBuilder uriComponentsBuilder) {
        if (usuarioRepository.existsByEmail(datosRegistroUsuario.email())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new TratadorDeErrores.ErrorRespuesta("Ya existe un usuario con el mismo email."));
        }

        Usuario usuario = usuarioRepository.save(new Usuario(datosRegistroUsuario));
        DatosRespuestaUsuario datosRespuestaUsuario = new DatosRespuestaUsuario(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail()
        );

        URI url = uriComponentsBuilder.path("/usuarios/{id}").buildAndExpand(usuario.getId()).toUri();
        return ResponseEntity.created(url).body(datosRespuestaUsuario);
    }

    @GetMapping
    public ResponseEntity<Page<DatosListadoUsuario>> listarUsuarios(
            @PageableDefault(size = 10, sort = "nombre", direction = Sort.Direction.ASC) Pageable paginacion) {
        return ResponseEntity.ok(usuarioRepository.findAll(paginacion).map(DatosListadoUsuario::new));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DatosRespuestaUsuario> retornaDatosUsuario(@PathVariable Long id) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            var datosUsuario = new DatosRespuestaUsuario(
                    usuario.getId(),
                    usuario.getNombre(),
                    usuario.getEmail()
            );
            return ResponseEntity.ok(datosUsuario);
        }

        throw new EntityNotFoundException("El usuario con ID " + id + " no fue encontrado.");
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id,
                                                                   @RequestBody @Valid DatosActualizarUsuario datosActualizarUsuario) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            boolean emailDuplicado = usuarioRepository.existsByEmailAndIdNot(datosActualizarUsuario.email(), id);

            if (emailDuplicado) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new TratadorDeErrores.ErrorRespuesta("Ya existe un usuario registrado con el email: " + datosActualizarUsuario.email()));
            }
            usuario.actualizarDatos(datosActualizarUsuario);
            return ResponseEntity.ok(new DatosRespuestaUsuario(
                    usuario.getId(),
                    usuario.getNombre(),
                    usuario.getEmail()
            ));
        }

        throw new EntityNotFoundException("El usuario con ID " + id + " no fue encontrado.");
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity eliminarUsuario(@PathVariable Long id) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

        if (usuarioOpt.isPresent()) {
            usuarioRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }

        throw new EntityNotFoundException("El usuario con ID " + id + " no fue encontrado.");
    }

    @PostMapping("/{id}/perfiles")
    @Transactional
    public ResponseEntity agregarPerfil(@PathVariable Long id, @RequestBody @Valid DatosPerfil datosPerfil) {
        var usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario con ID " + id + " no encontrado."));
        Perfil perfil = new Perfil(null, datosPerfil.nombre(), usuario);
        usuario.agregarPerfil(perfil);
        return ResponseEntity.ok("Perfil agregado exitosamente.");
    }

    @DeleteMapping("/{id}/perfiles/{perfilId}")
    @Transactional
    public ResponseEntity eliminarPerfil(@PathVariable Long id, @PathVariable Long perfilId) {
        var usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario con ID " + id + " no encontrado."));
        var perfil = usuario.getPerfiles().stream()
                .filter(p -> p.getId().equals(perfilId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Perfil con ID " + perfilId + " no encontrado."));

        usuario.eliminarPerfil(perfil);
        perfilRepository.delete(perfil);

        return ResponseEntity.ok("Perfil eliminado exitosamente.");
    }

    @GetMapping("/{id}/perfiles")
    public ResponseEntity<List<DatosPerfil>> listarPerfiles(@PathVariable Long id) {
        var usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario con ID " + id + " no encontrado."));
        var perfiles = usuario.getPerfiles()
                .stream()
                .map(perfil -> new DatosPerfil(perfil.getId(), perfil.getNombre()))
                .toList();
        return ResponseEntity.ok(perfiles);
    }


}


