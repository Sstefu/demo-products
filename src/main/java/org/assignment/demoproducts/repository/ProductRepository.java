package org.assignment.demoproducts.repository;

import org.assignment.demoproducts.domain.Product;
import org.assignment.demoproducts.service.ProductService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Created by sstefan
 * Date: 4/13/2024
 * Project: demo-products
 */
public interface ProductRepository extends JpaRepository<Product, UUID> {

}
