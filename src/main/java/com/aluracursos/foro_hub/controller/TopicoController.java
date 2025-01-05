package com.aluracursos.foro_hub.controller;

import com.aluracursos.foro_hub.domain.TopicoNoEncontradoException;
import com.aluracursos.foro_hub.domain.ValidacionException;
import com.aluracursos.foro_hub.domain.topico.*;
import com.aluracursos.foro_hub.infra.errores.TratadorDeErrores;
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
import java.util.Optional;

@RestController
@RequestMapping("/topicos")
public class TopicoController {

    private final TopicoRepository topicoRepository;

    public TopicoController(TopicoRepository topicoRepository) {
        this.topicoRepository = topicoRepository;
    }

    @PostMapping
    public ResponseEntity<DatosRespuestaTopico> registrarTopico(@RequestBody @Valid DatosRegistroTopico datosRegistroTopico,
                                                                UriComponentsBuilder uriComponentsBuilder) {
        if (topicoRepository.existsByTituloAndMensaje(datosRegistroTopico.titulo(), datosRegistroTopico.mensaje())) {
            throw new ValidacionException("Ya existe un tópico con el mismo título y/o mensaje.");
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

//    @GetMapping("/buscar")
//    public ResponseEntity<List<DatosListadoTopico>> buscarTopicos(
//            @RequestParam String curso,
//            @RequestParam int anio) {
//        LocalDateTime inicioAnio = LocalDateTime.of(anio, 1, 1, 0, 0);
//        LocalDateTime finAnio = LocalDateTime.of(anio, 12, 31, 23, 59);
//        List<Topico> topicos = topicoRepository.findByCursoAndFechaDeCreacionBetween(curso, inicioAnio, finAnio);
//        return ResponseEntity.ok(topicos.stream().map(DatosListadoTopico::new).toList());
//    }

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

        // Lanza una excepción si el tópico no se encuentra
        throw new TopicoNoEncontradoException("El tópico con ID " + id + " no fue encontrado.");
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<DatosRespuestaTopico> actualizarTopico(@PathVariable Long id,
                                                                 @RequestBody @Valid
                                                                 DatosActualizarTopico datosActualizarTopico) {
        Optional<Topico> topicoOpt = topicoRepository.findById(id);

        if (topicoOpt.isPresent()) {
            Topico topico = topicoOpt.get();
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

        throw new TopicoNoEncontradoException("El tópico con ID " + id + " no fue encontrado.");
    }

}
