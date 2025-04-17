package kr.hhplus.be.server.interfaces.api.point;

public class SwaggerExample {
    public static final String CHARGE_POINT_SUCCESS = """
        {
            "code": 200,
            "message": "SUCCESS",
            "data": {
                "userId": 123,
                "chargedAmount": 5000,
                "newBalance": 6000
            }
        }
    """;

    public static final String ERROR_USER_NOT_FOUND = """
        {
            "code": "400",
            "message": "정의되지 않은 사용자입니다."
        }
    """;

    public static final String ERROR_CHARGE_LIMIT_EXCEEDED = """
        {
            "code": "400",
            "message": "1회 최대 충전량(100,000)을 초과하였습니다."
        }
    """;

    public static final String ERROR_CHARGE_TOTAL_PRICE_EXCEEDED = """
        {
            "code": "400",
            "message": "1인 사용자의 최대 한도(1,000,000)를 초과하였습니다."
        }
    """;

    public static final String SERVER_ERROR = """
        {
            "code": "500",
             "message": "서버 에러"
        }
    """;

    public static final String USE_POINT_SUCCESS = """
        {
            "code": 200,
            "message": "SUCCESS",
            "data": {
                "userId": 123,
                "usedAmount": 2000,
                "newBalance": 6000
            }
        }
    """;

    public static final String NOT_ENOUGH_BALANCE = """
        {
            "code": "400",
            "message": "포인트 잔액이 부족합니다."
        }
    """;

    public static final String GET_HISTORY_SUCCESS = """
        {
            "code": 200,
            "message": "SUCCESS",
            "data": [{
             "historyId": 1,
             "amount": 200, // 변동된 포인트
             "balanceBefore": 2000, // 변경 전 포인트 잔액\s
             "balanceAfter": 1800, // 변경 후 포인트 잔액\s
             "transcation": "use",
             "createDate": "2024-04-02T12:00:00Z"
           }, {
             "historyId": 1,
             "amount": 200, // 변동된 포인트
             "balanceBefore": 2000, // 변경 전 포인트 잔액\s
             "balanceAfter": 1800, // 변경 후 포인트 잔액\s
             "transcation": "charge",
             "createDate": "2024-04-02T12:00:00Z"
           }]
        }
    """;
}
