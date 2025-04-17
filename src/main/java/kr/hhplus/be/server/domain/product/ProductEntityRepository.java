package kr.hhplus.be.server.domain.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductEntityRepository extends JpaRepository<ProductEntity, Long> {
    List<ProductEntity> findAllByStatus(ProductStatus status);
}
