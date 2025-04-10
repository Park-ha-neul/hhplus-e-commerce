package kr.hhplus.be.server.application.service.product;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.common.ProductStatus;
import kr.hhplus.be.server.domain.product.Balance;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import org.apache.tomcat.util.net.jsse.JSSEUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BalanceService {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    // 재고 조회
    public Long getStock(Long productId){
        Product product = productService.getProduct(productId);
        return product.getBalance().getQuantity();
    }

    // 재고 감소
    @Transactional
    public void decreaseStock(Long productId, Long amount){
        Product product = productService.getProduct(productId);
        Balance balance = new Balance();
        balance.decrease(amount);

        product.setBalance(balance);

        if(balance.isSoldOut()) {
            productService.updateProductStatus(productId, ProductStatus.SOLD_OUT);
        } else{
            productService.updateProductStatus(productId, ProductStatus.AVAILABLE);
        }

        productRepository.save(product);
    }

    // 재고 증가
    @Transactional
    public void increaseStock(Long productId, Long amount){
        Product product = productService.getProduct(productId);
        Balance balance = new Balance();
        balance.increase(amount);

        product.setBalance(balance);

        if(balance.isSoldOut()) {
            productService.updateProductStatus(productId, ProductStatus.SOLD_OUT);
        } else{
            productService.updateProductStatus(productId, ProductStatus.AVAILABLE);
        }

        productRepository.save(product);
    }
}
