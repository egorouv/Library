package ru.nsu.egorov.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.nsu.egorov.models.Readers;

import java.util.List;

@Repository
public interface ReadersRepository extends JpaRepository<Readers, Long> {
    @Query("select id " +
            "from READERS " +
            "where id in (select id " +
            "from STUDENT " +
            "where university = 'Новосибирский Государственный Университет');")
    List<Readers> findReadersByFeature();
}
