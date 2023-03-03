package com.example.produktapi.service;

import com.example.produktapi.model.Product;
import com.example.produktapi.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Time;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock //skapar en mock av repository
    private ProductRepository repository;
    @InjectMocks //injecterar mocks i underTest, mocksen som injecteras är repository i det här fallet.
    private  ProductService underTest;
    @Captor
    ArgumentCaptor<Product> productCaptor;
    @Test
    void getAllProducts(){
        //when
        underTest.getAllProducts();
        //then
        BDDMockito.verify(repository, Mockito.times(1)).findAll();
        BDDMockito.verifyNoMoreInteractions(repository);
    }
    @Test
    void whenAddingAProduct_thenSaveMethodShouldBeCalled(){
        //given
        Product product = new Product("Dator",4000.0, "",
                "","");
        //when
        underTest.addProduct(product);
        //then
        BDDMockito.verify(repository).save(productCaptor.capture());
        assertEquals(product,productCaptor.getValue());
    }
}