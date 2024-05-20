INSERT INTO LIBRARY (library_name, address) VALUES
    ('Новосибирская государственная областная научная библиотека', 'ул. Советская, 6, Новосибирск, Новосибирская обл., 630007'),
    ('Новосибирская областная юношеская библиотека', 'Красный пр-т., 26, Новосибирск, Новосибирская обл., 630099'),
    ('Центральная городская библиотека им. К. Маркса', 'Красный пр-т., 163, Новосибирск, Новосибирская обл., 630049');

INSERT INTO CATEGORY (category) VALUES
    ('студент'),
    ('школьник'),
    ('преподаватель'),
    ('научный сотрудник'),
    ('рабочий');

INSERT INTO READERS (reader_name, reader_surname, birth_date, library, category) VALUES
    ('Сергей', 'Родин', '2004-09-10', 1, 1),
    ('Александр', 'Семенов', '2010-05-12', 1, 2),
    ('Ольга', 'Иванова', '1988-07-28', 3, 3),
    ('Борис', 'Вихров', '1974-11-01', 2, 4),
    ('Илья', 'Сидоров', '1990-02-15', 2, 5);

INSERT INTO READING_ROOM (library, room) VALUES
    (1, 1),
    (1, 2),
    (1, 3),
    (2, 1),
    (2, 2),
    (2, 3),
    (3, 1),
    (3, 2),
    (3, 3);

INSERT INTO LIBRARY_WORKERS (worker_name, worker_surname, birth_date, work_place) VALUES
    ('Анастасия', 'Быстрова', '2000-04-08', 1),
    ('Анна', 'Рыкова', '1981-08-10', 5),
    ('Юлия', 'Панова', '1975-06-20', 9);

INSERT INTO PUBLICATION_TITLE (title) VALUES
    ('Война и мир'),
    ('Лолита'),
    ('Путешествие Гулливера'),
    ('О происхождении видов'),
    ('Винни-Пух'),
    ('Приключения Гекльберри Финна'),
    ('Анна Каренина'),
    ('Родной сын'),
    ('Гамлет'),
    ('Десять негритят');

INSERT INTO AUTHORS (author_name, author_surname) VALUES
    ('Лев', 'Толстой'),
    ('Владимир', 'Набоков'),
    ('Джонатан', 'Свифт'),
    ('Чарльз', 'Дарвин'),
    ('Алан', 'Милн'),
    ('Марк', 'Твен'),
    ('Ричард', 'Райт'),
    ('Уильям', 'Шекспир'),
    ('Агата', 'Кристи');

INSERT INTO PUBLICATION_TYPE (type) VALUES
    ('учебник'),
    ('повесть'),
    ('роман'),
    ('статья'),
    ('стихи'),
    ('диссертация'),
    ('реферат');

INSERT INTO PUBLICATION_DESCRIPTION (title, author, type) VALUES
    (1, 1, 3),
    (2, 2, 3),
    (3, 3, 2),
    (4, 4, 1),
    (5, 5, 2),
    (6, 6, 2),
    (7, 1, 3),
    (8, 7, 3),
    (9, 8, 5),
    (10, 9, 2);

INSERT INTO LOCATION_HALL (library, location_hall) VALUES
    (1, 1),
    (1, 2),
    (1, 3),
    (2, 1),
    (2, 2),
    (2, 3),
    (3, 1),
    (3, 2),
    (3, 3);

INSERT INTO LOCATION_RACK (location_hall, location_rack) VALUES
    (1, 1),
    (1, 2),
    (1, 3),
    (4, 1),
    (4, 2),
    (4, 3),
    (7, 1),
    (7, 2),
    (7, 3);

INSERT INTO LOCATION_SHELF (location_rack, location_shelf) VALUES
    (1, 1),
    (1, 2),
    (1, 3),
    (4, 1),
    (4, 2),
    (4, 3),
    (7, 1),
    (7, 2),
    (7, 3);

INSERT INTO PUBLICATION (location, description, allow_take, write_off) VALUES
    (1, 1, true, false),
    (2, 2, true, false),
    (3, 3, true, false),
    (4, 4, true, false),
    (5, 5, true, false),
    (6, 6, true, false);

INSERT INTO ISSUANCE (reader, worker, publication, issuance_date, return_date, actual_return_date) VALUES
    (1, 1, 1, '2024-04-30', '2024-05-30', null),
    (2, 1, 2, '2024-04-30', '2024-05-30', null),
    (3, 2, 3, '2024-03-30', '2024-04-30', '2024-05-02');

INSERT INTO REPLENISHMENT (replenishment_date, library_worker, publication) VALUES
    ('2024-03-30', 3, 5);

INSERT INTO WRITE_OFF (write_off_date, library_worker, publication) VALUES
    ('2024-04-30', 3, 6);

INSERT INTO STUDENT (id, university, course, student_group) VALUES
    (1, 'Новосибирский Государственный Университет', 2, 22705);

INSERT INTO SCHOOLBOY (id, school, class) VALUES
    (2, 'МБОУ СОШ №163', '8В');

INSERT INTO TEACHER (id, place_of_work, job_title) VALUES
    (3, 'МБОУ СОШ №163', 'Преподаватель литературы');

INSERT INTO RESEARCHER (id, organization, topic) VALUES
    (4, 'ФИЦ ИВТ', 'Автоматизация вычислений');

INSERT INTO WORKER (id, place_of_work, job_title) VALUES
    (5, 'ФИЦ ИВТ', 'Заведующий лабораторией');
