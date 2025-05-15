package kr.hhplus.be.server.domain.coupon;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Entity
@Table(name = "coupon_issued_users")
@AllArgsConstructor
@NoArgsConstructor
public class CouponIssuedUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long couponId;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String userIdsJson;

    private LocalDateTime issuedAt;

    public CouponIssuedUser(Long couponId, List<Long> userIds) {
        this.couponId = couponId;
        this.userIdsJson = toJson(userIds);
        this.issuedAt = LocalDateTime.now();
    }

    private String toJson(List<Long> userIds) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(userIds);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("userIds JSON 변환 실패", e);
        }
    }
}
