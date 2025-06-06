package kr.hhplus.be.server.domain.user;

public enum UserPointErrorCode {
    CHARGE_AMOUNT_MUST_BE_POSITIVE("충전 금액은 0보다 커야 합니다."),
    CHARGE_AMOUNT_LIMIT_EXCEEDED("1회 충전 한도를 초과했습니다."),
    TOTAL_POINT_LIMIT_EXCEEDED("충전 시 보유 포인트가 최대 한도를 초과합니다."),
    USAGE_AMOUNT_MUST_BE_POSITIVE("사용 금액은 0보다 커야 합니다."),
    INSUFFICIENT_BALANCE("잔액이 부족합니다."),
    CONCURRENCY_CHARGE_POINT("동시에 충전 요청이 처리되어 충돌이 발생했습니다."),
    CONCURRENCY_USE_POINT("동시에 사용 요청이 처리되어 충돌이 발생했습니다.");

    private final String message;

    UserPointErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
