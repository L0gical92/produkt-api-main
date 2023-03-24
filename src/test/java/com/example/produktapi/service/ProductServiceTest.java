package com.example.produktapi.service;

import com.example.produktapi.exception.BadRequestException;
import com.example.produktapi.exception.EntityNotFoundException;
import com.example.produktapi.model.Product;
import com.example.produktapi.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock //skapar en mock av repository
    private ProductRepository repository;
    @InjectMocks //injecterar mocks i underTest, mocksen som injecteras är repository i det här fallet.
    private ProductService underTest;

    @Test
    void getAllProducts() {
        //when
        underTest.getAllProducts();
        //then
        verify(repository, times(1)).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    void whenCallingGetAllCategories_thenFindAllCategoriesMethodCalled() {
        //when
        underTest.getAllCategories();
        //then
        verify(repository, times(1)).findAllCategories();
        verifyNoMoreInteractions(repository);    //verifying there was no more interactions with repository
    }

    @Test
    void whenSearchingProductByCategory_thenReturnProductsInCategory() {
        // given
        List<Product> optionalProducts = Arrays.asList(
                new Product("Dator", 4000.0, "Electronics", "", ""),
                new Product("Tshirt", 4000.0, "Mens clothing", "", ""));
        // when
        when(repository.findByCategory("Electronics")).thenReturn(Arrays.asList(optionalProducts.get(0)));
        when(repository.findByCategory("Mens clothing")).thenReturn(Arrays.asList(optionalProducts.get(1)));
        when(repository.findByCategory("Womens clothing")).thenReturn(new ArrayList<>());
        // then
        assertEquals(Arrays.asList(optionalProducts.get(0)), underTest.getProductsByCategory("Electronics"));
        assertEquals(Arrays.asList(optionalProducts.get(1)), underTest.getProductsByCategory("Mens clothing"));
        assertTrue(underTest.getProductsByCategory("Womens clothing").isEmpty());
        verify(repository, times(1)).findByCategory("Electronics");
        verify(repository, times(1)).findByCategory("Mens clothing");
        verify(repository, times(1)).findByCategory("Womens clothing");
        assertTrue(underTest.getProductsByCategory("Womens clothing").isEmpty());
    }

    @Test
    void whenRetrievingAnExistingProductById_thenReturnProduct() {
        //given
        Product product = new Product("Dator", 4000.0, "", "", "");
        product.setId(1);
        //when
        when(repository.findById(1)).thenReturn(Optional.of(product));
        //then
        assertEquals(product, underTest.getProductById(1));
        verify(repository).findById(1);
    }

    @Test
    void whenRetrievingANoneExistingProductById_thenThrowAnException() {
        // then
        assertThrows(EntityNotFoundException.class, () -> {
            underTest.getProductById(1);
        });

        verify(repository).findById(1);
    }

    @Test
    void whenAddingAProduct_thenSaveMethodShouldBeCalled() {
        //given
        Product product = new Product("Dator", 4000.0, "", "", "");
        //when
        underTest.addProduct(product);
        //then
        verify(repository).save(product);
    }

    @Test
    void whenAddingAProductAlreadyInDatabase_thenThrowException() {
        Product existingProduct = new Product("Dator", 4000.0, "", "", "");
        repository.save(existingProduct);
        when(repository.findByTitle(existingProduct.getTitle())).thenReturn(Optional.of(existingProduct));
        Product newProduct = new Product("Dator", 4000.0, "", "", "");
        assertThrows(BadRequestException.class, () -> underTest.addProduct(newProduct));
    }

    @Test
    void whenUpdatingAnExistingProduct_thenReturnUpdatedProduct() {
        //given
        Product orginalProduct = new Product("Dator", 4000.0, "", "", "");
        int id = 1;
        Product updatedProduct = new Product("Uppdaterad Dator", 4000.0, "", "", "");
        //when
        when(repository.findById(id)).thenReturn(Optional.of(orginalProduct));
        when(repository.save(updatedProduct)).thenReturn(updatedProduct);
        //then
        assertEquals(updatedProduct.getTitle(), underTest.updateProduct(updatedProduct, id).getTitle());
    }

    @Test
    void whenUpdatingANoneExistingProduct_thenThrowException() {
        //given
        Product product = new Product("Dator", 4000.0, "", "", "");
        int id = 1;
        //when
        when(repository.findById(id)).thenReturn(Optional.empty());
        //then
        assertThrows(EntityNotFoundException.class, () -> underTest.updateProduct(product, id));
    }

    @Test
    void whenDeletingAnExistingProduct_thenReturnConfirmation() {
        // given
        int id = 1;
        Product product = new Product("Dator", 4000.0, "", "", "");

        // when
        when(repository.findById(id)).thenReturn(Optional.of(product));
        // then
        underTest.deleteProduct(id);
        verify(repository, times(1)).deleteById(id);
    }

    @Test
    void whenDeletingANoneExistingProduct_thenThrowException() {
        // given
        int id = 1;
        // when
        when(repository.findById(id)).thenReturn(Optional.empty());
        // then
        assertThrows(EntityNotFoundException.class, () -> underTest.deleteProduct(id));
    }
}