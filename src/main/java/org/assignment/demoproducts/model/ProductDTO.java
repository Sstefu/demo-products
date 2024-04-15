package org.assignment.demoproducts.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.assignment.demoproducts.constants.ErrorMessages;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Created by sstefan
 * Date: 4/13/2024
 * Project: demo-products
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private UUID id;
    @NotNull(message = ErrorMessages.NOT_NULL)
    @NotBlank(message = ErrorMessages.NOT_BLANK)
    @Size(max = 50)
    private String productName;

    private String description;
    private Integer quantityOnHand;

    @NotNull(message = ErrorMessages.NOT_NULL)
    private BigDecimal price;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
