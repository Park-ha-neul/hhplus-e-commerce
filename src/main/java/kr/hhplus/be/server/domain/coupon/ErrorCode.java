package kr.hhplus.be.server.domain.coupon;

public enum ErrorCode {
    COUPON_ISSUED_EXCEED("쿠폰 발급 한도를 초과했습니다."),
    COUPON_TYPE_NOT_FOUND("유효하지 않은 할인 타입입니다."),
    DISCOUNT_RATE_NOT_FOUND("할인율이 필요합니다."),
    DISCOUNT_AMOUNT_NOT_FOUND("할인금액이 필요합니다."),
    ALREADY_ISSUED_COUPON("이미 발급된 쿠폰입니다."),
    ALREADY_USED_COUPON("이미 사용된 쿠폰입니다."),
    USER_COUPON_NOT_FOUND("유저 쿠폰이 존재하지 않습니다."),
    INACTIVE_COUPON("사용 불가한 쿠폰입니다."),
    COUPON_NOT_FOUND("존재하지 않는 쿠폰입니다."),
    CREATE_COUPON_MUST_BE_ADMIN("쿠폰은 등록은 관리자만 가능합니다.");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
