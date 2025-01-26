package org.example.usermanagement.security.services;

import org.example.usermanagement.models.User;
import org.example.usermanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUser(){
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id){
        return userRepository.findById(id);
    }

    public User editUser(Long id, String phoneNumber){
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setPhonenumber(phoneNumber);
        return userRepository.save(user);
    }

    public void deleteUser(Long id){
        userRepository.deleteById(id);
    }

    public User ToggleUserActiveStatus(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setActive(!user.isActive());
        return userRepository.save(user);
    }
}
