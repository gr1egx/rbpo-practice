package ru.mtuci.rbpo_practice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mtuci.rbpo_practice.models.*;
import ru.mtuci.rbpo_practice.services.ProductService;

import java.util.Objects;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('modification')")
    public ResponseEntity<String> createProduct(@RequestBody ProductCreateRequest request) {
        try {

            Long id = productService.createProduct(request.getName(), request.getIsBlocked());

            return ResponseEntity.status(HttpStatus.OK).body("Product created successfully.\nID: " + id);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong.");
        }
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyAuthority('modification')")
    public ResponseEntity<String> updateProduct(@RequestBody ProductUpdateRequest request) {
        try {

            String result = productService.updateProduct(request.getProductId(), request.getName(), request.getIsBlocked());
            if (!Objects.equals(result, "OK")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(result);
            }

            return ResponseEntity.status(HttpStatus.OK).body("Successfully updated product.");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong.");
        }
    }
}
