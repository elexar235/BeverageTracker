package cz.vsfs.danilalisitskii.beveragetracker.config;

import cz.vsfs.danilalisitskii.beveragetracker.entity.BeverageType;
import cz.vsfs.danilalisitskii.beveragetracker.repository.BeverageTypeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final BeverageTypeRepository beverageTypeRepository;

    public DataInitializer(BeverageTypeRepository beverageTypeRepository) {
        this.beverageTypeRepository = beverageTypeRepository;
    }

    @Override
    public void run(String... args) {
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
}
