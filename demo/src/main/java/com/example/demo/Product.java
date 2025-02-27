package com.example.demo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @NotBlank(message = "Name must not be null or empty")
    @Column(nullable = false)
    private String name;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String Description;

    @NotNull(message = "Price must not be null")
    @Positive(message = "Price must be greater than 0")
    @Column(nullable = false)
    private double price;

    @NotNull(message = "Stock quantity must not be null")
    @Min(value = 0, message = "Stock quantity must not be negative")
    @Column(nullable = false)
    private Integer stockQuantity;
}
