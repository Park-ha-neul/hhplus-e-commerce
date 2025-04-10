package kr.hhplus.be.server.application.service.product;

import kr.hhplus.be.server.domain.common.ProductStatus;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // 상품 조회
    public Product getProduct(Long productId){
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));
    }

    // 판매 가능한 상품 목록 조회
    public List<Product> getAvailableProducts(){
        return productRepository.findByStatus(ProductStatus.AVAILABLE);
    }

    // 상품 상태 업데이트
    public void updateProductStatus(Long productId, ProductStatus status){
        Product product = getProduct(productId);
        product.updateStatus(status);
        productRepository.save(product);
    }

    // 상품 등록
    public Product createProduct(Product product){
        return productRepository.save(product);
    }

    // 상품 삭제
    public void deleteProduct(Long productId){
        Product product = getProduct(productId);
        productRepository.delete(product);
    }
}
