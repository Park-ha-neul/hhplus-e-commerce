package kr.hhplus.be.server.domain.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.support.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public ProductResult createProduct(ProductCommand command, Long userId){
        User user = userRepository.findById(userId);

        if(!user.isAdmin()){
            throw new ForbiddenException(ProductErrorCode.CREATE_PRODUCT_MUST_BE_ADMIN.getMessage());
        }

        Product product = Product.create(command);
        productRepository.save(product);
        return ProductResult.of(product);
    }

    public Product getProduct(Long productId){
        String cacheKey = "product:detail:" + productId;

        Object cachedObj = redisTemplate.opsForValue().get(cacheKey);
        if (cachedObj != null) {
            if (cachedObj instanceof Product) {
                return (Product) cachedObj;
            } else if (cachedObj instanceof LinkedHashMap) {
                return objectMapper.convertValue(cachedObj, Product.class);
            } else {
                throw new IllegalStateException("Unexpected cache type: " + cachedObj.getClass());
            }
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException(ProductErrorCode.PRODUCT_NOT_FOUND.getMessage()));

        redisTemplate.opsForValue().set(cacheKey, product, Duration.ofMinutes(10));

        return product;
    }

    public List<Product> getProducts(Product.ProductStatus status){
        if (status != null){
            return productRepository.findAllByStatus(status);
        } else {
            return productRepository.findAll();
        }
    }

    public void increaseProductBalance(Long productId, Long amount) {
        Product product = getProduct(productId);
        product.increaseBalance(amount);
        productRepository.save(product);
    }

    @Transactional
    public void decreaseProductStock(Long productId, Long amount) {
        Product product = productRepository.findByIdForUpdate(productId)
                .orElseThrow(() -> new IllegalArgumentException(ProductErrorCode.PRODUCT_NOT_FOUND.getMessage()));

        product.decreaseBalance(amount);
        productRepository.save(product);
    }
}
