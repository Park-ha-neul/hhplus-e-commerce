package kr.hhplus.be.server.domain.coupon;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponIssuedUserPersistenceService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final CouponRepository couponRepository;
    private final CouponIssuedUserRepository couponIssuedUserRepository;

    public void persistIssuedUserInfo() {
        List<Coupon> activeCoupons = couponRepository.findAllByStatus(Coupon.CouponStatus.ACTIVE);

        for (Coupon coupon : activeCoupons) {
            String redisKey = "coupon:" + coupon.getCouponId() + ":users";
            Set<Object> userIdObjects = redisTemplate.opsForSet().members(redisKey);

            if (userIdObjects == null || userIdObjects.isEmpty()) continue;

            List<Long> userIds = userIdObjects.stream()
                    .map(Object::toString)
                    .map(Long::valueOf)
                    .collect(Collectors.toList());

            CouponIssuedUser summary = new CouponIssuedUser(coupon.getCouponId(), userIds);
            couponIssuedUserRepository.save(summary);
        }
    }
}
