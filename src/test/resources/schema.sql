SET MODE MYSQL;

CREATE TABLE IF NOT EXISTS Genre (
                                     ID          IDENTITY    PRIMARY KEY,
                                     Name        varchar     NOT NULL UNIQUE,
                                     Description varchar
);

CREATE TABLE IF NOT EXISTS Film (
                                    ID          IDENTITY    PRIMARY KEY,
                                    Name        varchar     NOT NULL UNIQUE,
                                    MPA_Rating  int,
                                    Description varchar,
                                    ReleaseDate date        NOT NULL,
                                    Duration    int         DEFAULT 0,
                                    LikesCount  int         DEFAULT 0
);

CREATE TABLE IF NOT EXISTS FilmLikes (
                                         UserID      int         NOT NULL,
                                         FilmID      int         NOT NULL,
                                         PRIMARY KEY(UserID, FilmID)
);

CREATE TABLE IF NOT EXISTS User (
                                    ID          IDENTITY    PRIMARY KEY,
                                    Login       varchar     NOT NULL UNIQUE,
                                    Name        varchar,
                                    Email       varchar     NOT NULL UNIQUE,
                                    Birthday    date
);

CREATE TABLE IF NOT EXISTS Friends (
                                       User_From   int         NOT NULL,
                                       User_To     int         NOT NULL,
                                       Status      int         DEFAULT 0,
                                       PRIMARY KEY(User_From, User_To)
);

CREATE TABLE IF NOT EXISTS MPA (
                                   ID          IDENTITY    PRIMARY KEY,
                                   Name        varchar     NOT NULL UNIQUE,
                                   Description varchar
);

CREATE TABLE IF NOT EXISTS FilmGenres (
                                          FilmID      int         NOT NULL,
                                          GenreID     int         NOT NULL,
                                          PRIMARY KEY(FilmID, GenreID)
);

ALTER TABLE FilmGenres  ADD CONSTRAINT IF NOT EXISTS fk_FilmGenres_FilmID   FOREIGN KEY(FilmID)       REFERENCES Film (ID);

ALTER TABLE FilmGenres  ADD CONSTRAINT IF NOT EXISTS fk_FilmGenres_GenreID  FOREIGN KEY(GenreID)      REFERENCES Genre (ID);

ALTER TABLE Film        ADD CONSTRAINT IF NOT EXISTS fk_MPA_Rating          FOREIGN KEY(MPA_Rating)   REFERENCES MPA (ID);

ALTER TABLE FilmLikes   ADD CONSTRAINT IF NOT EXISTS fk_FilmLikes_UserID    FOREIGN KEY(UserID)       REFERENCES User (ID);

ALTER TABLE FilmLikes   ADD CONSTRAINT IF NOT EXISTS fk_FilmLikes_FilmID    FOREIGN KEY(FilmID)       REFERENCES Film (ID);

ALTER TABLE Friends     ADD CONSTRAINT IF NOT EXISTS fk_Friends_User_From   FOREIGN KEY(User_From)    REFERENCES User (ID);

ALTER TABLE Friends     ADD CONSTRAINT IF NOT EXISTS fk_Friends_User_To     FOREIGN KEY(User_To)      REFERENCES User (ID);

ALTER TABLE Film        ADD CONSTRAINT IF NOT EXISTS only_Positive_Duration CHECK (Duration >= 0);