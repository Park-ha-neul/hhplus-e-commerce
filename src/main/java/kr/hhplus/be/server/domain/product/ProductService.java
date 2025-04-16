package kr.hhplus.be.server.domain.product;

import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository productRepository) {
        this.repository = productRepository;
    }

    public Product createProduct(String name, String description, Long price, Balance balance, ProductStatus status) {
        Product product = Product.create(name, description, price, balance, status);
        return repository.save(product);
    }

    public Product getProductDetails(Long productId){
        return repository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));
    }

    public Balance getQuntity(Long productId){
        Product product = getProductDetails(productId);
        return product.getBalance();
    }

    public void increaseProductBalance(Long productId, Long quantity) {
        Product product = getProductDetails(productId);
        product.increaseBalance(productId, quantity);
        repository.save(product);
    }

    public void decreaseProductBalance(Long productId, Long quantity) {
        Product product = getProductDetails(productId);
        product.decreaseBalance(productId, quantity);
        repository.save(product);
    }
}
