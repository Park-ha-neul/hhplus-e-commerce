package kr.hhplus.be.server.interfaces.api.payment;

public class SwaggerExample {
    public static final String CREATE_PAYMENT_SUCCESS = """
        {
            "code": 200,
            "message": "SUCCESS",
            "data": {
                "paymentId": 1,
                "odreId": 1,
                "totalAmount": 1000,
                "status": "pending",
                "description": "",
                "createDate": "2024-04-02T12:00:00Z"
            }
        }
    """;

    public static final String EMPTY_ORDER = """
        {
            "code": "400",
            "message": "주문 id가 누락되었습니다. 필수 값을 확인해주세요."
        }
    """;

    public static final String INTERNAL_SERVER_ERROR = """
        {
            "code":  "500",
            "message": "서버 에러"
        }
    """;

    public static final String SUCCESS_PAYMENT = """
        {
            "code": 200,
            "message": "SUCCESS",
            "data": {
                "paymentId": 1,
                "odreId": 1,
                "totalAmount": 1000,
                "status": "success",
                "createDate": "2024-04-02T12:00:00Z",
                "updateDate": "2024-04-02T12:00:00Z",
                "products": [
                    {"productId": 1, "amount": 2},
                    {"productId": 2, "amount": 3}
                ]
            }
        }
    """;

    public static final String PAYMENT_NOT_FOUND = """
        {
            "code": "400",
            "message": "정의되지 않은 결제 id 입니다."
        }
    """;

    public static final String PAYMENT_FAIL_NOT_ENOUGH_BALANCE = """
       {
            "code": "400",
            "message": "잔액이 부족합니다. 충전 후 다시 이용해주세요."
        }
    """;

    public static final String GET_PAYMENTS_SUCCESS = """
        {
             "code": 200,
             "message": "SUCCESS",
             "data": [{
                 "paymentId": 1,
                 "odreId": 1,
                 "totalAmount": 1000,
                 "status": "success",
                 "createDate": "2024-04-02T12:00:00Z",
                 "updateDate": "2024-04-02T12:00:00Z",
                 "products": [
                     {"productId": 1, "amount": 2},
                     {"productId": 2, "amount": 3}
                 ]},{}...{}
         }
         ""\"),
    """;

    public static final String GET_PAYEMNT_DETAIL = """
        {
            "code": 200,
            "message": "SUCCESS",
            "data": [{
                "paymentId": 1,
                "odreId": 1,
                "totalAmount": 1000,
                "status": "success",
                "createDate": "2024-04-02T12:00:00Z",
                "updateDate": "2024-04-02T12:00:00Z",
                "products": [
                    {"productId": 1, "amount": 2},
                    {"productId": 2, "amount": 3}
            ]}
        }
    """;
}
