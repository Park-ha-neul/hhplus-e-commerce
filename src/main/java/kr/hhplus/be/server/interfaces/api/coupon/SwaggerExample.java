package kr.hhplus.be.server.interfaces.api.coupon;

public class SwaggerExample {
    public static final String CREATE_COUPON_SUCCESS = """
        {
            "code": 200,
            "message": "SUCCESS",
            "data": {
                "couponId": 1,
                "couponName": "10% 할인 쿠폰",
                "totalQuantity": 1000,
                "discountRate": 10,
                "discountAmount": null,
                "status": "active",
                "startDate": "2025-04-10T00:00:00Z",
                "endDate": "2025-12-31T23:59:59Z"
            }
        }
    """;

    public static final String EMPTY_COUPON_NAME= """
        {
            "code": "400",
            "message": "쿠폰명이 누락되었습니다. 필수 값을 확인해주세요."
        }
    """;

    public static final String EMPTY_COUPON_AMOUNT= """
        {
            "code": "400",
            "message": "쿠폰 총 수량이 누락되었습니다. 필수 값을 확인해주세요."
        }
    """;

    public static final String EMPTY_DISCOUNT_RATE_OR_DISCOUNT_AMOUNT= """
        {
            "code": "400",
            "message": "할인율 or 할인 금액을 입력해주세요."
        }
    """;

    public static final String EMPTY_COUPON_USECASE= """
        {
            "code": "400",
            "message": "쿠폰 사용 기간이 누락되었습니다. 필수 값을 확인해주세요."
        }
    """;

    public static final String SERVER_ERROR = """
        {
            "code": "500",
             "message": "서버 에러"
        }
    """;
}
