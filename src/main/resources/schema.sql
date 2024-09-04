
create table IF NOT EXISTS GENRE
(
    ID   INTEGER not null,
    NAME CHARACTER VARYING(50),
    constraint GENRE_PK
        primary key (ID)
);

create table IF NOT EXISTS RATING
(
    ID   INTEGER not null,
    NAME CHARACTER VARYING(5),
    constraint RATING_PK
        primary key (ID)
);

create table IF NOT EXISTS FILM
(
    ID          BIGINT auto_increment,
    NAME        CHARACTER VARYING(100) not null,
    DESCRIPTION CHARACTER VARYING(200) not null,
    RELEASEDATE DATE                   not null,
    DURATION    INTEGER                not null,
    RATING_ID   INTEGER,
    constraint FILM_PK
        primary key (ID),
    constraint FILM_RATING_ID_FK
        foreign key (RATING_ID) references RATING
);

create table IF NOT EXISTS FILM_GENRE
(
    FILM_ID  BIGINT  not null,
    GENRE_ID INTEGER not null,
    constraint FILM_GENRE_PK
        primary key (FILM_ID, GENRE_ID),
    constraint FILM_GENRE_FILM_ID_FK
        foreign key (FILM_ID) references FILM,
    constraint FILM_GENRE_GENRE_ID_FK
        foreign key (GENRE_ID) references GENRE
);

create table IF NOT EXISTS "USER"
(
    ID       BIGINT auto_increment,
    EMAIL    CHARACTER VARYING(255) not null,
    LOGIN    CHARACTER VARYING(255) not null,
    NAME     CHARACTER VARYING(255),
    BIRTHDAY DATE                   not null,
    constraint USER_PK
        primary key (ID)
);

create table IF NOT EXISTS "LIKE"
(
    FILM_ID BIGINT not null,
    USER_ID BIGINT not null,
    constraint LIKE_PK
        primary key (USER_ID, FILM_ID),
    constraint LIKE_FILM_ID_FK
        foreign key (FILM_ID) references FILM,
    constraint LIKE_USERS_ID_FK
        foreign key (USER_ID) references "USER"
);

create table IF NOT EXISTS USER_RELATIONSHIP
(
    FOLLOWING_USER_ID BIGINT,
    FOLLOWED_USER_ID  BIGINT,
    CONFIRMED         BOOLEAN default FALSE not null,
    constraint USER_RELATIONSHIP_USER_ID_FK
        foreign key (FOLLOWING_USER_ID) references "USER",
    constraint USER_RELATIONSHIP_USER_ID_FK_2
        foreign key (FOLLOWED_USER_ID) references "USER"
);

