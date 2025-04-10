package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.common.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByStatus(ProductStatus status);
}
