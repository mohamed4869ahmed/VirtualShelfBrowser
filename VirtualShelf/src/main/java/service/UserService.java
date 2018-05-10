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

    public boolean checkIfExists(User user) {
        return userRepository.findById(user.getUsername()).isPresent();
    }

    public boolean checkIfLibraryNameExists(User user) {
        return userRepository.findByLibraryName(user.getLibraryName()).isPresent();
    }

    public boolean authenticate(User user) {
        Optional<User> optionalUser = userRepository.findById(user.getUsername());
        return optionalUser.map(u -> u.getPassword().equals(user.getPassword())).orElse(false);
    }

    public void addUser(User user) {
        userRepository.save(user);
    }

    public String getLibraryName(User user){
        return userRepository.findById(user.getUsername()).map(u -> u.getLibraryName()).orElse(null);
    }


}
