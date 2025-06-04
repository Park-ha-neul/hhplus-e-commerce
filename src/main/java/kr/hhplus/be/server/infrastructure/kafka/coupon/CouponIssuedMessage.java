package kr.hhplus.be.server.infrastructure.kafka.coupon;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CouponIssuedMessage {
    private final Long userId;
    private final Long couponId;
    private final LocalDateTime endDate;

    @JsonCreator
    public CouponIssuedMessage(
            @JsonProperty("userId") Long userId,
            @JsonProperty("couponId") Long couponId,
            @JsonProperty("endDate") LocalDateTime endDate
    ) {
        this.userId = userId;
        this.couponId = couponId;
        this.endDate = endDate;
    }
}
