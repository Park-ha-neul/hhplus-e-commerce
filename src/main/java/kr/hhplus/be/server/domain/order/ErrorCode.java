package kr.hhplus.be.server.domain.order;

public enum ErrorCode {
    ORDER_NOT_FOUND("주문이 존재하지 않습니다."),
    ORDER_ITEM_NOT_FOUND("주문에 해당하는 상세 내역이 존재하지 않습니다.");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
