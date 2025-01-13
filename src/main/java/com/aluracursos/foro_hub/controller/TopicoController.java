package com.aluracursos.foro_hub.controller;

import com.aluracursos.foro_hub.domain.topico.*;
import com.aluracursos.foro_hub.infra.errores.TratadorDeErrores;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/topicos")
public class TopicoController {

    private final TopicoRepository topicoRepository;

    public TopicoController(TopicoRepository topicoRepository) {
        this.topicoRepository = topicoRepository;
    }

    @PostMapping
    public ResponseEntity<?> registrarTopico(@RequestBody @Valid DatosRegistroTopico datosRegistroTopico,
                                                                UriComponentsBuilder uriComponentsBuilder) {
        if (topicoRepository.existsByTituloAndMensaje(datosRegistroTopico.titulo(), datosRegistroTopico.mensaje())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new TratadorDeErrores.ErrorRespuesta("Ya existe un tópico con el mismo título y mensaje."));
        }

        Topico topico = topicoRepository.save(new Topico(datosRegistroTopico));
        DatosRespuestaTopico datosRespuestaTopico = new DatosRespuestaTopico(
                topico.getId(),
                topico.getTitulo(),
                topico.getMensaje(),
                topico.getFechaDeCreacion(),
                topico.getStatus(),
                topico.getAutor(),
                topico.getCurso()
        );

        URI url = uriComponentsBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();
        return ResponseEntity.created(url).body(datosRespuestaTopico);
    }

    @GetMapping
    public ResponseEntity<Page<DatosListadoTopico>> listarTopicos(@PageableDefault(size = 10, sort = "fechaDeCreacion", direction = Sort.Direction.ASC) Pageable paginacion) {
        return ResponseEntity.ok(topicoRepository.findAll(paginacion).map(DatosListadoTopico::new));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DatosRespuestaTopico> retornaDatosTopico(@PathVariable Long id) {
        Optional<Topico> topicoOpt = topicoRepository.findById(id);

        if (topicoOpt.isPresent()) {
            Topico topico = topicoOpt.get();
            var datosTopico = new DatosRespuestaTopico(
                    topico.getId(),
                    topico.getTitulo(),
                    topico.getMensaje(),
                    topico.getFechaDeCreacion(),
                    topico.getStatus(),
                    topico.getAutor(),
                    topico.getCurso());
            return ResponseEntity.ok(datosTopico);
        }

        throw new EntityNotFoundException("El tópico con ID " + id + " no fue encontrado.");
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> actualizarTopico(@PathVariable Long id,
                                                                 @RequestBody @Valid
                                                                 DatosActualizarTopico datosActualizarTopico) {
        Optional<Topico> topicoOpt = topicoRepository.findById(id);

        if (topicoOpt.isPresent()) {
            Topico topico = topicoOpt.get();
            boolean tituloDuplicado = topicoRepository.existsByTituloAndIdNot(datosActualizarTopico.titulo(), id);
            boolean mensajeDuplicado = topicoRepository.existsByMensajeAndIdNot(datosActualizarTopico.mensaje(), id);

            if (tituloDuplicado || mensajeDuplicado) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new TratadorDeErrores.ErrorRespuesta("Ya existe un tópico con el mismo título y mensaje."));
            }
            topico.actualizarDatos(datosActualizarTopico);
            return ResponseEntity.ok(new DatosRespuestaTopico(
                    topico.getId(),
                    topico.getTitulo(),
                    topico.getMensaje(),
                    topico.getFechaDeCreacion(),
                    topico.getStatus(),
                    topico.getAutor(),
                    topico.getCurso()
            ));
        }

        throw new EntityNotFoundException("El tópico con ID " + id + " no fue encontrado.");
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity eliminarTopico(@PathVariable Long id) {
        Optional<Topico> topicoOpt = topicoRepository.findById(id);

        if (topicoOpt.isPresent()) {
            topicoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }

        throw new EntityNotFoundException("El tópico con ID " + id + " no fue encontrado.");
    }

}
