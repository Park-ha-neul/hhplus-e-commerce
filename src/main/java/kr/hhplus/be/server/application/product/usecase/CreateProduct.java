package kr.hhplus.be.server.application.product.usecase;

import kr.hhplus.be.server.domain.product.Balance;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.product.ProductStatus;
import kr.hhplus.be.server.domain.user.User;

import java.nio.file.AccessDeniedException;

public class CreateProduct {

    private final ProductService productService;


    public CreateProduct(ProductService productService) {
        this.productService = productService;
    }

    public void create(User user, String name, String description, Long price, Long amount, ProductStatus status) throws AccessDeniedException {
        if(!user.isAdmin()){
            throw new AccessDeniedException("관리자만 상품을 등록할 수 있습니다.");
        }

        Balance balance = Balance.create(amount);
        productService.createProduct(name, description, price, balance, status);
    }
}
