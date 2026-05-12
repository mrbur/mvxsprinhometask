package org.example.mvxsprinhometask.servise;

import org.example.mvxsprinhometask.entity.Product;
import org.example.mvxsprinhometask.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    @Transactional
    public Product createProduct(Product product) {
        if(product == null || product.getPrice() == null || product.getName() == null) {
            throw new IllegalArgumentException("bad product data");
        }
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long id, Product productDetails) {
        if(productDetails == null || productDetails.getPrice() == null || productDetails.getName() == null) {
            throw new IllegalArgumentException("bad product data");
        }
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));

        if (productDetails.getName() != null) {
            existingProduct.setName(productDetails.getName());
        }
        if (productDetails.getDescription() != null) {
            existingProduct.setDescription(productDetails.getDescription());
        }
        if (productDetails.getCount() != null) {
            existingProduct.setCount(productDetails.getCount());
        }
        if (productDetails.getPrice() != null) {
            existingProduct.setPrice(productDetails.getPrice());
        }
        return productRepository.save(existingProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        if(id == null || id < 0) {
            throw new IllegalArgumentException("bad product id");
        }
        if (!productRepository.existsById(id)) {
            throw new NoSuchElementException("Cannot delete. Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<Product> getAllProducts(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return productRepository.findAll(pageable);
    }


    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        if(id == null || id < 0) {
            throw new IllegalArgumentException("bad product id");
        }
        return productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));
    }
}