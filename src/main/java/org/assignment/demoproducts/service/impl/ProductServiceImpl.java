package org.assignment.demoproducts.service.impl;

import lombok.RequiredArgsConstructor;
import org.assignment.demoproducts.domain.Product;
import org.assignment.demoproducts.model.ProductDTO;
import org.assignment.demoproducts.repository.ProductRepository;
import org.assignment.demoproducts.service.ProductService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Created by sstefan
 * Date: 4/13/2024
 * Project: demo-products
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ModelMapper mapper;

    @Override
    public ProductDTO saveNewProduct(ProductDTO productDTO) {
        return mapper.map(
                productRepository.save(
                        mapper.map(productDTO, Product.class)),
                ProductDTO.class);
    }

    @Override
    public Optional<ProductDTO> getProductById(UUID productId) {
        Product product = productRepository.findById(productId).orElse(null);
        return Optional.ofNullable(
                product == null ? null : mapper.map(product, ProductDTO.class));
    }

    @Override
    public List<ProductDTO> findAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(product -> mapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Optional<ProductDTO> updateById(UUID id, ProductDTO productDTO) {
        AtomicReference<Optional<ProductDTO>> atomicReference = new AtomicReference<>();

        productRepository.findById(id).ifPresentOrElse(foundProduct -> {
            foundProduct.setProductName(productDTO.getProductName());
            foundProduct.setDescription(productDTO.getDescription());
            foundProduct.setQuantityOnHand(productDTO.getQuantityOnHand());
            foundProduct.setPrice(productDTO.getPrice());
            atomicReference.set(Optional.of(
                    mapper.map(productRepository.save(foundProduct), ProductDTO.class)
            ));
        }, () -> atomicReference.set(Optional.empty()));

        return atomicReference.get();
    }

    @Override
    @Transactional
    public Optional<ProductDTO> patchProductById(UUID id, ProductDTO productDTO) {
        AtomicReference<Optional<ProductDTO>> atomicReference = new AtomicReference<>();
        productRepository.findById(id).ifPresentOrElse(foundProduct -> {
            if (StringUtils.hasText(productDTO.getProductName())) {
                foundProduct.setProductName(productDTO.getProductName());
            }
            if (StringUtils.hasText(productDTO.getDescription())) {
                foundProduct.setDescription(productDTO.getDescription());
            }
            if (productDTO.getPrice() != null) {
                foundProduct.setPrice(productDTO.getPrice());
            }
            if (productDTO.getQuantityOnHand() != null) {
                foundProduct.setQuantityOnHand(productDTO.getQuantityOnHand());
            }
            atomicReference.set(Optional.of(
                    mapper.map(productRepository.save(foundProduct),
                            ProductDTO.class)));
        }, () -> atomicReference.set(Optional.empty()));

        return atomicReference.get();
    }

    @Override
    @Transactional
    public Optional<ProductDTO> updatePriceById(UUID productId, BigDecimal price) {
        AtomicReference<Optional<ProductDTO>> atomicReference = new AtomicReference<>();
        productRepository.findById(productId).ifPresentOrElse( product -> {
            product.setPrice(price);
            atomicReference.set(Optional.of(mapper.map(
                    productRepository.save(product), ProductDTO.class
            )));
        }, () -> atomicReference.set(Optional.empty()));

        return atomicReference.get();
    }

    @Override
    public Boolean deleteProductById(UUID productId) {
        if (productRepository.existsById(productId)){
            productRepository.deleteById(productId);
            return true;
        }
        return false;
    }
}
