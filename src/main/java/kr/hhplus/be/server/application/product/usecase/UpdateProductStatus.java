package kr.hhplus.be.server.application.product.usecase;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductService;

public class UpdateProductStatus {

    private final ProductService productService;


    public UpdateProductStatus(ProductService productService) {
        this.productService = productService;
    }

    public void updateProductStatus(Long productId, Long amount){
        Product product = productService.getProductDetails(productId);

        if (amount > 0) {
            product.increaseBalance(productId, amount);
            productService.increaseProductBalance(productId, amount);
        } else if (amount < 0) {
            product.decreaseBalance(productId, Math.abs(amount));
            productService.decreaseProductBalance(productId, Math.abs(amount));
        }
    }
}
