package cz.vsfs.danilalisitskii.beveragetracker.controller;

import cz.vsfs.danilalisitskii.beveragetracker.controller.dto.BeverageTypeCreateDto;
import cz.vsfs.danilalisitskii.beveragetracker.controller.dto.BeverageTypeDto;
import cz.vsfs.danilalisitskii.beveragetracker.controller.dto.mappers.BeverageTypeMapper;
import cz.vsfs.danilalisitskii.beveragetracker.entity.BeverageType;
import cz.vsfs.danilalisitskii.beveragetracker.service.BeverageTypeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/beverage-types")
public class BeverageTypeController {

    private final BeverageTypeService beverageTypeService;
    private final BeverageTypeMapper beverageTypeMapper;

    public BeverageTypeController(BeverageTypeService beverageTypeService, BeverageTypeMapper beverageTypeMapper) {
        this.beverageTypeService = beverageTypeService;
        this.beverageTypeMapper = beverageTypeMapper;
    }

    @GetMapping
    public List<BeverageTypeDto> findAll() {
        return beverageTypeService.findAll().stream()
                .map(beverageTypeMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public BeverageTypeDto findById(@PathVariable Long id) {
        BeverageType beverageType = beverageTypeService.findById(id);
        return beverageTypeMapper.toDto(beverageType);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BeverageTypeDto create(@RequestBody @Valid BeverageTypeCreateDto createDto) {
        BeverageType beverageType = beverageTypeMapper.toEntity(createDto);
        BeverageType saved = beverageTypeService.create(beverageType);
        return beverageTypeMapper.toDto(saved);
    }
}
