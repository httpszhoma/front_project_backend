package zhoma.service;


import org.springframework.stereotype.Service;
import zhoma.models.User;
import zhoma.repository.UserRepository;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> allUsers(){
        return new ArrayList<>(userRepository.findAll());

    }

}
