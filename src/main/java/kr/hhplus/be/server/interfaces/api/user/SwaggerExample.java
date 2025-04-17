package kr.hhplus.be.server.interfaces.api.user;

public class SwaggerExample {
    public static final String GET_POINT_SUCCESS = """
        {
            "code": 200,
            "message": "SUCCESS",
            "data": {
                "userId": 1,
                "point": 5000,
                "createDate": "2024-04-02T12:00:00Z",
                "updateDate": "2024-04-02T12:30:00Z"
            }
        }
    """;

    public static final String GET_POINT_NEW_USER = """
        {
            "code": 200,
            "message": "SUCCESS",
            "data": {
                "userId": 1,
                "point": 0,
                "createDate": "2024-04-02T12:00:00Z",
                "updateDate": "2024-04-02T12:30:00Z"
            }
        }
    """;

    public static final String ERROR_USER_NOT_FOUND = """
        {
            "code": "400",
            "message": "정의되지 않은 사용자입니다."
        }
    """;

    public static final String SERVER_ERROR = """
        {
            "code": "500",
             "message": "서버 에러"
        }
    """;
}
