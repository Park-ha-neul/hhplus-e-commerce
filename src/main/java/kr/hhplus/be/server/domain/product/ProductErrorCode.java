package kr.hhplus.be.server.domain.product;

public enum ProductErrorCode {
    PRODUCT_PRICE_MUST_BE_POSITIVE("상품 가격은 0보다 커야 합니다."),
    PRODUCT_STOCK_MUST_BE_POSITIVE("재고는 0 이상이어야 합니다."),
    VALIDATE_PRODUCT_STATUS_STOCK("재고가 없는 상품은 판매 상태일 수 없습니다."),
    DECREASE_MUST_BE_POSITIVE("차감량은 0보다 커야 합니다."),
    INCREASE_MUST_BE_POSITIVE("증가량은 0보다 커야 합니다."),
    NOT_ENOUGH_STOCK("재고 부족"),
    PRODUCT_NOT_FOUND("상품이 존재하지 않습니다."),
    TOP_PRODUCT_PERIOD_NOT_FOUND("정의되지 않은 날짜 기준입니다.."),
    CREATE_PRODUCT_MUST_BE_ADMIN("상품 등록은 관리자만 가능합니다.");

    private final String message;

    ProductErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
