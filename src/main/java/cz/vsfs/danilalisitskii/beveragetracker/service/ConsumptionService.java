package cz.vsfs.danilalisitskii.beveragetracker.service;

import cz.vsfs.danilalisitskii.beveragetracker.entity.Achievement;
import cz.vsfs.danilalisitskii.beveragetracker.entity.BeverageType;
import cz.vsfs.danilalisitskii.beveragetracker.entity.Consumption;
import cz.vsfs.danilalisitskii.beveragetracker.entity.User;
import cz.vsfs.danilalisitskii.beveragetracker.repository.AchievementRepository;
import cz.vsfs.danilalisitskii.beveragetracker.repository.ConsumptionRepository;
import cz.vsfs.danilalisitskii.beveragetracker.repository.UserRepository;
import cz.vsfs.danilalisitskii.beveragetracker.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ConsumptionService {
    private final ConsumptionRepository consumptionRepository;
    private final UserRepository userRepository;
    private final AchievementRepository achievementRepository;
    private final BeverageTypeService beverageTypeService;

    public ConsumptionService(
            ConsumptionRepository consumptionRepository,
            UserRepository userRepository,
            AchievementRepository achievementRepository,
            BeverageTypeService beverageTypeService
    ) {
        this.consumptionRepository = consumptionRepository;
        this.userRepository = userRepository;
        this.achievementRepository = achievementRepository;
        this.beverageTypeService = beverageTypeService;
    }

    @Transactional
    public Consumption addConsumption(Long userId, Integer amountMl) {
        return addConsumption(userId, amountMl, null);
    }

    @Transactional
    public Consumption addConsumption(Long userId, Integer amountMl, Long beverageTypeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        BeverageType beverageType = (beverageTypeId != null)
                ? beverageTypeService.findById(beverageTypeId)
                : beverageTypeService.getOrDefaultWater();

        int effectiveHydrationMl = (int) Math.round(amountMl * beverageType.getHydrationFactor());

        Consumption consumption = new Consumption();
        consumption.setUser(user);
        consumption.setBeverageType(beverageType);
        consumption.setAmountMl(amountMl);
        consumption.setEffectiveHydrationMl(effectiveHydrationMl);
        consumption.setCreatedAt(LocalDateTime.now());
        consumption = consumptionRepository.save(consumption);

        checkAchievements(user);
        
        return consumption;
    }

    @Transactional
    public void deleteConsumption(Long id) {
        if (!consumptionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Consumption not found with id: " + id);
        }
        consumptionRepository.deleteById(id);
    }

    public java.util.List<Consumption> findAll() {
        return consumptionRepository.findAll();
    }

    public java.util.List<cz.vsfs.danilalisitskii.beveragetracker.controller.dto.UserStatsDto> getTopDrinkers() {
        return consumptionRepository.findTopDrinkers();
    }

    public void checkAchievements(User user) {
        long totalDrinks = consumptionRepository.countByUserId(user.getId());

        if (totalDrinks == 1) {
            assignAchievement(user, "Novice Drinker");
        }
        if (totalDrinks == 5) {
            assignAchievement(user, "Average cola enjoyer");
        }
        if (totalDrinks == 10) {
            assignAchievement(user, "Diabetes is loading...lol");
        }
    }

    public void assignAchievement(User user, String achievementName) {
        Achievement achievement = achievementRepository.findByName(achievementName);

        if (achievement != null) {
            if (!user.getAchievements().contains(achievement)) {
                user.getAchievements().add(achievement);
                userRepository.save(user);
                System.out.println("Achievement " + achievementName + " has been assigned to user " + user.getUsername());
            } else {
                System.out.println("Error: achievement " + achievementName + " is already assigned to user " + user.getUsername());
            }
        }
    }
}
