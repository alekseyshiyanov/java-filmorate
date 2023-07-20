--заполняем таблицу Genre
MERGE INTO Genre (Genre_ID, Name, Description)
VALUES
(1, 'Комедия', ''),
(2, 'Драма', ''),
(3, 'Мультфильм', ''),
(4, 'Триллер', ''),
(5, 'Документальный', ''),
(6, 'Боевик', '');

--заполняем таблицу MpaRating
MERGE INTO MPA (Mpa_Id, Name, Description)
VALUES
(1, 'G', 'Нет возрастных ограничений'),
(2, 'PG', 'Детям рекомендуется смотреть фильм с родителями'),
(3, 'PG-13', 'Детям до 13 лет просмотр не желателен'),
(4, 'R', 'Лицам до 17 лет просматривать фильм можно только в присутствии взрослого'),
(5, 'NC-17', 'Лицам до 18 лет просмотр запрещён');

--заполняем таблицу User
MERGE INTO User (User_ID, Login, Name, Email, Birthday)
VALUES
(1, 'user@1975', 'Ivan Ivanov', 'user_Ivan@mail.ru', '1975-09-19'),
(2, 'petunya@1986', 'Petr Petrov', 'user_Petr@mail.ru', '1986-01-28'),
(3, 'galya@1951', 'Galina Medvedeva', 'user_Galya@gmail.com', '1951-08-30'),
(4, 'alex@1951', 'Aleksandr Sch', 'user_Sanya@gmail.com', '1955-08-16'),
(5, 'irina@1980', 'Irina V', 'user_V@gmail.com', '1980-09-19'),
(6, 'sidor@1989', 'Sidor Petrov', 'user_ginPetr@mail.ru', '1989-01-28');

--заполняем таблицу Film
MERGE INTO Film (Film_ID, Name, MPA_Rating, Description, ReleaseDate, Duration, LikesCount)
VALUES
(1, 'Чебурашка', 1, 'Чебурашка!!!', '2023-01-01', 113, 0),
(2, 'О чём говорят мужчины', 4, 'О чем говорят мужчины? Конечно, о женщинах.', '2010-03-04', 93, 0),
(3, 'Я — легенда', 3, 'Неизвестный вирус унёс жизни половины населения земного шара', '2007-12-05', 96, 0),
(4, 'Брат 2', 4, 'Данила Багров вылетает в Америку', '2000-05-11', 127, 0),
(5, 'Зеленая миля', 4, 'Гигант Джон Коффи, обвинённый в страшном преступлении, стал одним из самых необычных обитателей блока смертников', '1999-12-06', 189, 0);
