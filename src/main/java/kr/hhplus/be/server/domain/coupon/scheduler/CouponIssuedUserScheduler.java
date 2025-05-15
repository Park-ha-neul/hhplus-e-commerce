package kr.hhplus.be.server.domain.coupon.scheduler;

import kr.hhplus.be.server.domain.coupon.CouponIssuedUserPersistenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CouponIssuedUserScheduler {

    private final CouponIssuedUserPersistenceService persistenceService;

    @Scheduled(cron = "0 0 * * * *")
    public void syncIssuedUsersToDb() {
        log.info("쿠폰 발급 사용자 정보 DB 영속화 시작");
        persistenceService.persistIssuedUserInfo();
        log.info("쿠폰 발급 사용자 정보 DB 영속화 완료");
    }
}
