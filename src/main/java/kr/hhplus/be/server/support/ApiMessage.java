package kr.hhplus.be.server.support;

public class ApiMessage {
    public static final String CREATE_SUCCESS = "생성 완료";
    public static final String VIEW_SUCCESS = "조회 성공";
    public static final String CHARGE_SUCCESS = "충전 완료";
    public static final String ISSUED_SUCCESS = "발급 완료";
    public static final String USE_SUCCESS = "사용 완료";
    public static final String CANCLE_SUCCESS = "취소 완료";
    public static final String PAYMENT_SUCCESS = "결제 완료";

    // error
    public static final String FORBIDDEN_ACCESS = "권한이 없습니다.";
    public static final String INVALID_USER = "정의되지 않은 사용자입니다.";
    public static final String SERVER_ERROR = "서버 오류가 발생하였습니다.";
}
