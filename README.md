# Card game test task

Api и их описание:

1. /user/registration POST - регистрация пользователя.
```
{
    "login": "{login}",
    "password": "{password}",
    "name": "{name}"
}
```
2. /user/login POST - аутентификация пользователя.
```
{
    "login": "{login}",
    "password": "{password}"
}
```
3. /game-session POST - создание игровой сессии.
4. /game-session/enter/{sessionId} POST - присоединение к игровой сессии.
5. /game-session/start/{sessionId} POST - запустить игровую сессию.
6. /game-session/{sessionId} GET - получить информацию об игровой сессии.
7. /turn/current/info/session/{sessionId} GET - получить информацию о ходе для текущего игрока(доступно лишь игроку, чей сейчас ход)
8. /turn/session/{sessionId}?targetUserId={targetUserId} POST - сделать ход(доступно лишь игроку, чей сейчас ход)

Для авторизации используется JWT, который требуется передать в заголовке запроса в следующем формате:
```
    Authorization: Bearer your_token
```

[Ссылка](card-game-collection.json) на postman-коллекцию

Для запуска приложения, если использовать докер, то:
1. Зайти в папку [dockerfiles](Dockerfiles ) через консоль
2. Запустить команду docker-compose up


<ul>
<li>Бд будет доступно по http://localhost:5222</li>

<li>Приложение по http://localhost:8000</li> 

</ul>

Если что-то пошло не так, то стоит обратить внимание не заняты ли внешние порты другими приложениями и изменить их. Либо же в файле [.env](/Dockerfiles/.env) 
поменять SPRING_DATASOURCE_URL="jdbc:postgresql://host.docker.internal:5222/card_game" на SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5222/card_game"

Чтобы запуститься без докера, нужно зайти в консоль postgres и запустить [init.sql](Dockerfiles/init.sql)
<br>После чего установить переменные окружения взяв их из файла [.env](Dockerfiles/.env) <li>DATASOURCE_USERNAME, DATASOURCE_PASSWORD будут соответствовать SPRING_DATASOURCE_USERNAME, SPRING_DATASOURCE_PASSWORD
<br>Запуститься. Приложение будет доступно по localhost:8080