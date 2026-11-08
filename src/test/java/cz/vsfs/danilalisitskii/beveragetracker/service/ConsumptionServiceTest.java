package cz.vsfs.danilalisitskii.beveragetracker.service;

import cz.vsfs.danilalisitskii.beveragetracker.entity.BeverageType;
import cz.vsfs.danilalisitskii.beveragetracker.entity.Consumption;
import cz.vsfs.danilalisitskii.beveragetracker.entity.User;
import cz.vsfs.danilalisitskii.beveragetracker.repository.AchievementRepository;
import cz.vsfs.danilalisitskii.beveragetracker.repository.ConsumptionRepository;
import cz.vsfs.danilalisitskii.beveragetracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsumptionServiceTest {

    @Mock
    private ConsumptionRepository consumptionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AchievementRepository achievementRepository;

    @Mock
    private BeverageTypeService beverageTypeService;

    @InjectMocks
    private ConsumptionService consumptionService;

    @Test
    void addConsumption_WithCoffee_ShouldCalculateEffectiveHydration() {
        User user = new User("danila", "danila@vsfs.cz", 2000);
        user.setId(1L);

        BeverageType coffee = new BeverageType("Coffee", 0.80, "☕");
        coffee.setId(4L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(beverageTypeService.findById(4L)).thenReturn(coffee);
        when(consumptionRepository.save(any(Consumption.class))).thenAnswer(inv -> inv.getArgument(0));
        when(consumptionRepository.countByUserId(1L)).thenReturn(1L);

        Consumption result = consumptionService.addConsumption(1L, 250, 4L);

        assertNotNull(result);
        assertEquals(250, result.getAmountMl());
        assertEquals(200, result.getEffectiveHydrationMl()); // 250 * 0.80 = 200 ml
        assertEquals("Coffee", result.getBeverageType().getName());
    }

    @Test
    void addConsumption_WithoutBeverageType_ShouldDefaultToWater() {
        User user = new User("danila", "danila@vsfs.cz", 2000);
        user.setId(1L);

        BeverageType water = new BeverageType("Water", 1.0, "💧");
        water.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(beverageTypeService.getOrDefaultWater()).thenReturn(water);
        when(consumptionRepository.save(any(Consumption.class))).thenAnswer(inv -> inv.getArgument(0));
        when(consumptionRepository.countByUserId(1L)).thenReturn(2L);

        Consumption result = consumptionService.addConsumption(1L, 500, null);

        assertNotNull(result);
        assertEquals(500, result.getAmountMl());
        assertEquals(500, result.getEffectiveHydrationMl());
        assertEquals("Water", result.getBeverageType().getName());
    }
}
