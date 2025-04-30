package kr.hhplus.be.server.domain.user;

public enum UserErrorCode {
    USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
    INVALID_USER_ID("잘못된 사용자 ID입니다."),
    USER_CREATE_ERROR("사용자 생성 중 오류가 발생하였습니다.");

    private final String message;

    UserErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
