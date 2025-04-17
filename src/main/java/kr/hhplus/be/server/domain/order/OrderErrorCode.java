package kr.hhplus.be.server.domain.order;

public enum OrderErrorCode {
    ORDER_NOT_FOUND("주문이 존재하지 않습니다."),
    ORDER_ITEM_NOT_FOUND("주문에 해당하는 상세 내역이 존재하지 않습니다.");

    private final String message;

    OrderErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
