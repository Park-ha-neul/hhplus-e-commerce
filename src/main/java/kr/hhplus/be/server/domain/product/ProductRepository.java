package kr.hhplus.be.server.domain.product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository{
    Product save (Product product);
    Optional<Product> findByIdForUpdate(Long productId);
    Optional<Product> findById(Long productId);
    List<Product> findAllByStatus(Product.ProductStatus status);
    List<Product> findAll();
}
