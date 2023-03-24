package com.example.produktapi.repository;

import com.example.produktapi.model.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductRepositoryTest {
    @Autowired
    private ProductRepository underTest;

    @Test
    void testingOurRepository() {
        List<Product> products = underTest.findAll();
        Assertions.assertFalse(products.isEmpty());
    }

    @Test
    void WhenSearchingByCategory_andCategoryExists_thenReturnProduct() {
        //given
        String title = "Ett halsband";
        Product product = new Product(title, 25000.0, "Smycken", "flashigt", "urltitle");
        underTest.save(product);
        //when
        List<Product> expectedProducts = underTest.findByCategory("Smycken");
        //then
        assertTrue(expectedProducts.contains(product));
    }

    @Test
    void WhenSearchingByCategory_andCategoryDoesNotExist_thenReturnEmptyList() {
        //given
        String title = "Ett halsband";
        Product product = new Product(title, 25000.0, "Smycken", "flashigt", "urltitle");
        underTest.save(product);
        //when
        List<Product> expectedProducts = underTest.findByCategory("Skor");
        //then
        assertTrue(expectedProducts.isEmpty());
    }

    @Test
    void whenSearchingForAnExistingTitle_thenReturnThatProduct() {
        //given
        String title = "En dator";
        underTest.save(new Product(title, 25000.0, "Elektronik", "bra o ha", "urltitle"));
        //when
        Optional<Product> optionalProduct = underTest.findByTitle(title);
        //then
        assertTrue(optionalProduct.isPresent());
        Assertions.assertEquals(title, optionalProduct.get().getTitle());

    }

    @Test
    void whenSearchingForNoneExistingTitle_thenReturnEmpty() {
        //given
        String title = "En dator";
        //when
        Optional<Product> optionalProduct = underTest.findByTitle(title);
        //then
        assertTrue(optionalProduct.isEmpty());
        assertFalse(optionalProduct.isPresent());
        assertThrows(Exception.class, () -> optionalProduct.get().getTitle());
    }

    @Test
    void returnAllDistinctCategories() {
        List<String> expectedCategories = Arrays.asList("electronics", "jewelery", "men's clothing", "women's clothing");
        List<String> categories = underTest.findAllCategories();
        assertFalse(categories.isEmpty());
        assertEquals(expectedCategories, categories);
    }
}