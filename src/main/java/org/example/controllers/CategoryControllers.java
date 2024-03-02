package org.example.controllers;

import org.springframework.web.bind.annotation.RequestBody;
import lombok.AllArgsConstructor;
import org.example.dto.category.CategoryCreateDTO;
import org.example.dto.category.CategoryItemDTO;
import org.example.entities.CategoryEntity;
import org.example.mapper.CategoryMapper;
import org.example.repositories.CategoryRepository;
import org.example.storage.FileSaveFormat;
import org.example.storage.StorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@RestController
@AllArgsConstructor
@RequestMapping("api/categories")
public class CategoryControllers {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final StorageService storageService;
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping
    public ResponseEntity<List<CategoryEntity>> index() {
        List<CategoryEntity> list = categoryRepository.findAll();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity<CategoryEntity> create(@RequestBody CategoryEntity category) {
        CategoryEntity entity = new CategoryEntity();
        entity.setName(category.getName());
        entity.setImage(category.getImage());
        entity.setDescription(category.getDescription());
        entity.setCreationTime(category.getCreationTime());
        categoryRepository.save(entity);
        return new ResponseEntity<>(category, HttpStatus.CREATED);
    }

    @PostMapping("/{id}")
    public ResponseEntity<CategoryEntity> update(@PathVariable("id") int id, @RequestBody CategoryEntity category) {
        CategoryEntity entity = categoryRepository.findById(id).get();
        entity.setName(category.getName());
        entity.setImage(category.getImage());
        entity.setDescription(category.getDescription());
        entity.setCreationTime(category.getCreationTime());
        categoryRepository.save(entity);
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CategoryEntity> delete(@PathVariable("id") int id) {
        CategoryEntity entity = categoryRepository.findById(id).get();
        categoryRepository.delete(entity);
        return new ResponseEntity<>(entity, HttpStatus.OK);
    }

    /*@PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CategoryItemDTO> create(@ModelAttribute CategoryCreateDTO dto) {
        try {
            CategoryEntity entity = categoryMapper.categoryCreateDTO(dto);
            String image = storageService.saveImage(dto.getImage(), FileSaveFormat.WEBP);
            entity.setImage(image);
            categoryRepository.save(entity);
            return new ResponseEntity<>(categoryMapper.categoryItemDTO(entity), HttpStatus.CREATED);
        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }*/
}