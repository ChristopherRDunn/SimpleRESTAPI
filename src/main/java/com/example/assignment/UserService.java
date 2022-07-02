package com.example.assignment;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Manages communication with the RestRepo
 */
@Service
public class UserService {
    private final RestRepo restRepo;

    public UserService(RestRepo restRepo) {
        this.restRepo = restRepo;
    }

    public List<UserData> findAll() {
        var it = restRepo.findAll();

        var users = new ArrayList<UserData>();
        it.forEach(e -> users.add(e));

        return users;
    }

    public Long count() {

        return restRepo.count();
    }

    public Optional<UserData> findById(Long userId) {
        return restRepo.findById(userId);
    }

    public void save(UserData userData) {
        restRepo.save(userData);
    }

    public void deleteById(Long userId) {

        restRepo.deleteById(userId);
    }
}
