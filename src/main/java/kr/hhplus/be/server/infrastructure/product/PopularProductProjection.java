package kr.hhplus.be.server.infrastructure.product;

public interface PopularProductProjection {
    Long getProductId();
    String getProductName();
    Long getPrice();
    Long getTotalCount();
    Integer getRanking();
}
