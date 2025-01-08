package com.aluracursos.foro_hub.domain.perfil;

import com.aluracursos.foro_hub.domain.usuario.Usuario;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "perfiles")
@Entity(name = "Perfil")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Setter
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;


}
