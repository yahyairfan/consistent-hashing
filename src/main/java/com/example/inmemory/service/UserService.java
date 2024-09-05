package com.example.inmemory.service;

import com.example.inmemory.dao.InMemoryDBRepository;
import com.example.inmemory.dao.entity.User;
import com.example.inmemory.service.dto.UserDTO;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final InMemoryDBRepository inMemoryDBRepository;

    public UserService(InMemoryDBRepository inMemoryDBRepository) {
        this.inMemoryDBRepository = inMemoryDBRepository;
    }

    public UserDTO getUser(String username) {
        return inMemoryDBRepository.getUser(username);
    }

    public void saveUser(User user) {
        inMemoryDBRepository.saveUser(user);
    }
}
