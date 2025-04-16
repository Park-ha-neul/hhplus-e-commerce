package kr.hhplus.be.server.application.product.usecase;

import kr.hhplus.be.server.domain.common.PeriodType;
import kr.hhplus.be.server.domain.product.*;

import java.time.LocalDate;
import java.util.Optional;

public class CreateTopProduct {

    private final TopProductService topProductService;
    private final ProductService productService;
    private final TopProductRepository topProductRepository;

    public CreateTopProduct(TopProductService topProductService, ProductService productService, TopProductRepository topProductRepository) {
        this.topProductService = topProductService;
        this.productService = productService;
        this.topProductRepository = topProductRepository;
    }

    public void registerOrUpdateTopProduct(Long productId, PeriodType type, Long count){
        LocalDate calculatedDate = TopProduct.calculateDate(type);

        Optional<TopProduct> topProduct = topProductRepository.findByProductId(productId);

        if (topProduct.isPresent()) {
            TopProduct existingTopProduct = topProduct.get();
            existingTopProduct.addCount(count);
            topProductRepository.save(existingTopProduct);
        } else {
            Product product = productService.getProductDetails(productId);
            topProductService.registerTopProduct(productId, type, count);
        }
    }
}
