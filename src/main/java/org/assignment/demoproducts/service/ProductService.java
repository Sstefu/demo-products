package org.assignment.demoproducts.service;

import org.assignment.demoproducts.model.ProductDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by sstefan
 * Date: 4/13/2024
 * Project: demo-products
 */
public interface ProductService {
    ProductDTO saveNewProduct(ProductDTO productDTO);

    Optional<ProductDTO> getProductById(UUID productId);

    List<ProductDTO> findAllProducts();

    Optional<ProductDTO> updateById(UUID id, ProductDTO productDTO);

    Optional<ProductDTO> patchProductById(UUID id, ProductDTO productDTO);

    Optional<ProductDTO> updatePriceById(UUID productId, BigDecimal price);

    Boolean deleteProductById(UUID productId);
}
