package cz.vsfs.danilalisitskii.beveragetracker.service;

import cz.vsfs.danilalisitskii.beveragetracker.entity.BeverageType;
import cz.vsfs.danilalisitskii.beveragetracker.exception.ResourceNotFoundException;
import cz.vsfs.danilalisitskii.beveragetracker.repository.BeverageTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BeverageTypeService {

    public static final String DEFAULT_WATER_NAME = "Water";

    private final BeverageTypeRepository beverageTypeRepository;

    public BeverageTypeService(BeverageTypeRepository beverageTypeRepository) {
        this.beverageTypeRepository = beverageTypeRepository;
    }

    public List<BeverageType> findAll() {
        return beverageTypeRepository.findAll();
    }

    public BeverageType findById(Long id) {
        return beverageTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BeverageType not found with id: " + id));
    }

    public BeverageType create(BeverageType beverageType) {
        if (beverageTypeRepository.existsByName(beverageType.getName())) {
            throw new IllegalArgumentException("BeverageType with name '" + beverageType.getName() + "' already exists");
        }
        return beverageTypeRepository.save(beverageType);
    }

    public BeverageType getOrDefaultWater() {
        return beverageTypeRepository.findByName(DEFAULT_WATER_NAME)
                .orElseGet(() -> beverageTypeRepository.save(new BeverageType(DEFAULT_WATER_NAME, 1.0, "💧")));
    }
}
