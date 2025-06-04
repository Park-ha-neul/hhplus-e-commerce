package kr.hhplus.be.server.interfaces.api.order;

import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.infrastructure.kafka.PaymentCompletedExternalPlatformMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mock")
@Tag(name = "📌 외부 전달받는 테스트용 컨트롤러", description = "주문 정보 외부 데이터 플랫폼 전송")
public class MockApiController {

    private static final Logger log = LoggerFactory.getLogger(MockApiController.class);

    @PostMapping("/order")
    public ResponseEntity<Void> receiveOrder(@RequestBody PaymentCompletedExternalPlatformMessage message) {
        log.info("📦 Mock API received order message: {}", message);
        return ResponseEntity.ok().build();
    }
}
