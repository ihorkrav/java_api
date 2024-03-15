package org.example.controllers;

import org.example.dto.category.CategoryEditDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @GetMapping
    public ResponseEntity<List<CategoryEntity>> index() {
        List<CategoryEntity> list = categoryRepository.findAll();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }


    /*@PostMapping
    public ResponseEntity<CategoryEntity> create(@RequestBody CategoryEntity category) {
        CategoryEntity entity = new CategoryEntity();
        entity.setName(category.getName());
        entity.setImage(category.getImage());
        entity.setDescription(category.getDescription());
        entity.setCreationTime(category.getCreationTime());
        categoryRepository.save(entity);
        return new ResponseEntity<>(category, HttpStatus.CREATED);
    }*/

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
    }

    @PutMapping(value="", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CategoryItemDTO> edit(@ModelAttribute CategoryEditDTO model) {
        var old = categoryRepository.findById(model.getId()).orElse(null);
        if (old == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        var entity = categoryMapper.categoryEditDto(model);
        if(model.getFile()==null) {
            entity.setImage(old.getImage());
        }
        else {
            try {
                storageService.deleteImage(old.getImage());
                String fileName = storageService.saveImage(model.getFile(), FileSaveFormat.WEBP);
                entity.setImage(fileName);
            }
            catch (Exception exception) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        entity.setCreationTime(old.getCreationTime());
        categoryRepository.save(entity);
        var result = categoryMapper.categoryItemDTO(entity);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CategoryEntity> delete(@PathVariable("id") int id) {
        CategoryEntity entity = categoryRepository.findById(id).get();
        categoryRepository.delete(entity);
        return new ResponseEntity<>(entity, HttpStatus.OK);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryItemDTO> getById(@PathVariable int categoryId) {
        var entity = categoryRepository.findById(categoryId).orElse(null);
        if (entity == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        var result =  categoryMapper.categoryItemDTO(entity);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<CategoryItemDTO>> searchByName(@RequestParam(required = false) String name,
                                                              Pageable pageable) {
        Page<CategoryEntity> categories = categoryRepository.findByNameContainingIgnoreCase(name, pageable);
        Page<CategoryItemDTO> result = categories.map(categoryMapper::categoryItemDTO);
        return new ResponseEntity<>(result, HttpStatus.OK);
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