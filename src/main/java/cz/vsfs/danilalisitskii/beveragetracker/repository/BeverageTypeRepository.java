package cz.vsfs.danilalisitskii.beveragetracker.repository;

import cz.vsfs.danilalisitskii.beveragetracker.entity.BeverageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BeverageTypeRepository extends JpaRepository<BeverageType, Long> {
    Optional<BeverageType> findByName(String name);
    boolean existsByName(String name);
}
