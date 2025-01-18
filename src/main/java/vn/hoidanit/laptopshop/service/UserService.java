package vn.hoidanit.laptopshop.service;

import java.util.List;

import org.springframework.stereotype.Service;

import vn.hoidanit.laptopshop.domain.Role;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.repository.RoleRepository;
import vn.hoidanit.laptopshop.repository.UserRepository;

@Service
public class UserService {
    public final UserRepository userRepository;
    public final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
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

    public User handelUpdateUser(User user) {
        User eric = this.userRepository.save(user);
        System.out.println(eric);
        return eric;
    }

    public void handelDeleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    public Role getRoleByName(String name) {
        return this.roleRepository.findByName(name);
    }

}
