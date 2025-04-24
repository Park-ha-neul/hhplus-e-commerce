package kr.hhplus.be.server.support;

import lombok.Getter;

@Getter

public class CustomApiResponse<T>{
    private final int code;
    private final String message;
    private final T data;

    public CustomApiResponse(int code, String message) {
        this(code, message, null);  // 기본적으로 data는 null
    }

    public CustomApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> CustomApiResponse<T> success(String message, T data){
        return new CustomApiResponse<>(ResponseCode.SUCCESS, message, data);
    }

    public static CustomApiResponse<Void> success(String message) {
        return new CustomApiResponse<>(ResponseCode.SUCCESS, message);
    }

    public static <T> CustomApiResponse<T> create(String message, T data){
        return new CustomApiResponse<>(ResponseCode.CREATE_SUCCESS, message, data);
    }

    public static CustomApiResponse<Void> create(String message) {
        return new CustomApiResponse<>(ResponseCode.CREATE_SUCCESS, message);
    }

    public static CustomApiResponse<Void> badRequest(String message) {
        return new CustomApiResponse<>(ResponseCode.BAD_REQUEST, message);
    }

    public static CustomApiResponse<Void> forbidden(String message) {
        return new CustomApiResponse<>(ResponseCode.FORBIDDEN, message);
    }

    public static CustomApiResponse<Void> notFound(String message) {
        return new CustomApiResponse<>(ResponseCode.NOT_FOUND, message);
    }

    public static CustomApiResponse<Void> duplicateError(String message) {
        return new CustomApiResponse<>(ResponseCode.DUPLICATE_ERROR, message);
    }

    public static CustomApiResponse<Void> internalError(String message) {
        return new CustomApiResponse<>(ResponseCode.SERVER_ERROR, message);
    }

}
