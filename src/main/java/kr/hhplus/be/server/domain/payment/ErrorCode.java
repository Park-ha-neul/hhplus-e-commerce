package kr.hhplus.be.server.domain.payment;

public enum ErrorCode {
    PAYMENT_NOT_FOUND("결제가 존재하지 않습니다."),
    PAYMENT_PROCESS_ERROR("결제 처리 중 오류 발생");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
