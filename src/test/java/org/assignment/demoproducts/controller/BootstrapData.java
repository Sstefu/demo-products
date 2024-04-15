package org.assignment.demoproducts.controller;

import org.assignment.demoproducts.model.ProductDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sstefan
 * Date: 4/14/2024
 * Project: demo-products
 */
public class BootstrapData {

    public static List<ProductDTO> getData(){
        List<ProductDTO> list = new ArrayList<>();
        ProductDTO product1 = ProductDTO.builder()
                .productName("Test 1")
                .description("Test description 1")
                .price(BigDecimal.TEN)
                .quantityOnHand(300)
                .build();
        ProductDTO product2 = ProductDTO.builder()
                .productName("Test 2")
                .description("Test description 2")
                .price(BigDecimal.valueOf(150))
                .quantityOnHand(100)
                .build();
        ProductDTO product3 = ProductDTO.builder()
                .productName("Test 4")
                .description("Test description 3")
                .price(BigDecimal.valueOf(1004231))
                .quantityOnHand(200)
                .build();
        ProductDTO product4 = ProductDTO.builder()
                .productName("Test 4")
                .description("Test description 4")
                .price(BigDecimal.ONE)
                .quantityOnHand(300)
                .build();
        ProductDTO product5 = ProductDTO.builder()
                .productName("Test 5")
                .description("Test description 5")
                .price(BigDecimal.TEN)
                .quantityOnHand(150)
                .build();
        ProductDTO product6 = ProductDTO.builder()
                .productName("Test 6")
                .description("Test description 6")
                .price(BigDecimal.TEN)
                .quantityOnHand(30)
                .build();
        list.add(product1);
        list.add(product2);
        list.add(product3);
        list.add(product4);
        list.add(product5);
        list.add(product6);
        return list;
    }
}
