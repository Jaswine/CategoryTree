package com.categorytree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;

import org.hibernate.mapping.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.categorytree.models.Category;
import com.categorytree.models.CategoryRepository;

@SpringBootTest
class CategorytreeApplicationTests {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @Transactional
    public void testCreateCategorySimple() {
        Category category = new Category();
        category.setCategoryName("Decor");

        categoryRepository.save(category);

        Category savedCategory = categoryRepository.findByCategoryName("Decor");
        assertNotNull(savedCategory);
        assertEquals("Decor", savedCategory.getCategoryName());
    }

    @Test
    @Transactional
    public void testCreateCategoryFull() {
        Category newCategory = new Category();
        newCategory.setCategoryName("HomeDecor");
        categoryRepository.save(newCategory);

        Category exCategory = categoryRepository.findByCategoryName("HomeDecor");

    
        Category newCategory1 = new Category();
        newCategory1.setCategoryName("Lamps");
        newCategory1.setParent(exCategory);
        categoryRepository.save(newCategory1);

        categoryRepository.save(exCategory);


        Category savedCategory = categoryRepository.findByCategoryName("HomeDecor");
        assertNotNull(savedCategory);
        assertEquals("HomeDecor", savedCategory.getCategoryName());

        Category savedCategory1 = categoryRepository.findByCategoryName("Lamps");
        assertNotNull(savedCategory1);
        assertEquals("Lamps", savedCategory1.getCategoryName());
        // assertEquals("HomeDecor", savedCategory1.getParent());
    }

    @Test
    @Transactional
    public void testDeleteCategory() {
        Category category = new Category();
        category.setCategoryName("Decor");
        categoryRepository.save(category);

        categoryRepository.delete(category);

        Category deletedCategory = categoryRepository.findByCategoryName("Decor");
        assertNull(deletedCategory, "Категория была удалена успешно");
    }
    
}
