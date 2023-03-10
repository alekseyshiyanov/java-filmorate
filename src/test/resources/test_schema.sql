SET MODE MYSQL;

CREATE TABLE IF NOT EXISTS Genre (
                                     GenreID     IDENTITY    PRIMARY KEY,
                                     Name        varchar     NOT NULL,
                                     Description varchar(200),
                                     CONSTRAINT  unique_Genre_Name UNIQUE(Name)
);

CREATE TABLE IF NOT EXISTS Film (
                                    FilmID      IDENTITY    PRIMARY KEY,
                                    Name        varchar     NOT NULL,
                                    MPA_Rating  int,
                                    Description varchar(200),
                                    ReleaseDate date        NOT NULL,
                                    Duration    int         DEFAULT 0,
                                    LikesCount  int         DEFAULT 0,
                                    CONSTRAINT  unique_Film_Name UNIQUE(Name)
);

CREATE TABLE IF NOT EXISTS FilmLikes (
                                         UserID      int         NOT NULL,
                                         FilmID      int         NOT NULL,
                                         CONSTRAINT  pk_FilmLikes PRIMARY KEY(UserID, FilmID)
);

CREATE TABLE IF NOT EXISTS User (
                                    UserID      IDENTITY    PRIMARY KEY,
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
                                   MpaId       IDENTITY    PRIMARY KEY,
                                   Name        varchar     NOT NULL,
                                   Description varchar(200),
                                   CONSTRAINT  unique_MPA_Name UNIQUE(Name)
);

CREATE TABLE IF NOT EXISTS FilmGenres (
                                          FilmID      int         NOT NULL,
                                          GenreID     int         NOT NULL,
                                          CONSTRAINT  pk_FilmGenres PRIMARY KEY(FilmID, GenreID)
);

ALTER TABLE Film        ADD CONSTRAINT IF NOT EXISTS fk_MPA_Rating          FOREIGN KEY(MPA_Rating)   REFERENCES MPA (MpaId);
ALTER TABLE Film        ADD CONSTRAINT IF NOT EXISTS only_Positive_Duration CHECK (Duration >= 0);

ALTER TABLE FilmGenres  ADD CONSTRAINT IF NOT EXISTS fk_FilmGenres_FilmID   FOREIGN KEY(FilmID)       REFERENCES Film (FilmID);
ALTER TABLE FilmGenres  ADD CONSTRAINT IF NOT EXISTS fk_FilmGenres_GenreID  FOREIGN KEY(GenreID)      REFERENCES Genre (GenreID);

ALTER TABLE FilmLikes   ADD CONSTRAINT IF NOT EXISTS fk_FilmLikes_UserID    FOREIGN KEY(UserID)       REFERENCES User (UserID);
ALTER TABLE FilmLikes   ADD CONSTRAINT IF NOT EXISTS fk_FilmLikes_FilmID    FOREIGN KEY(FilmID)       REFERENCES Film (FilmID);

ALTER TABLE Friends     ADD CONSTRAINT IF NOT EXISTS fk_Friends_User_From   FOREIGN KEY(User_From)    REFERENCES User (UserID);
ALTER TABLE Friends     ADD CONSTRAINT IF NOT EXISTS fk_Friends_User_To     FOREIGN KEY(User_To)      REFERENCES User (UserID);

