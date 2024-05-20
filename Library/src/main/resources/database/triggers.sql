-- Процедура для добавления новых книг в каталог.
CREATE OR REPLACE PROCEDURE add_new_books(
    IN new_location_id INTEGER,
    IN new_description_id INTEGER,
    IN new_allow_take BOOLEAN,
    IN new_write_off BOOLEAN
)
AS $$
BEGIN
    INSERT INTO PUBLICATION (location, description, allow_take, write_off)
    VALUES (new_location_id, new_description_id, new_allow_take, new_write_off);
END;
$$ LANGUAGE plpgsql;

-- Процедура для добавления новых читателей.
CREATE OR REPLACE PROCEDURE add_new_reader(
    IN new_name VARCHAR(50),
    IN new_surname VARCHAR(50),
    IN new_birth_date DATE,
    IN new_library_id INTEGER,
    IN new_category_id INTEGER
)
AS $$
BEGIN
    INSERT INTO READERS (reader_name, reader_surname, birth_date, library, category)
    VALUES (new_name, new_surname, new_birth_date, new_library_id, new_category_id);
END;
$$ LANGUAGE plpgsql;

-- Процедура для добавления новых библиотекарей.
CREATE OR REPLACE PROCEDURE add_new_worker(
    IN new_name VARCHAR(50),
    IN new_surname VARCHAR(50),
    IN new_birth_date DATE,
    IN new_work_place INTEGER
)
AS $$
BEGIN
    INSERT INTO LIBRARY_WORKERS (worker_name, worker_surname, birth_date, work_place)
    VALUES (new_name, new_surname, new_birth_date, new_work_place);
END;
$$ LANGUAGE plpgsql;

-- Процедура для выдачи списка просроченных книг.
CREATE OR REPLACE PROCEDURE list_overdue_books()
AS $$
BEGIN
    SELECT *
    FROM ISSUANCE
    WHERE return_date < CURRENT_DATE AND actual_return_date IS NULL;
END;
$$ LANGUAGE plpgsql;

-- Процедура для обновления даты возврата книги.
CREATE OR REPLACE PROCEDURE update_actual_return_date(
    issuance_id INT
)
AS $$
BEGIN
    UPDATE ISSUANCE
    SET actual_return_date = CURRENT_DATE
    WHERE id = issuance_id;
END;
$$ LANGUAGE plpgsql;

-- Триггер для контроля ограничения количества выдаваемых книг.
CREATE OR REPLACE FUNCTION check_issuance_limit()
RETURNS TRIGGER AS
$$
DECLARE
    issuance_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO issuance_count
    FROM ISSUANCE
    WHERE reader = NEW.reader;

    IF issuance_count >= 5 THEN
        RAISE EXCEPTION 'Превышен лимит на количество книг для выдачи';
    END IF;

    RETURN NULL;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER issuance_limit_trigger
BEFORE INSERT ON ISSUANCE
FOR EACH ROW
EXECUTE FUNCTION check_issuance_limit();

-- Триггер для оповещения о просроченных выдачах.
CREATE OR REPLACE FUNCTION notify_overdue_issuance()
RETURNS TRIGGER AS
$$
BEGIN
    IF NEW.return_date < CURRENT_DATE THEN
        RAISE EXCEPTION 'Просрочен срок возврата книги.';
    END IF;
    RETURN NULL;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER overdue_issuance_trigger
BEFORE INSERT OR UPDATE ON ISSUANCE
FOR EACH ROW
EXECUTE FUNCTION notify_overdue_issuance();

-- Триггер для изменения статуса списанной книги.
CREATE OR REPLACE FUNCTION update_write_off_status()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE PUBLICATION
    SET write_off = TRUE
    WHERE id = NEW.publication;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_write_off_status
AFTER INSERT ON WRITE_OFF
FOR EACH ROW
EXECUTE FUNCTION update_write_off_status();

-- Триггер, который проверяет, можно ли списать книгу.
CREATE OR REPLACE FUNCTION prevent_write_off()
RETURNS TRIGGER AS $$
BEGIN
    IF EXISTS (SELECT 1 FROM ISSUANCE WHERE publication = NEW.publication AND actual_return_date IS NULL) THEN
        RAISE EXCEPTION 'Нельзя списать книгу, которая выдана читателю';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_prevent_write_off
BEFORE INSERT ON WRITE_OFF
FOR EACH ROW
EXECUTE FUNCTION prevent_write_off();

-- Триггер для изменения статуса поступившей книги.
CREATE OR REPLACE FUNCTION update_replenishment_status()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE PUBLICATION
    SET write_off = FALSE
    WHERE id = NEW.publication;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_replenishment_status
AFTER INSERT ON REPLENISHMENT
FOR EACH ROW
EXECUTE FUNCTION update_replenishment_status();

-- Триггер для удаления читателя из таблицы категорий при удалении из общей.
CREATE OR REPLACE FUNCTION delete_reader_categories()
RETURNS TRIGGER AS $$
BEGIN
    DELETE FROM STUDENT WHERE id = OLD.id;
    DELETE FROM SCHOOLBOY WHERE id = OLD.id;
    DELETE FROM TEACHER WHERE id = OLD.id;
    DELETE FROM RESEARCHER WHERE id = OLD.id;
    DELETE FROM WORKER WHERE id = OLD.id;
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_delete_reader_categories
AFTER DELETE ON READERS
FOR EACH ROW
EXECUTE FUNCTION delete_reader_categories();

---------------------------------------------------------------------------------------------

-- Один читатель не может быть записан сразу в двух категориях.
CREATE OR REPLACE FUNCTION check_single_category()
RETURNS TRIGGER AS $$
BEGIN
    IF (NEW.id IS NOT NULL AND (
        (EXISTS (SELECT 1 FROM STUDENT WHERE id = NEW.id)) OR
        (EXISTS (SELECT 1 FROM SCHOOLBOY WHERE id = NEW.id)) OR
        (EXISTS (SELECT 1 FROM TEACHER WHERE id = NEW.id)) OR
        (EXISTS (SELECT 1 FROM RESEARCHER WHERE id = NEW.id)) OR
        (EXISTS (SELECT 1 FROM WORKER WHERE id = NEW.id))
    )) THEN
        RAISE EXCEPTION 'Читатель уже существует в одной из категорий';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_check_single_category_student
BEFORE INSERT ON STUDENT
FOR EACH ROW
EXECUTE FUNCTION check_single_category();

-- Человек, находящийся в таблице категории, должен также находиться в общей таблице.
CREATE OR REPLACE FUNCTION check_reader_exists()
RETURNS TRIGGER AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM READERS WHERE id = NEW.id) THEN
        RAISE EXCEPTION 'Читатель должен существовать в общей таблице';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_check_reader_exists_student
BEFORE INSERT ON STUDENT
FOR EACH ROW
EXECUTE FUNCTION check_reader_exists();

-- Категория читателя не должна изменяться.
CREATE OR REPLACE FUNCTION prevent_category_update()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.category <> NEW.category THEN
        RAISE EXCEPTION 'Категория читателя не должна изменяться';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_prevent_category_update
BEFORE UPDATE ON READERS
FOR EACH ROW
EXECUTE FUNCTION prevent_category_update();

--
CREATE OR REPLACE FUNCTION prevent_category_change()
RETURNS TRIGGER AS $$
BEGIN
    RAISE EXCEPTION 'Категория читателя не должна изменяться';
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_prevent_update_student
BEFORE UPDATE ON STUDENT
FOR EACH ROW
EXECUTE FUNCTION prevent_category_change();

-- Книга должна выдаваться из библиотеки, в которой работает сотрудник библиотеки, обслуживающий читателя.
CREATE OR REPLACE FUNCTION check_library_match()
RETURNS TRIGGER AS $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM LIBRARY l
        JOIN READING_ROOM rr ON l.id = rr.library
        JOIN LIBRARY_WORKERS lw ON rr.id = lw.work_place
        WHERE lw.id = NEW.worker AND l.id = (SELECT library FROM READERS WHERE id = NEW.reader)
    ) THEN
        RAISE EXCEPTION 'Книга должна выдаваться из библиотеки, в которой работает сотрудник библиотеки, обслуживающий читателя';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_check_library_match
BEFORE INSERT ON ISSUANCE
FOR EACH ROW
EXECUTE FUNCTION check_library_match();

-- Книга должна быть доступна для выдачи, т.е. не должна быть уже выдана другому читателю.
CREATE OR REPLACE FUNCTION check_book_availability()
RETURNS TRIGGER AS $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM ISSUANCE
        WHERE publication = NEW.publication AND actual_return_date IS NULL OR write_off = TRUE
    ) THEN
        RAISE EXCEPTION 'Книга уже выдана другому читателю';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_check_book_availability
BEFORE INSERT ON ISSUANCE
FOR EACH ROW
EXECUTE FUNCTION check_book_availability();

-- Нельзя списать выданную книгу.
CREATE OR REPLACE FUNCTION prevent_write_off_issued_book()
RETURNS TRIGGER AS $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM ISSUANCE
        WHERE publication = NEW.publication AND actual_return_date IS NULL
    ) THEN
        RAISE EXCEPTION 'Нельзя списать книгу, которая выдана читателю';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_prevent_write_off_issued_book
BEFORE INSERT ON WRITE_OFF
FOR EACH ROW
EXECUTE FUNCTION prevent_write_off_issued_book();

-- Пополнение и списание должно проводиться сотрудником, который работает в этой библиотеке.
CREATE OR REPLACE FUNCTION check_worker_library_match()
RETURNS TRIGGER AS $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM PUBLICATION p
        JOIN LOCATION_SHELF ls ON p.location = ls.id
        JOIN LOCATION_RACK lr ON ls.location_rack = lr.id
        JOIN LOCATION_HALL lh ON lr.location_hall = lh.id
        JOIN LIBRARY l ON lh.library = l.id
        JOIN READING_ROOM rr ON l.id = rr.library
        JOIN LIBRARY_WORKERS lw ON rr.id = lw.work_place
        WHERE lw.id = NEW.library_worker
        AND p.id = NEW.publication
    ) THEN
        RAISE EXCEPTION 'Пополнение или списание должно проводиться сотрудником, который работает в этой библиотеке';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_check_worker_library_match_replenishment
BEFORE INSERT ON REPLENISHMENT
FOR EACH ROW
EXECUTE FUNCTION check_worker_library_match();

CREATE TRIGGER trigger_check_worker_library_match_write_off
BEFORE INSERT ON WRITE_OFF
FOR EACH ROW
EXECUTE FUNCTION check_worker_library_match();

--
CREATE OR REPLACE FUNCTION check_category_correctness()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_TABLE_NAME <> (SELECT c.category FROM readers r JOIN category c on r.category = c.id) THEN
        RAISE EXCEPTION 'Несовпадение категорий';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER before_insert_or_update_on_student
BEFORE INSERT OR UPDATE ON student
FOR EACH ROW
EXECUTE FUNCTION check_category_correctness();
