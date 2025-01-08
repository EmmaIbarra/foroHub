create table usuarios (
    id bigint not null auto_increment,
    nombre varchar(255) not null,
    email varchar(255) not null,
    clave varchar(255) not null,

    primary key(id)
)