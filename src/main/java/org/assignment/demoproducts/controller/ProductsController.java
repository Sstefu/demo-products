package org.assignment.demoproducts.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.assignment.demoproducts.exception.ex.NotFoundException;
import org.assignment.demoproducts.model.ProductDTO;
import org.assignment.demoproducts.service.ProductService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.SQLOutput;
import java.util.List;
import java.util.UUID;

/**
 * Created by sstefan
 * Date: 4/13/2024
 * Project: demo-products
 */
@RestController
@RequiredArgsConstructor
public class ProductsController {
    public static final String PRODUCTS_PATH = "/api/v1/products";
    public static final String PRODUCTS_PATH_BY_ID = PRODUCTS_PATH + "/{productId}";
    public static final String PRODUCT_PRICE_UPDATE_ID = PRODUCTS_PATH + "/price/{productId}";
    private final ProductService productService;

    @PostMapping(PRODUCTS_PATH)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> handlePost(@Valid @RequestBody ProductDTO productDTO) {
        ProductDTO savedProduct = productService.saveNewProduct(productDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", PRODUCTS_PATH + "/" + savedProduct.getId().toString());

        return new ResponseEntity<>(savedProduct, headers, HttpStatus.CREATED);
    }

    @PutMapping(PRODUCTS_PATH_BY_ID)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<HttpStatus> updateProductById(@PathVariable("productId") UUID productId, @Valid @RequestBody ProductDTO productDTO) {
        if (productService.updateById(productId, productDTO).isEmpty()) {
            throw new NotFoundException();
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(PRODUCTS_PATH_BY_ID)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<HttpStatus> updateProductPatchById(@PathVariable("productId") UUID productId, @Valid @RequestBody ProductDTO productDTO) {
        if (productService.patchProductById(productId, productDTO).isEmpty()) {
            throw new NotFoundException();
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(PRODUCT_PRICE_UPDATE_ID)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<HttpStatus> updateProductPrice(@PathVariable("productId") UUID productId,
                                                         @RequestParam("price") BigDecimal price) {
        if (productService.updatePriceById(productId, price).isEmpty()) {
            throw new NotFoundException();
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(PRODUCTS_PATH_BY_ID)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<HttpStatus> deleteProductById(@PathVariable("productId") UUID productId) {
        if (!productService.deleteProductById(productId)) {
            throw new NotFoundException();
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(PRODUCTS_PATH_BY_ID)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")
    public ProductDTO getProductById(@PathVariable("productId") UUID productId) {
        return productService.getProductById(productId).orElseThrow(NotFoundException::new);
    }

    @GetMapping(PRODUCTS_PATH)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")
    public List<ProductDTO> getAllProducts() {
        return productService.findAllProducts();
    }
}
