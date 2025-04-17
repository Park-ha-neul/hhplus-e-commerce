package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.user.UserPointEntity;
import kr.hhplus.be.server.domain.user.UserPointEntityRepository;
import kr.hhplus.be.server.domain.user.UserPointErrorCode;
import kr.hhplus.be.server.interfaces.api.product.ProductRequest;
import kr.hhplus.be.server.support.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductEntityRepository productEntityRepository;
    private final UserPointEntityRepository userPointEntityRepository;

    public ProductEntity createProduct(ProductRequest request, Long userId){
        UserPointEntity userPointEntity = userPointEntityRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(UserPointErrorCode.USER_NOT_FOUND.getMessage()));

        if(!userPointEntity.getUser().isAdmin()){
            throw new ForbiddenException(ProductErrorCode.CREATE_PRODUCT_MUST_BE_ADMIN.getMessage());
        }

        ProductEntity productEntity = ProductEntity.create(request);
        return productEntityRepository.save(productEntity);
    }

    public ProductEntity getProductDetails(Long productId){
        return productEntityRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException(ProductErrorCode.PRODUCT_NOT_FOUND.getMessage()));
    }

    public List<ProductEntity> getProducts(ProductStatus status){
        if (status != null){
            return productEntityRepository.findAllByStatus(status);
        } else {
            return productEntityRepository.findAll();
        }
    }

    public void increaseProductBalance(Long productId, Long quantity) {
        ProductEntity productEntity = getProductDetails(productId);
        productEntity.increaseBalance(productId, quantity);
        productEntityRepository.save(productEntity);
    }

    public void decreaseProductBalance(Long productId, Long quantity) {
        ProductEntity productEntity = getProductDetails(productId);
        productEntity.decreaseBalance(productId, quantity);
        productEntityRepository.save(productEntity);
    }
}
