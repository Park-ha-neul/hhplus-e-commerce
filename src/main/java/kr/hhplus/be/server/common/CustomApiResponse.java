package kr.hhplus.be.server.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class CustomApiResponse {
    private int code;
    private String message;
    private Object data;

    public static CustomApiResponse success(Map<String, Object> data){
        return new CustomApiResponse(200, "SUCCESS", data);
    }

    public static CustomApiResponse success(List<Map<String, Object>> data){
        return new CustomApiResponse(200, "SUCCESS", data);
    }

    public static CustomApiResponse error(int code, String message){
        return new CustomApiResponse(code, message, null);
    }
}
