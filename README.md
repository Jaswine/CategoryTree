
# Category Tree Bot


## 🎈 Команды 

Отображает дерево категорий в структурированном виде:

> /viewTree


Создание элемента:

> /addElement/<название элемента>

Добавление детей к элементу:

> /addElement/<родительский элемент>/<дочерний элемент>

Удаление элемента:

> /removeElement/<название элемента>

Выводит список всех доступных команд и краткое их описание:

> /help

Скачивает Excel документ с деревом категорий:

> /download

Для приобретения/уничтожения прав администратора:

> /changeUserStatus

## 🏁 Запуск проекта 


### Установка и запуск на Ubuntu

Установка зависимостей:

> sudo apt update

> sudo apt install openjdk-17-jdk

> sudo apt install maven

Запуск проекта:

> mvn spring-boot:run

### Установка и запуск с помощью Dockerfile

> docker-compose up --build