package cz.vsfs.danilalisitskii.beveragetracker.config;

import cz.vsfs.danilalisitskii.beveragetracker.entity.Achievement;
import cz.vsfs.danilalisitskii.beveragetracker.entity.BeverageType;
import cz.vsfs.danilalisitskii.beveragetracker.entity.Role;
import cz.vsfs.danilalisitskii.beveragetracker.entity.User;
import cz.vsfs.danilalisitskii.beveragetracker.repository.AchievementRepository;
import cz.vsfs.danilalisitskii.beveragetracker.repository.BeverageTypeRepository;
import cz.vsfs.danilalisitskii.beveragetracker.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final BeverageTypeRepository beverageTypeRepository;
    private final AchievementRepository achievementRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            BeverageTypeRepository beverageTypeRepository,
            AchievementRepository achievementRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.beverageTypeRepository = beverageTypeRepository;
        this.achievementRepository = achievementRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedBeverageTypes();
        seedAchievements();
        seedDefaultAdmin();
    }

    private void seedBeverageTypes() {
        List<BeverageType> defaultBeverages = List.of(
                new BeverageType("Water", 1.0, "💧"),
                new BeverageType("Unsweetened Tea", 0.95, "🍵"),
                new BeverageType("Juice", 0.85, "🧃"),
                new BeverageType("Coffee", 0.80, "☕"),
                new BeverageType("Sweetened Soda / Cola", 0.65, "🥤")
        );

        for (BeverageType beverage : defaultBeverages) {
            if (!beverageTypeRepository.existsByName(beverage.getName())) {
                beverageTypeRepository.save(beverage);
            }
        }
    }

    private void seedAchievements() {
        record DefaultBadge(String name, String description, int points, int requiredConsumptions) {}

        List<DefaultBadge> defaultBadges = List.of(
                new DefaultBadge("First Sip", "Log your very first beverage entry", 10, 1),
                new DefaultBadge("Hydration Habit", "Reach 5 logged beverage entries", 50, 5),
                new DefaultBadge("Hydration Master", "Reach 10 logged beverage entries", 100, 10)
        );

        for (DefaultBadge badge : defaultBadges) {
            if (achievementRepository.findByName(badge.name()) == null) {
                Achievement achievement = new Achievement(badge.name(), badge.description(), badge.points());
                achievement.setRequiredConsumptions(badge.requiredConsumptions());
                achievementRepository.save(achievement);
            }
        }
    }

    private void seedDefaultAdmin() {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User("admin", "admin@beveragetracker.local", 2500);
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ROLE_ADMIN);
            userRepository.save(admin);
        }
    }
}
