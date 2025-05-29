package kr.hhplus.be.server.infrastructure.kafka.coupon;

import kr.hhplus.be.server.domain.coupon.UserCouponService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

@Component
public class CouponIssuedConsumer {

    private final UserCouponService userCouponService;
    private final Logger log = LoggerFactory.getLogger(CouponIssuedConsumer.class);

    public CouponIssuedConsumer(UserCouponService userCouponService) {
        this.userCouponService = userCouponService;
    }

    @KafkaListener(topicPattern = "inside\\.coupon\\..*", groupId = "coupon-issue")
    public void handleIssue(CouponIssuedMessage message) {
        try{
            log.info("📥 Kafka 수신: couponId={}, userId={}", message.getCouponId(), message.getUserId());
            userCouponService.issueToUser(message.getUserId(), message.getCouponId());
        } catch (Exception e){
            log.error("쿠폰 발급 처리 실패: userId={}, couponId={}", message.getUserId(), message.getCouponId());
            throw new IllegalArgumentException("쿠폰 발급 처리 실패 : " + e.getMessage());
        }
    }
}
