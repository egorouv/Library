-- 1. Получить список читателей с заданными характеристиками: студентов указанного
--    учебного заведения, факультета, научных работников по определенной тематике и т.д.
select *
from READERS
where id in (select id
             from STUDENT
             where university = 'Новосибирский Государственный Университет');

-- 2. Выдать перечень читателей, на руках у которых находится указанное произведение
select r.*
from READERS r
join ISSUANCE i on r.id = i.reader
join PUBLICATION p on i.publication = p.id
join PUBLICATION_DESCRIPTION pd on p.description = pd.id
join PUBLICATION_TITLE pt on pd.title = pt.id
where pt.title = 'Лолита';

-- 3. Получить список читателей, на руках у которых находится указанное издание (книга, журнал и т.д).
select r.*
from READERS r
join ISSUANCE i on r.id = i.reader
join PUBLICATION p on i.publication = p.id
join PUBLICATION_DESCRIPTION pd on p.description = pd.id
join PUBLICATION_TYPE pt on pd.type = pt.id
where pt.type = 'роман';

-- 4. Получить перечень читателей, которые в течение указанного промежутка времени
--    получали издание с некоторым произведением, и название этого издания.
select r.*, pt.title
from READERS r
join ISSUANCE i on r.id = i.reader
join PUBLICATION p on i.publication = p.id
join PUBLICATION_DESCRIPTION pd on p.description = pd.id
join PUBLICATION_TITLE pt on pd.title = pt.id
where i.issuance_date between '2023-01-01' and '2025-01-01';

-- 5. Выдать список изданий, которые в течение некоторого времени получал указанный
--    читатель из фонда библиотеки, где он зарегистрирован.
select pt.title
from PUBLICATION_TITLE pt
join PUBLICATION_DESCRIPTION pd on pt.id = pd.title
join PUBLICATION p on pd.id = p.description
join ISSUANCE i on p.id = i.publication
join READERS r on i.reader = r.id
join LIBRARY_WORKERS lw on i.worker = lw.id
join READING_ROOM rr on lw.work_place = rr.id
where r.id = '1'
and i.issuance_date between '2023-01-01' and '2025-01-01'
and r.library = rr.library;

-- 6. Получить перечень изданий, которыми в течение некоторого времени пользовался
--    указанный читатель из фонда библиотеки, где он не зарегистрирован.
select pt.title
from PUBLICATION_TITLE pt
join PUBLICATION_DESCRIPTION pd on pt.id = pd.title
join PUBLICATION p on pd.id = p.description
join ISSUANCE i on p.id = i.publication
join READERS r on i.reader = r.id
join LIBRARY_WORKERS lw on i.worker = lw.id
join READING_ROOM rr on lw.work_place = rr.id
where r.id = '1'
and i.issuance_date between '2023-01-01' and '2025-01-01'
and r.library <> rr.library;

-- 7. Получить список литературы, которая в настоящий момент выдана с определенной полки некоторой библиотеки.
select pt.title
from PUBLICATION_TITLE pt
join PUBLICATION_DESCRIPTION pd on pt.id = pd.title
join PUBLICATION p on pd.id = p.description
join LOCATION_SHELF ls on p.location = ls.id
join LOCATION_RACK lr on ls.location_rack = lr.id
join LOCATION_HALL lh on lr.location_hall = lh.id
where lh.library = '1'
and ls.location_shelf = '2';

-- 8. Выдать список читателей, которые в течение обозначенного периода были обслужены указанным библиотекарем.
select distinct r.*
from READERS r
join ISSUANCE i on r.id = i.reader
join LIBRARY_WORKERS lw on i.worker = lw.id
where lw.id = '1'
and i.issuance_date between '2023-01-01' and '2025-01-01';

-- 9. Получить данные о выработке библиотекарей (число обслуженных читателей в указанный период времени).
select lw.*, count(i.id) as issuance_count
from ISSUANCE i
join LIBRARY_WORKERS lw on i.worker = lw.id
where i.issuance_date between '2023-01-01' and '2025-01-01'
group by lw.id;

-- 10. Получить список читателей с просроченным сроком литературы
select distinct r.*
from READERS r
join ISSUANCE i on r.id = i.reader
where i.return_date < current_date and i.actual_return_date is null;

-- 11. Получить перечень указанной литературы, которая поступила (была списана) в течение некоторого периода.
select pt.title
from PUBLICATION_TITLE pt
join PUBLICATION_DESCRIPTION pd on pt.id = pd.title
join PUBLICATION p on pd.id = p.description
where p.id in (select publication
               from REPLENISHMENT
               where replenishment_date between '2024-01-01' and '2025-01-01');

select pt.title
from PUBLICATION_TITLE pt
join PUBLICATION_DESCRIPTION pd on pt.id = pd.title
join PUBLICATION p on pd.id = p.description
where p.id in (select publication
               from WRITE_OFF
               where write_off_date between '2024-01-01' and '2025-01-01');

-- 12. Выдать список библиотекарей, работающих в указанном читальном зале некоторой библиотеки
select lw.*
from LIBRARY_WORKERS lw
join READING_ROOM rr on lw.work_place = rr.id
where rr.room = '1'
and rr.library = '1';

-- 13. Получить список читателей, не посещавших библиотеку в течение указанного времени
select *
from READERS
where id not in (select reader
                 from ISSUANCE
                 where issuance_date between '2024-01-01' and '2024-01-07');

-- 14. Получить список инвентарных номеров и названий из библиотечного фонда, в которых
--     содержится указанное произведение.
select p.id as inventory_number
from PUBLICATION p
join PUBLICATION_DESCRIPTION pd on p.description = pd.id
join PUBLICATION_TITLE pt on pd.title = pt.id
where pt.title = 'Война и мир';

-- 15. Выдать список инвентарных номеров и названий из библиотечного фонда, в которых
--     содержатся произведения указанного автора.
select p.id as inventory_number
from PUBLICATION p
join PUBLICATION_DESCRIPTION pd on p.description = pd.id
join AUTHORS a on pd.author = a.id
where a.author_name = 'Чарльз' and a.author_surname = 'Дарвин';

-- 16. Получить список самых популярных произведений.
select pt.title as publication_title, count(i.id) as issuance_count
from ISSUANCE i
join PUBLICATION p on i.publication = p.id
join PUBLICATION_DESCRIPTION pd on p.description = pd.id
join PUBLICATION_TITLE pt on pd.title = pt.id
group by pt.title
order by issuance_count desc;