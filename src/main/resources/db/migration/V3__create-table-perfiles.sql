create table perfiles (
    id bigint not null auto_increment,
    nombre varchar(255) not null,
    usuario_id bigint not null,

    primary key (id),
    foreign key (usuario_id) references usuarios(id) on delete cascade
);
