package kr.hhplus.be.server.domain.coupon;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.data.redis.core.SetOperations;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CouponIssuedUserPersistenceServiceTest {

    @InjectMocks
    private CouponIssuedUserPersistenceService couponIssuedUserPersistenceService;

    @Mock
    private CouponIssuedUserRepository couponIssuedUserRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private SetOperations<String, Object> setOperations;

    @Mock
    private CouponRepository couponRepository;

    private static final Logger logger = LogManager.getLogger(CouponIssuedUserPersistenceServiceTest.class);

    @Test
    void 쿠폰_발급_사용자_정보_영속화_성공() {
        Coupon coupon = mock(Coupon.class);
        when(coupon.getCouponId()).thenReturn(1L);

        when(couponRepository.findAllByStatus(Coupon.CouponStatus.ACTIVE))
                .thenReturn(List.of(coupon));

        when(redisTemplate.opsForSet()).thenReturn(setOperations);

        Set<Object> userIdsFromRedis = Set.of("1", "2");
        when(setOperations.members("coupon:1:users"))
                .thenReturn(userIdsFromRedis);

        couponIssuedUserPersistenceService.persistIssuedUserInfo();

        ArgumentCaptor<CouponIssuedUser> captor = ArgumentCaptor.forClass(CouponIssuedUser.class);
        verify(couponIssuedUserRepository).save(captor.capture());

        CouponIssuedUser saved = captor.getValue();
        assertEquals(Long.valueOf(1L), saved.getCouponId());

        ObjectMapper mapper = new ObjectMapper();
        try {
            List<Long> userIds = mapper.readValue(saved.getUserIdsJson(), new TypeReference<List<Long>>() {});
            assertTrue(userIds.containsAll(List.of(1L, 2L)));
        } catch (Exception e) {
            logger.warn("userIdsJson 파싱 실패");
        }
    }
}
