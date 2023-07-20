# java-filmorate

### ER диаграмма

![Filmorate ERD.png](Filmorate%20ERD.png)

### Примеры запросов

#### Список общих друзей для пользователей с идентификаторами 1 и 2
```sql
SELECT u.*
FROM USER u, FRIENDS f_0, FRIENDS f_1
WHERE (f_0.USER_TO = f_1.USER_TO) AND (u.User_ID = f_0.USER_TO)
    AND (f_0.USER_FROM = 1) AND (f_1.USER_FROM = 2);
```
#### Список друзей для пользователя с идентификатором 1
```sql
SELECT u.*
FROM USER AS u, FRIENDS f
WHERE (u.User_ID = f.User_To) AND (f.User_From = 1);
```
#### Получение списка фильмов
```sql
SELECT * FROM FILM;
```
