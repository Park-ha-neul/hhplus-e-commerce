package kr.hhplus.be.server.infrastructure.product;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductJpaRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByStatus(Product.ProductStatus status);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.productId IN :productIds ORDER BY p.productId ASC")
    Optional<Product> findByIdForUpdate(@Param("productId") Long productId);
}
