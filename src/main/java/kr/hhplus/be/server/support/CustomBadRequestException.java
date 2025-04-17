package kr.hhplus.be.server.support;

public class CustomBadRequestException extends RuntimeException{
    public CustomBadRequestException(String message){
        super(message);
    }
}
