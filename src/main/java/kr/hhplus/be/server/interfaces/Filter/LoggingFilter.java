package kr.hhplus.be.server.interfaces.Filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import java.io.IOException;

public class LoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 요청 로그 기록
        logger.info("Request URI: {}", httpRequest.getRequestURI());
        logger.info("Request Method: {}", httpRequest.getMethod());
        logger.info("Request Parameters: {}", request.getParameterMap());

        // 실제 요청을 처리한 후 응답 처리
        chain.doFilter(request, response);

        // 응답 로그 기록
        logger.info("Response Status: {}", httpResponse.getStatus());
    }
}
