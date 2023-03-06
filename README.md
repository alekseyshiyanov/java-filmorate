# java-filmorate

### ER диаграмма

![Filmorate ERD.png](Filmorate%20ERD.png)

### Примеры запросов

#### Список общих друзей для пользователей с идентификаторами 1 и 2
```sql
SELECT * 
FROM USER AS u 
WHERE u.ID IN   ( 
                    SELECT f_0.USER_TO 
                    FROM FRIENDS AS f_0 
                    INNER JOIN FRIENDS AS f_1 ON f_0.USER_TO = f_1.USER_TO 
                    WHERE (f_0.USER_FROM = 1) AND (f_1.USER_FROM = 2) 
                );
```
#### Список друзей для пользователя с идентификатором 1
```sql
SELECT * 
FROM USER AS u 
INNER JOIN FRIENDS AS f ON u.ID = f.User_To 
WHERE f.User_From = 1;
```
#### Получение списка фильмов
```sql
SELECT * FROM FILM;
```
