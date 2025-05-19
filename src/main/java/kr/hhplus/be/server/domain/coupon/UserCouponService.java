package kr.hhplus.be.server.domain.coupon;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserCouponService {

    private final UserCouponRepository userCouponRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final RedissonClient redissonClient;
    private final RedisTemplate<String, Object> redisTemplate;

    public UserCouponResult issueWithLock(Long userId, Long couponId){
        String lockKey = "lock:coupon:" + couponId;
        RLock lock = redissonClient.getLock(lockKey);

        int retry = 0;
        int maxRetries = 50;
        while(retry++ < maxRetries){
            try{
                if(lock.tryLock(0, 1, TimeUnit.SECONDS)) {
                    try{
                        return issue(userId, couponId);
                    }finally {
                        if(lock.isHeldByCurrentThread()){
                            lock.unlock();
                        }
                    }
                } else {
                    Thread.sleep(10);
                }
            }catch (InterruptedException e){
                throw new RuntimeException("락 대기 중 인터럽트 발생", e);
            }
        }
        throw new RuntimeException("쿠폰 발급 실패: 락 획득에 반복적으로 실패하였습니다.");
    }

    @Transactional
    public UserCouponResult issue(Long userId, Long couponId){
        String issuedSetKey = "coupon:" + couponId + ":users";
        String stockListKey = "coupon:" + couponId;
        String userKey = String.valueOf(userId);

        Long added = redisTemplate.opsForSet().add(issuedSetKey, userKey);
        if (added == null || added == 0) {
            throw new IllegalArgumentException("이미 발급된 쿠폰입니다.");
        }

        String popped = (String) redisTemplate.opsForList().leftPop(stockListKey);
        if (popped == null) {
            redisTemplate.opsForSet().remove(issuedSetKey, userKey);
            throw new IllegalArgumentException(ErrorCode.COUPON_ISSUED_EXCEED.getMessage());
        }

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.COUPON_NOT_FOUND.getMessage()));

        if(coupon.getStatus() != Coupon.CouponStatus.ACTIVE){
            redisTemplate.opsForSet().remove(issuedSetKey, userKey);
            redisTemplate.opsForList().leftPush(stockListKey, popped);
            throw new IllegalArgumentException(ErrorCode.INACTIVE_COUPON.getMessage());
        }

        UserCoupon userCoupon = UserCoupon.create(userId, couponId);
        userCouponRepository.save(userCoupon);
        coupon.increaseIssuedCount();
        couponRepository.save(coupon);

        redisTemplate.opsForSet().add(issuedSetKey, String.valueOf(userId));

        // TTL 설정
        long secondsToExpire = Duration.between(LocalDateTime.now(), coupon.getEndDate()).getSeconds();
        redisTemplate.expire(issuedSetKey, secondsToExpire, TimeUnit.SECONDS);

        return UserCouponResult.of(userCoupon);
    }

    public List<UserCouponResult> getUserCoupons(Long userId, UserCoupon.UserCouponStatus status){
        if(status == null){
            List<UserCoupon> userCoupons = userCouponRepository.findByUserId(userId);
            return userCoupons.stream()
                    .map(UserCouponResult::of)
                    .collect(Collectors.toList());
        } else{
            List<UserCoupon> userCoupons = userCouponRepository.findByUserIdAndStatus(userId, status);
            return userCoupons.stream()
                    .map(UserCouponResult::of)
                    .collect(Collectors.toList());
        }
    }

    public UserCoupon getUserCoupon(Long userCouponId){
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.USER_COUPON_NOT_FOUND.getMessage()));

        return userCoupon;
    }

    public void useWithLock(Long userCouponId){
        String lockKey = "lock:userCoupon:" + userCouponId;
        RLock lock = redissonClient.getLock(lockKey);

        try{
            if(lock.tryLock(0, 1, TimeUnit.SECONDS)) {
                try{
                    use(userCouponId);
                }finally {
                    if(lock.isHeldByCurrentThread()){
                        lock.unlock();
                    }
                }
            } else {
                throw new RuntimeException("Lock 획득 실패");
            }
        }catch (InterruptedException e){
            throw new RuntimeException("락 대기 중 인터럽트 발생", e);
        }
    }

    @Transactional
    public void use(Long userCouponId){
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.USER_COUPON_NOT_FOUND.getMessage()));

        if (userCoupon.getStatus() == UserCoupon.UserCouponStatus.USED) {
            throw new IllegalArgumentException(ErrorCode.ALREADY_USED_COUPON.getMessage());
        }

        userCoupon.use();

        userCouponRepository.save(userCoupon);
    }
}
