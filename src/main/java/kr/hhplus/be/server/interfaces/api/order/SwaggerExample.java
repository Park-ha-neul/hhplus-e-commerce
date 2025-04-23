package kr.hhplus.be.server.interfaces.api.order;

public class SwaggerExample {

    public static final String GET_ORDERS_SUCCESS = """
       {
        "code": 200,
        "message": "SUCCESS",
        "data": [{
            "orderId": 1,
            "userId": 1,
            "status": "pending",
            "createDate": "2024-04-02T12:00:00Z"
           },{}...]
    }
    """;

    public static final String SERVER_ERROR = """
       {
            "code": "500",
             "message": "서버 에러"
       }
    """;

    public static final String GET_ORDER_DETAIL_SUCCESS = """
       {
            "code": 200,
            "message": "SUCCESS",
            "data": {
                "orderId": 1,
                "userId": 1,
                "status": "pending",
                "createDate": "2024-04-02T12:00:00Z"
               }
        }
    """;

    public static final String ORDER_NOT_FOUND = """
       {
            "code": "400",
             "message": "정의되지 않은 주문입니다."
        }
    """;

    public static final String ORDER_CREATE_SUCCESS = """
       {
            "code": 200,
            "message": "SUCCESS",
            "data": {
                "orderId": 1,
                "userId": 1,
                "status": "pending",
                "createDate": "2024-04-02T12:00:00Z"
               }
        }
    """;

    public static final String EMPTY_ORDER_PRODUCT_LIST = """
       {
            "code": "400",
             "message": "상품 목록이 누락되었습니다. 필수 값을 확인해주세요."
        }
    """;

    public static final String PRODUCT_INVALID_ERROR = """
        {
            "code": "400",
             "message": "상품 ID가 유효하지 않습니다."
        }
    """;

    public static final String NOT_ENOUGH_BALANCE = """
        {
            "code": "400",
             "message":  "상품 재고가 부족합니다."
        }
    """;

    public static final String INVALID_COUPON = """
        {
            "code": "400",
             "message": "쿠폰이 유효하지 않습니다."
        }
    """;




}
