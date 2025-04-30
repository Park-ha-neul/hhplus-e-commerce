package kr.hhplus.be.server.interfaces.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.hhplus.be.server.domain.user.UserResult;
import kr.hhplus.be.server.domain.user.UserService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminInterceptor implements HandlerInterceptor {


    private final UserService userService;

    public AdminInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userIdParam = request.getParameter("userId");

        if (userIdParam == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("userId가 필요합니다.");
            return false;
        }

        Long userId = Long.parseLong(userIdParam);
        UserResult user = userService.getUser(userId);

        if (!user.isAdmin()) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("관리자 권한이 필요합니다.");
            return false;
        }

        return true;
    }
}
