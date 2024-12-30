package vn.hoidanit.laptopshop.service;

import java.util.List;

import org.springframework.stereotype.Service;

import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.repository.UserRepository;

@Service
public class UserService {
    public final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        // findAll đc lấy từ UserRepository kế thừa từ JpaRepository
        return this.userRepository.findAll();
    }

    public List<User> getAllUsersByEmail(String email) {
        // find by email đc lấy từ UserRepository
        return this.userRepository.findByEmail(email);
    }

    public User handelSaveUser(User user) {
        User eric = this.userRepository.save(user);
        System.out.println(eric);
        return eric;
    }

    public User getUserById(Long id) {
        User user = this.userRepository.findById(id).orElse(null);
        return user;
    }
}
