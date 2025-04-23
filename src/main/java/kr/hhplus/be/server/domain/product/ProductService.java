package kr.hhplus.be.server.domain.product;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.interfaces.api.product.ProductRequest;
import kr.hhplus.be.server.support.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public Product createProduct(ProductRequest request, Long userId){
        User user = userRepository.findById(userId);

        if(!user.isAdmin()){
            throw new ForbiddenException(ProductErrorCode.CREATE_PRODUCT_MUST_BE_ADMIN.getMessage());
        }

        Product product = Product.create(request);
        return productRepository.save(product);
    }

    public Product getProductDetails(Long productId){
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException(ProductErrorCode.PRODUCT_NOT_FOUND.getMessage()));
    }

    public List<Product> getProducts(Product.ProductStatus status){
        if (status != null){
            return productRepository.findAllByStatus(status);
        } else {
            return productRepository.findAll();
        }
    }

    public void increaseProductBalance(Long productId, Long amount) {
        Product product = getProductDetails(productId);
        product.increaseBalance(amount);
        productRepository.save(product);
    }

    @Transactional
    public void decreaseProductBalance(Long productId, Long amount) {
        Product product = productRepository.findByIdForUpdate(productId)
                .orElseThrow(() -> new IllegalArgumentException(ProductErrorCode.PRODUCT_NOT_FOUND.getMessage()));

        product.decreaseBalance(amount);
        productRepository.save(product);
    }
}
