package ru.nsu.egorov.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.nsu.egorov.models.Readers;
import ru.nsu.egorov.repositories.ReadersRepository;

import java.util.List;

@Service
public class ReadersService {

    @Autowired
    private ReadersRepository readersRepository;

    public List<Readers> getAllReaders() {
        return readersRepository.findAll();
    }

    public List<Readers> getReadersByFeature() {
        return readersRepository.findReadersByFeature();
    }

}
