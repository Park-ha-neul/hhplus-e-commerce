package kr.hhplus.be.server.domain.user;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    public boolean isAdmin(User user){
        return user.isAdmin();
    }
}
