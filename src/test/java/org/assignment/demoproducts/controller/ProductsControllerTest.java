package org.assignment.demoproducts.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.annotation.Before;
import org.assignment.demoproducts.config.SecurityConfig;
import org.assignment.demoproducts.domain.Product;
import org.assignment.demoproducts.exception.GlobalExceptionHandler;
import org.assignment.demoproducts.exception.ex.NotFoundException;
import org.assignment.demoproducts.model.ProductDTO;
import org.assignment.demoproducts.repository.ProductRepository;
import org.assignment.demoproducts.service.ProductService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.core.Is.is;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Created by sstefan
 * Date: 4/13/2024
 * Project: demo-products
 */
@SpringBootTest
class ProductsControllerTest {

    @Autowired
    ProductsController productsController;

    @Autowired
    ProductRepository productRepository;
    @Autowired
    ModelMapper mapper;

    @Autowired
    WebApplicationContext wac;

    @Autowired
    ObjectMapper objectMapper;
    MockMvc mockMvc;

    public static class BasicUser {
        static String username = "user";
        static String password = "user";
    }

    public static class AdminUser {
        static String username = "admin";
        static String password = "admin";
    }

    @BeforeEach()
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .apply(springSecurity())
                .build();
    }

    @Test
    void testSaveNewProductSuccess() throws Exception {

        ProductDTO productDTO = ProductDTO.builder()
                .productName("Test product")
                .description("Test description")
                .price(BigDecimal.TEN)
                .quantityOnHand(200)
                .build();

        mockMvc.perform(post(ProductsController.PRODUCTS_PATH)
                        .with(httpBasic(AdminUser.username, AdminUser.password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

    }

    @Test
    void testSaveNewProductInvalidUserAuthority() throws Exception {
        ProductDTO productDTO = ProductDTO.builder()
                .productName("Test product")
                .description("Test description")
                .price(BigDecimal.TEN)
                .quantityOnHand(200)
                .build();

        mockMvc.perform(post(ProductsController.PRODUCTS_PATH)
                        .with(httpBasic(BasicUser.username, BasicUser.password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isForbidden());

    }

    @Test
    public void testNewProductInvalidJson() throws Exception {
        //given
        String invalidJson = "{}";

        //when
        MvcResult result = mockMvc.perform(post(ProductsController.PRODUCTS_PATH)
                        .with(httpBasic(AdminUser.username, AdminUser.password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest()).andReturn();


        //then
        assertAll(
                () -> assertTrue(result.getResponse().getContentAsString().contains("\"price\":\"Field cannot be null\"")),
                () -> assertTrue(result.getResponse().getContentAsString().contains("\"productName\":\"Field cannot be null\"")),
                () -> assertTrue(result.getResponse().getContentAsString().contains("\"productName\":\"Field cannot be blank\""))
        );
    }

    @Test
    void testNewProductInvalidProductName() throws Exception{
        Map<String, Object> productMap = new HashMap<>();
        productMap.put("productName","test product name largeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
        productMap.put("price", "2000.00");

        MvcResult result = mockMvc.perform(post(ProductsController.PRODUCTS_PATH)
                .with(httpBasic(AdminUser.username,AdminUser.password))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productMap)))
                .andExpect(status().isBadRequest()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("\"productName\":\"size must be between 0 and 50\""));
    }

    @Test
    void testGetProductById() throws Exception {
        //given
        List<ProductDTO> productDTOS = BootstrapData.getData();
        Product product = productRepository.save(mapper.map(productDTOS.get(0), Product.class));
        UUID id = product.getId();


        mockMvc.perform(get(ProductsController.PRODUCTS_PATH_BY_ID, id)
                        .with(httpBasic(AdminUser.username, AdminUser.password))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(id.toString())));
    }

    @Test
    void testGetProductByIdNotFound() throws Exception{
        //given
        UUID uuid = UUID.randomUUID();

        //when
        mockMvc.perform(get(ProductsController.PRODUCTS_PATH_BY_ID,uuid)
                .with(httpBasic(AdminUser.username, AdminUser.password))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    void testFindAllProducts() throws Exception {
        //given
        List<ProductDTO> productDTOS = BootstrapData.getData();
        List<Product> products = productDTOS.stream()
                .map(productDTO -> mapper.map(productDTO, Product.class))
                .toList();
        productRepository.saveAll(products);

        mockMvc.perform(get(ProductsController.PRODUCTS_PATH)
                .with(httpBasic(BasicUser.username,BasicUser.password))
                .accept(MediaType.APPLICATION_JSON)
                .content(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()",Matchers.greaterThan(5)));
    }

    @Test
    void testUpdateProductByIdSuccess() throws Exception{
        //given
        ProductDTO dto = BootstrapData.getData().get(0);
        UUID productId = productRepository.save(mapper.map(dto, Product.class)).getId();
        ProductDTO updatedProduct = ProductDTO.builder()
                .productName("Product updated")
                .description("description updated")
                .price(BigDecimal.valueOf(200))
                .build();
        //when
        mockMvc.perform(put(ProductsController.PRODUCTS_PATH_BY_ID,productId)
                .with(httpBasic(AdminUser.username,AdminUser.password))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isNoContent());


    }

    @Test
    void testUpdateProductByIdNotFound() throws Exception {
        //given
        UUID id = UUID.randomUUID();
        ProductDTO updated = ProductDTO.builder()
                .productName("test product name")
                .price(BigDecimal.valueOf(1000))
                .build();

        mockMvc.perform(put(ProductsController.PRODUCTS_PATH_BY_ID,id)
                .with(httpBasic(AdminUser.username,AdminUser.password))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isNotFound());

    }

    @Test
    void testPatchProductByIdSuccess() throws Exception{

        //given

        ProductDTO dto = BootstrapData.getData().get(0);
        UUID productId = productRepository.save(mapper.map(dto, Product.class)).getId();
        ProductDTO patchedProduct = ProductDTO.builder()
                .productName("test product name")
                .price(BigDecimal.valueOf(1000))
                .build();

        //when
        mockMvc.perform(patch(ProductsController.PRODUCTS_PATH_BY_ID,productId)
                .with(httpBasic(AdminUser.username,AdminUser.password))
                .content(objectMapper.writeValueAsString(patchedProduct))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

    }

    @Test
    void testPatchProductNotFound() throws Exception {
        //given
        UUID id = UUID.randomUUID();
        ProductDTO updated = ProductDTO.builder()
                .productName("test product name")
                .price(BigDecimal.valueOf(1000))
                .build();

        mockMvc.perform(patch(ProductsController.PRODUCTS_PATH_BY_ID,id)
                        .with(httpBasic(AdminUser.username,AdminUser.password))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateProductPriceById() throws Exception{
        //given
        ProductDTO dto = BootstrapData.getData().get(0);
        BigDecimal initialPrice = dto.getPrice();
        UUID productId = productRepository.save(mapper.map(dto, Product.class)).getId();

        mockMvc.perform(patch(ProductsController.PRODUCT_PRICE_UPDATE_ID,productId)
                .queryParam("price", String.valueOf(BigDecimal.valueOf(200)))
                .with(httpBasic(AdminUser.username,AdminUser.password))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteProductById() throws Exception{
        //given
        List<ProductDTO> productDTOS = BootstrapData.getData();
        List<Product> products = productDTOS.stream()
                .map(productDTO -> mapper.map(productDTO, Product.class))
                .toList();
        List<Product> list = productRepository.saveAll(products);
        UUID id = list.get(0).getId();

       mockMvc.perform(delete(ProductsController.PRODUCTS_PATH_BY_ID,id)
               .with(httpBasic(AdminUser.username,AdminUser.password))
               .contentType(MediaType.APPLICATION_JSON)
               .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isNoContent());
    }
}