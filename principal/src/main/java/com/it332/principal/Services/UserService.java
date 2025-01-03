package com.it332.principal.Services;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.it332.principal.DTO.UserAdminRequest;
import com.it332.principal.DTO.UserDetails;
import com.it332.principal.DTO.UserResponse;
import com.it332.principal.Models.Association;
import com.it332.principal.Models.Position;
import com.it332.principal.Models.School;
import com.it332.principal.Models.User;
import com.it332.principal.Repository.AssociationRepository;
import com.it332.principal.Repository.UserRepository;
import com.it332.principal.Security.JwtUtil;
import com.it332.principal.Security.NotFoundException;
import com.it332.principal.Models.Token; // Your Token model
import com.it332.principal.Repository.TokenRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UserService {

    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssociationRepository associationRepository;

    @Autowired
    private SchoolService schoolService;

    @Autowired
    private PositionService positionService;

    @Autowired
    @Lazy
    private TokenService tokenService;

    @Autowired
    private JwtUtil jwtUtil; // Inject your JwtUtil for token management

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String generateToken(String userId) {
        return jwtUtil.generateToken(userId);
    }

    @Transactional
    public User createUser(User user) {
        // Check if user exists and throws and exception
        checkUserExists(user.getEmail(), user.getUsername());

        // special case for creating super admin account
        if (user.getUsername().equals("administrator")) {
            user.setPosition("Super administrator");
        } else {
            // Check if position is existent
            Position exist = positionService.getPositionByName(user.getPosition());
            // insert position name to user
            user.setPosition(exist.getName());
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setVerified(false);

        // Save user to the repository
        User savedUser = userRepository.save(user);

        // Send verification email after user creation
        tokenService.sendEmailVerification(savedUser);

        return savedUser; // Return the saved user object
    }

    // Create a new user as an admin: for creation of principal
    public User createUser(UserAdminRequest user) {
        // Check if user exists and throws and exception
        checkUserExists(user.getEmail(), user.getUsername());

        // Check if user requesting is admin
        User admin = getUserById(user.getAdminId());
        String position = admin.getPosition();
        if (!position.equals("Super administrator")) {
            throw new IllegalArgumentException("Cannot process this request: Insufficient priviledge");
        }

        // Check if position is existent
        Position exist = positionService.getPositionByName(user.getPosition());

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // insert position name to user
        user.setPosition(exist.getName());
        return userRepository.save(new User(user));
    }

    public boolean validateUser(String emailOrUsername, String password) {
        // Find user by email or username
        User userByEmail = userRepository.findByEmail(emailOrUsername);
        User userByUsername = userRepository.findByUsername(emailOrUsername);
        User user;

        if (userByEmail != null || userByUsername != null) {
            if (userByEmail != null) {
                user = userByEmail;
            } else {
                user = userByUsername;
            }

            // Verify the password using BCrypt
            return passwordEncoder.matches(password, user.getPassword());
        }

        return false; // User not found
    }

    public void checkUserExists(String email, String username) {
        if (userRepository.findByEmail(email) != null) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }

        if (userRepository.findByUsername(username) != null) {
            throw new IllegalArgumentException("An account with this username already exists.");
        }
    }

    public List<UserDetails> fetchPrincipals() {
        List<User> principals = userRepository.findByPosition("Principal");
        return principals.stream()
                .map(UserDetails::new)
                .collect(Collectors.toList());
    }

    public UserResponse getUserByEmailUsername(String emailOrUsername) {
        // Find user by email or username
        User userByEmail = userRepository.findByEmail(emailOrUsername);
        User userByUsername = userRepository.findByUsername(emailOrUsername);
        User user;

        if (userByEmail != null || userByUsername != null) {
            if (userByEmail != null) {
                user = getUserByEmail(emailOrUsername);
            } else {
                user = getUserByUsername(emailOrUsername);
            }

            List<Association> associations = associationRepository.findByUserId(user.getId());

            // Filter associations where approved is true
            List<String> approvedSchoolIds = associations.stream()
                    .filter(Association::isApproved)
                    .map(Association::getSchoolId)
                    .collect(Collectors.toList());

            // Retrieve School objects for approved schoolIds
            List<School> approvedSchools = approvedSchoolIds.stream()
                    .map(schoolId -> schoolService.getSchoolById(schoolId))
                    .collect(Collectors.toList());

            // Verify the password using BCrypt
            return new UserResponse(user, approvedSchools);
        }

        throw new NotFoundException("User not found with email/username: " + emailOrUsername);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new NotFoundException("User not found with email: " + email);
        }

        return user;
    }

    public User getUserByToken(String tokenValue) {
        // Retrieve the token from the repository
        Token foundToken = tokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new NotFoundException("Token not found: " + tokenValue));

        // Use the found token to get the associated user
        User user = userRepository.findById(foundToken.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found with token: " + tokenValue));

        return user;
    }

    public UserResponse getUserAssociationsById(String id) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + id));

        List<Association> associations = associationRepository.findByUserId(existingUser.getId());

        // Filter associations where approved is true
        List<String> approvedSchoolIds = associations.stream()
                .filter(Association::isApproved)
                .map(Association::getSchoolId)
                .collect(Collectors.toList());

        // Retrieve School objects for approved schoolIds
        List<School> approvedSchools = approvedSchoolIds.stream()
                .map(schoolId -> schoolService.getSchoolById(schoolId))
                .collect(Collectors.toList());

        // Verify the password using BCrypt
        return new UserResponse(existingUser, approvedSchools);
    }

    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + id));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<UserDetails> getUsersByIds(Collection<String> userIds) {
        return userRepository.findAllById(userIds).stream()
                .map(UserDetails::new) // Convert User to UserDetails
                .collect(Collectors.toList()); // Collect results into a list
    }

    public void updateUser(String userId, User updateUser) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Update only the fields that are present in updateUser
            if (updateUser.getFname() != null) {
                user.setFname(updateUser.getFname());
            }
            if (updateUser.getMname() != null) {
                user.setMname(updateUser.getMname());
            }
            if (updateUser.getLname() != null) {
                user.setLname(updateUser.getLname());
            }
            if (updateUser.getUsername() != null) {
                user.setUsername(updateUser.getUsername());
            }
            if (updateUser.getEmail() != null) {
                user.setEmail(updateUser.getEmail());
            }
            if (updateUser.getPassword() != null) {
                String encodedPassword = passwordEncoder.encode(updateUser.getPassword());
                user.setPassword(encodedPassword);
            }
            if (updateUser.getPosition() != null) {
                user.setPosition(updateUser.getPosition());
            }
            if (updateUser.getAvatar() != null) {
                user.setAvatar(updateUser.getAvatar());
            }

            userRepository.save(user); // Save the updated user object
        } else {
            throw new NotFoundException("User not found with ID: " + userId);
        }
    }

    public void updateUserAvatar(String userId, String avatar) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (avatar != null) {
                user.setAvatar(avatar);
            }

            userRepository.save(user); // Save the updated user object
        } else {
            throw new NotFoundException("User not found with ID: " + userId);
        }
    }

    public void deleteUserById(String userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            userRepository.deleteById(userId);
        } else {
            throw new NotFoundException("User not found with ID: " + userId);
        }
    }

    public boolean checkIfUserExists(String emailOrUsername) {
        User userByEmail = userRepository.findByEmail(emailOrUsername);
        User userByUsername = userRepository.findByUsername(emailOrUsername);

        return userByEmail != null || userByUsername != null;
    }

    public void updateUserPassword(String userId, String newPassword) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String encodedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encodedPassword);
            userRepository.save(user);
        } else {
            throw new NotFoundException("User not found with ID: " + userId);
        }
    }

    public boolean isCurrentUserVerified(String emailOrUsername) {
        UserResponse userResponse = getUserByEmailUsername(emailOrUsername);

        // Check if userResponse is not null
        if (userResponse != null) {
            return userResponse.isVerified(); // Return the verification status
        }

        // Return false if the user is not found
        return false;
    }

    public void updateUserVerificationStatus(String email, boolean status) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            user.setVerified(status);
            userRepository.save(user);
        } else {
            throw new NotFoundException("User not found with email: " + email);
        }
    }

    public UserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName(); // Get the current username

        // Fetch the user by their email/username
        User user = userRepository.findByEmail(currentUsername);
        if (user == null) {
            user = userRepository.findByUsername(currentUsername);
        }
        if (user != null) {
            return new UserResponse(user); // This should now work
        }
        throw new NotFoundException("User not found: " + currentUsername);
    }

}