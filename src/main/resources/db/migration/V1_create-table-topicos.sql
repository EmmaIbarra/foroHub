create table topicos (
    id bigint not null auto_increment,
    titulo varchar(255) not null,
    mensaje text not null,
    fecha_de_creacion datetime not null,
    status tinyint(1) not null,
    autor varchar(255) not null,
    curso varchar(255) not null

    primary key(id)
);
