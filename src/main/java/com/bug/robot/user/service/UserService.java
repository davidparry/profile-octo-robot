package com.bug.robot.user.service;

import com.bug.robot.common.exception.UserNotFoundException;
import com.bug.robot.user.domain.User;
import com.bug.robot.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public List<User> getAll() {
        return repository.findAll();
    }

    public Optional<User> getById(Long id) {
        return repository.findById(id);
    }

    public User getUserByIdOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    public User create(User user) {
        return repository.save(user);
    }

    public boolean existsById(Long id) {
        return repository.existsById(id);
    }
}
