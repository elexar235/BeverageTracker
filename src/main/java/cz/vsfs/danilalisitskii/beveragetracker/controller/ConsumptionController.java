package cz.vsfs.danilalisitskii.beveragetracker.controller;

import cz.vsfs.danilalisitskii.beveragetracker.controller.dto.ConsumptionCreateDto;
import cz.vsfs.danilalisitskii.beveragetracker.controller.dto.ConsumptionDto;
import cz.vsfs.danilalisitskii.beveragetracker.controller.dto.mappers.ConsumptionMapper;
import cz.vsfs.danilalisitskii.beveragetracker.entity.Consumption;
import cz.vsfs.danilalisitskii.beveragetracker.service.ConsumptionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/consumptions")
public class ConsumptionController {
    
    private final ConsumptionService consumptionService;
    private final ConsumptionMapper consumptionMapper;

    public ConsumptionController(ConsumptionService consumptionService, ConsumptionMapper consumptionMapper) {
        this.consumptionService = consumptionService;
        this.consumptionMapper = consumptionMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConsumptionDto addConsumption(@RequestBody @Valid ConsumptionCreateDto createDto) {
        Consumption consumption = consumptionService.addConsumption(
                createDto.getUserId(),
                createDto.getAmountMl(),
                createDto.getBeverageTypeId()
        );
        return consumptionMapper.toDto(consumption);
    }

    @GetMapping
    public java.util.List<ConsumptionDto> getAllConsumptions() {
        return consumptionService.findAll().stream()
                .map(consumptionMapper::toDto)
                .toList();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteConsumption(@PathVariable Long id) {
        consumptionService.deleteConsumption(id);
    }

    @GetMapping("/stats/leaderboard")
    public java.util.List<cz.vsfs.danilalisitskii.beveragetracker.controller.dto.UserStatsDto> getLeaderboard() {
        return consumptionService.getTopDrinkers();
    }
}
