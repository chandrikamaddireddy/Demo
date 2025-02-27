package com.example.demo;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ProductController.class)
public class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private Product validProduct;
    private Product invalidProduct;

    @BeforeEach
    void setUp() {
        validProduct = new Product();
        validProduct.setName("Smartphone");
        validProduct.setPrice(500.0);
        validProduct.setStockQuantity(10);

        invalidProduct = new Product();
        invalidProduct.setName(""); // Invalid name
        invalidProduct.setPrice(0.0); // Invalid price
        invalidProduct.setStockQuantity(-5); // Invalid stock quantity
    }

    @Test
    void testCreateProduct_Success() throws Exception {
        Mockito.when(productService.saveProduct(any(Product.class))).thenReturn(validProduct);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Smartphone"))
                .andExpect(jsonPath("$.price").value(500.0))
                .andExpect(jsonPath("$.stockQuantity").value(10));
    }

    @Test
    void testCreateProduct_ValidationFailure() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Name must not be null or empty"))
                .andExpect(jsonPath("$.price").value("Price must be greater than 0"))
                .andExpect(jsonPath("$.stockQuantity").value("Stock quantity must not be negative"));
    }

    @Test
    void testGetProductById_Found() throws Exception {
        Mockito.when(productService.getProductById(1L)).thenReturn(Optional.of(validProduct));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Smartphone"));
    }

    @Test
    void testGetProductById_NotFound() throws Exception {
        Mockito.when(productService.getProductById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateProduct_Success() throws Exception {
        Mockito.when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(validProduct);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Smartphone"));
    }

    @Test
    void testUpdateProduct_NotFound() throws Exception {
        Mockito.when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validProduct)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteProduct_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }
}
