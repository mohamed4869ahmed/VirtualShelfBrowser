package service;

import model.User;
import model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public boolean checkIfExists(String username) {
        return userRepository.findById(username).isPresent();
    }

    public boolean authenticate(User user) {
        Optional<User> optionalUser = userRepository.findById(user.getUsername());
        return optionalUser.map(u -> u.getPassword().equals(user.getPassword())).orElse(false);
    }

    public void addUser(User user) {
        userRepository.save(user);
    }


}
