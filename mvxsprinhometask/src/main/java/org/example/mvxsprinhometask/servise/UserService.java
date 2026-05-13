package org.example.mvxsprinhometask.servise;

import jakarta.transaction.Transactional;
import org.example.mvxsprinhometask.entity.User;
import org.example.mvxsprinhometask.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(Long userId) {
        if(userId == null || userId < 0) {
            throw new IllegalArgumentException("bad userId");
        }
        return userRepository.findById(userId).orElse(null);
    }

    public Page<User> getAllUsers(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size));
    }

    public User create(User user) {
        if(user == null || user.getEmail() == null || user.getUsername() == null) {
            throw new IllegalArgumentException("bad user data");
        }
        return userRepository.save(user);
    }

    public void remove(Long userId) {
        if(userId == null || userId < 0) {
            throw new IllegalArgumentException("bad userId");
        }
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
    }

    public User update(Long id, User newUserData) {
        if(id == null || newUserData == null || newUserData.getEmail() == null || newUserData.getUsername() == null) {
            throw new IllegalArgumentException("bad user data to update");
        }

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        existingUser.setUsername(newUserData.getUsername());
        existingUser.setEmail(newUserData.getEmail());

        return userRepository.save(existingUser);
    }
}
