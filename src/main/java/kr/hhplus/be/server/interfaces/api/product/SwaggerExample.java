package kr.hhplus.be.server.interfaces.api.product;

public class SwaggerExample {
    public static final String GET_PRODUCTS_SUCCESS = """
        {
            "code": 200,
            "message": "SUCCESS",
            "data": [{
                 "productId": 1,
                 "productName": "A 상품",
                 "description": "좋은 상품입니다,",
                 "quantity": 3,
                 "status": "판매중",
                 "price": 5000,
                 "createDate": "2024-04-02T12:00:00Z",
                 "updateDate": "2024-04-02T12:30:00Z"
               },{}...]
        }
    """;

    public static final String SERVER_ERROR = """
        {
            "code": "500",
             "message": "서버 에러"
        }
    """;

    public static final String GET_PRODUCT_DETAIL_SUCCESS = """
        {
            "code": 200,
            "message": "SUCCESS",
            "data": {
                 "productId": 1,
                 "productName": "A 상품",
                 "description": "좋은 상품입니다,",
                 "quantity": 3,
                 "status": "판매중",
                 "price": 5000,
                 "createDate": "2024-04-02T12:00:00Z",
                 "updateDate": "2024-04-02T12:30:00Z"
               }
        }
    """;

    public static final String PRODUCT_NOT_FOUND = """
        {
            "code": "400",
             "message": "존재하지 않는 상품입니다."
        }
    """;

    public static final String CREATE_PRODUCT_SUCCESS = """
        {
            "code": 200,
            "message": "SUCCESS",
            "data": {
                 "productId": 1,
                 "productName": "A 상품",
                 "description": "좋은 상품입니다,",
                 "quantity": 3,
                 "status": "판매중",
                 "price": 5000,
                 "createDate": "2024-04-02T12:00:00Z",
                 "updateDate": "2024-04-02T12:30:00Z"
               }
        }
    """;

    public static final String EMPTY_PRODUCT_NAME = """
        {
            "code": "400",
             "message": "상품명이 누락되었습니다. 필수 값을 확인해주세요."
        }
    """;

    public static final String EMPTY_PRODUCT_COUNT = """
        {
            "code": "400",
             "message": "상품명이 누락되었습니다. 필수 값을 확인해주세요."
        }
    """;

    public static final String EMPTY_PRODUCT_PRICE = """
        {
            "code": "400",
             "message": "상품명이 누락되었습니다. 필수 값을 확인해주세요."
        }
    """;

    public static final String GET_POPULAR_PRODUCTS = """
        {
            "code": 200,
            "message": "SUCCESS",
            "data": [{
                 "productId": 1,
                 "productName": "A 상품",
                 "description": "좋은 상품입니다,",
                 "quantity": 3,
                 "status": "판매중",
                 "price": 5000,
                 "createDate": "2024-04-02T12:00:00Z",
                 "updateDate": "2024-04-02T12:30:00Z"
               },{}...]
        }
    """;

}
