SET MODE MYSQL;

CREATE TABLE IF NOT EXISTS Genre (
                                     Genre_ID    IDENTITY    PRIMARY KEY,
                                     Name        varchar     NOT NULL,
                                     Description varchar(200),
                                     CONSTRAINT  unique_Genre_Name UNIQUE(Name)
);

CREATE TABLE IF NOT EXISTS Film (
                                    Film_ID     IDENTITY    PRIMARY KEY,
                                    Name        varchar     NOT NULL,
                                    MPA_Rating  int,
                                    Description varchar(200),
                                    ReleaseDate date        NOT NULL,
                                    Duration    int         DEFAULT 0,
                                    LikesCount  int         DEFAULT 0,
                                    CONSTRAINT  unique_Film_Name UNIQUE(Name)
);

CREATE TABLE IF NOT EXISTS FilmLikes (
                                         User_ID     int         NOT NULL,
                                         Film_ID     int        NOT NULL,
                                         CONSTRAINT  pk_FilmLikes PRIMARY KEY(User_ID, Film_ID)
);

CREATE TABLE IF NOT EXISTS User (
                                    User_ID     IDENTITY    PRIMARY KEY,
                                    Login       varchar     NOT NULL,
                                    Name        varchar(100),
                                    Email       varchar     NOT NULL,
                                    Birthday    date,
                                    CONSTRAINT  unique_User_Login UNIQUE(Login),
                                    CONSTRAINT  unique_User_Email UNIQUE(Email)
);

CREATE TABLE IF NOT EXISTS Friends (
                                       User_From   int         NOT NULL,
                                       User_To     int         NOT NULL,
                                       Status      int         DEFAULT 0,
                                       CONSTRAINT  pk_Friends  PRIMARY KEY(User_From, User_To)
);

CREATE TABLE IF NOT EXISTS MPA (
                                   Mpa_Id      IDENTITY    PRIMARY KEY,
                                   Name        varchar     NOT NULL,
                                   Description varchar(200),
                                   CONSTRAINT  unique_MPA_Name UNIQUE(Name)
);

CREATE TABLE IF NOT EXISTS FilmGenres (
                                          Film_ID     int         NOT NULL,
                                          Genre_ID    int         NOT NULL,
                                          CONSTRAINT  pk_FilmGenres PRIMARY KEY(Film_ID, Genre_ID)
);

ALTER TABLE Film        ADD CONSTRAINT IF NOT EXISTS fk_MPA_Rating          FOREIGN KEY(MPA_Rating)   REFERENCES MPA (Mpa_Id);
ALTER TABLE Film        ADD CONSTRAINT IF NOT EXISTS only_Positive_Duration CHECK (Duration >= 0);

ALTER TABLE FilmGenres  ADD CONSTRAINT IF NOT EXISTS fk_FilmGenres_FilmID   FOREIGN KEY(Film_ID)      REFERENCES Film (Film_ID);
ALTER TABLE FilmGenres  ADD CONSTRAINT IF NOT EXISTS fk_FilmGenres_GenreID  FOREIGN KEY(Genre_ID)     REFERENCES Genre (Genre_ID);

ALTER TABLE FilmLikes   ADD CONSTRAINT IF NOT EXISTS fk_FilmLikes_UserID    FOREIGN KEY(User_ID)      REFERENCES User (User_ID);
ALTER TABLE FilmLikes   ADD CONSTRAINT IF NOT EXISTS fk_FilmLikes_FilmID    FOREIGN KEY(Film_ID)      REFERENCES Film (Film_ID);

ALTER TABLE Friends     ADD CONSTRAINT IF NOT EXISTS fk_Friends_User_From   FOREIGN KEY(User_From)    REFERENCES User (User_ID);
ALTER TABLE Friends     ADD CONSTRAINT IF NOT EXISTS fk_Friends_User_To     FOREIGN KEY(User_To)      REFERENCES User (User_ID);


