package cz.vsfs.danilalisitskii.beveragetracker.service;

import cz.vsfs.danilalisitskii.beveragetracker.entity.Achievement;
import cz.vsfs.danilalisitskii.beveragetracker.entity.User;
import cz.vsfs.danilalisitskii.beveragetracker.repository.AchievementRepository;
import cz.vsfs.danilalisitskii.beveragetracker.repository.UserRepository;
import cz.vsfs.danilalisitskii.beveragetracker.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final AchievementRepository achievementRepository;

    public UserService(UserRepository userRepository, AchievementRepository achievementRepository) {
        this.userRepository = userRepository;
        this.achievementRepository = achievementRepository;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public User create(User user) {
        if (user.getDailyWaterGoalMl() == null) {
            user.setDailyWaterGoalMl(2000);
        }
        return userRepository.save(user);
    }

    public User update(Long id, User userDetails) {
        User user = findById(id); 
        
        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        if (userDetails.getDailyWaterGoalMl() != null) {
            user.setDailyWaterGoalMl(userDetails.getDailyWaterGoalMl());
        }
        
        return userRepository.save(user);
    }

    public void delete(Long id) {
        User user = findById(id); 
        userRepository.delete(user);
    }

    public User addAchievement(Long userId, Long achievementId) {
        User user = findById(userId);
        
        Achievement achievement = achievementRepository.findById(achievementId)
                .orElseThrow(() -> new ResourceNotFoundException("Achievement not found with id: " + achievementId));

        user.getAchievements().add(achievement);

        return userRepository.save(user);
    }
}
