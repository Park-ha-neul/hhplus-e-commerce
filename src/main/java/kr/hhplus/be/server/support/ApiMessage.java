package kr.hhplus.be.server.support;

public class ApiMessage {
    public static final String CREATE_SUCCESS = "등록되었습니다.";
    public static final String VIEW_SUCCESS = "조회에 성공하였습니다.";
    public static final String CHARGE_SUCCESS = "충전이 완료되었습니다.";
    public static final String ISSUED_SUCCESS = "발급이 완료되었습니다.";
    public static final String USE_SUCCESS = "사용되었습니다.";
    public static final String CANCLE_SUCCESS = "취소되었습니다.";
    public static final String PAYMENT_SUCCESS = "결제가 완료되었습니다.";

    // error
    public static final String FORBIDDEN_ACCESS = "권한이 없습니다.";
    public static final String INVALID_USER = "정의되지 않은 사용자입니다.";
    public static final String INVALID_COUPON = "정의되지 않은 쿠폰입니다.";
    public static final String CREATE_USER_ERROR = "사용자 생성 중 오류가 발생했습니다.";
    public static final String SERVER_ERROR = "서버 오류가 발생하였습니다.";
}
