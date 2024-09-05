package com.example.inmemory.service;

import com.example.inmemory.dao.InMemoryDBRepository;
import org.springframework.stereotype.Service;

@Service
public class DBService {
    private final InMemoryDBRepository inMemoryDBRepository;

    public DBService(InMemoryDBRepository inMemoryDBRepository) {
        this.inMemoryDBRepository = inMemoryDBRepository;
    }

    public void addDb(String dbName) {
        inMemoryDBRepository.addDb(dbName);
    }

    public void removeDb(String dbName) {
        inMemoryDBRepository.removeDb(dbName);
    }
}
