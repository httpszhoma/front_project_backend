package zhoma.service;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import zhoma.models.Role;
import zhoma.models.User;
import zhoma.repository.SellerRequestRepository;
import zhoma.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final SellerRequestRepository sellerRequestRepository;


    public UserService(UserRepository userRepository, SellerRequestRepository sellerRequestRepository) {
        this.userRepository = userRepository;
        this.sellerRequestRepository = sellerRequestRepository;
    }

    public void  deleteUser(Long userId){
        userRepository.deleteById(userId);
    }


    public List<User> allUsers(){
        return new ArrayList<>(userRepository.findAll());

    }

    public User getUserById(Long userId) {
        return userRepository.findUserById(userId).orElseThrow(()-> new UsernameNotFoundException("this user doesn't exist"));
    }
    public User getUserByUsername(String username){
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("This User with this  username: "+ username+ " doesn't exist !!!"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                mapRolesToAuthorities(user.getRole())
        );
    }

    private Collection<GrantedAuthority> mapRolesToAuthorities(Role role) {
        return List.of(new SimpleGrantedAuthority(role.name())); // Предполагается, что `Role` — это Enum
    }

    public User saveUser(User currentUser) {
        return userRepository.save(currentUser);
    }

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = getUserByUsername(username);
        return currentUser;

    }
}
